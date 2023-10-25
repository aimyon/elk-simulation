package com.sim.elkSim.sim;

import com.sim.elkSim.entities.IEntity;
import com.sim.elkSim.task.ITask;
import com.sim.elkSim.core.infra.Channel;
import com.sim.elkSim.core.utils.SocketUtils;
import com.sim.elkSim.exe.Exercise;
import com.sim.elkSim.time.Time;

import com.alibaba.fastjson.JSON;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SimulationManager extends Channel implements Runnable {
    private static SimulationManager serverInstance;

//    @Autowired
//    private ISocketIOService socketIOService;

    private boolean running = false;

    private Exercise exercise;                // 训练想定

    private long simu_start_time;
    private long simu_cur_time;

    private long real_time;
    private static Long[] time_step = new Long[]{1L, 5L, 10L, 100L, 500L, 1000L, 3000L, 5000L, 10000L, 30000L, 60000L,
                                                180000L, 300000L, 600000L, 1800000L, 3600000L, 7200000L, 14400000L,
                                                28800000L, 57600000L, 115200000L, 230400000L, 460800000L, 921600000L,
                                                1843200000L, 3686400000L, 7372800000L, 14745600000L};         // ms为单位

    private MovementManager moveManB;
    private EffectManager effManB;

    private List<IEntity> endEntList;        // 标记结束本体
    private List<ITask> endTaskList;

    public Integer step_rank;
    public static Map<String, IEntity> redTeam = new ConcurrentHashMap<>();
    public static Map<String, IEntity> blueTeam = new ConcurrentHashMap<>();
    public static Map<String, IEntity> whiteTeam = new ConcurrentHashMap<>();

    public static Map<String, ITask> redTask = new ConcurrentHashMap<>();          // 设置任务池
    public static Map<String, ITask> blueTask = new ConcurrentHashMap<>();
    public static Map<String, ITask> whiteTask = new ConcurrentHashMap<>();

    public static Map<String, IEntity> entDB = new ConcurrentHashMap<>();
    public static Map<String, ITask> taskDB = new ConcurrentHashMap<>();

    public static SimulationManager getInstance(Exercise exe) {
        if (serverInstance == null) {
            serverInstance = new SimulationManager(exe);
        }
        return serverInstance;
    }

    private SimulationManager(Exercise exe) {
        exercise = exe;
        super.subscriberLists = new ConcurrentHashMap<>();
        endEntList = new ArrayList<>();
        endTaskList = new ArrayList<>();

        blueTeam = exe.getBlueTeam();
        redTeam = exe.getRedTeam();
        whiteTeam = exe.getWhiteTeam();

        redTask = exe.getRedTask();
        blueTask = exe.getBlueTask();
        whiteTask = exe.getWhiteTask();

        taskDB = exe.getTaskDB();
        entDB = exe.getEntityDB();
    }

    public void init() {
        moveManB = MovementManager.getInstance();
        Thread thMvB = new Thread(moveManB, "movement manager");
        thMvB.start();

        effManB = EffectManager.getInstance();
        Thread thEffB = new Thread(effManB, "Effect Manager");
        thEffB.start();

        try {
            System.out.println("waiting for the other componnents...");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {                                    // 暂停时sleep thread
        // 可以写成Timer的形式, 间隔执行一次运行
        // scheduleExecutor
        this.running = true;

        simu_start_time = 0;                               // 从零时刻开始运行仿真过程
        simu_cur_time = simu_start_time;
        real_time = System.currentTimeMillis();            // 运行了多少毫秒

        /**
         *
         * 此处设置推演步长对应级别0~27
         *
         * */
        step_rank = 11; // 3min 1round

        // Task.java中备注设置,单线程针对小体量可运行
        Time simStartTime = new Time(simu_start_time);
        Time simCurTime = new Time(simu_cur_time);


        while (running) {
            // 调用推演引擎进行战场推演，推演内容分为两个部分：行动位移与作战影响

            // 该处修改任务当前状态
            List<ITask> taskList = new ArrayList<>(taskDB.values());

            taskList = dispatchTask(taskList);

            for (ITask task : taskList) {
                if(simu_cur_time<task.getSetTime()) continue;

                String task_id = task.getId();
                IEntity ent = entDB.get(task.getExecutor_id());

                if (task.getStatus().toString().equals(ITask.STATUS.INIT.toString())) {
                    if (ent.getAvailableStatus()) {
                        task.setStatus(ITask.STATUS.EXECUTING);
                        task.setStartTime(simu_cur_time);                                           // 设置任务开始时间
                        ent.setAvailableStatus(false);
                        moveManB.moveProcess(simu_start_time, simu_cur_time, time_step[step_rank], task_id);         // 位置推演
                        effManB.effectProcess(simu_start_time, simu_cur_time, time_step[step_rank], task_id);        // 状态推演
                    }
                } else if (task.getStatus().toString().equals(ITask.STATUS.EXECUTING.toString())) {
                    moveManB.moveProcess(simu_start_time, simu_cur_time, time_step[step_rank], task_id);
                    effManB.effectProcess(simu_start_time, simu_cur_time, time_step[step_rank], task_id);
                } else {                                                                        // successful or fail
                    this.endTaskList.add(task);
                    taskDB.remove(task_id, task);
                }
            }

            adjustSim();

            if (exercise.end()) {
                running = false;
            }

            try {
                simu_cur_time = simu_cur_time + time_step[step_rank];                        // 对应1单位时间进行1次推演
                real_time = System.currentTimeMillis();
                System.out.println("time " + simu_cur_time/1000 + " sec...");
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 使用web socket推送给前端,初始化由前台调一次数据,后续由websocket向前台推送(前台监听)
            try {
                SocketUtils.sendMsg(entDB.values());
                // SocketUtils.sendMsgString("msg",entDB);
                System.out.println("send msg " + JSON.toJSONString(entDB.values()));
                // WebSocketServer.sendMessage(JSONObject.toJSON(entDB).toString(), null);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

    }


    // 删除被摧毁点<考虑暂不删除>
    private void adjustSim() {
        List<IEntity> blueL = new ArrayList<>(exercise.getBlueTeam().values());
        for (IEntity ent : blueL) {
            IEntity.STATUS status = ent.getStatus();
            if (status == IEntity.STATUS.DESTROYED ||
                    status == IEntity.STATUS.END) {
                this.endEntList.add(ent);
                exercise.removeNode(ent, "B");

            }
        }
        List<IEntity> redL = new ArrayList<>(exercise.getRedTeam().values());

        for (IEntity ent : redL) {
            IEntity.STATUS status = ent.getStatus();
            if (status == IEntity.STATUS.DESTROYED ||
                    status == IEntity.STATUS.END) {
                this.endEntList.add(ent);
                exercise.removeNode(ent, "R");
            }
        }
    }


    private List<ITask> dispatchTask(List<ITask> taskList) {
        // 进行任务调度
        // 任务布置时间调度 优先级调度 事件三段扫描
        taskList.sort(Comparator.comparingInt(t -> (int) t.getSetTime()));  // 先进先执行调度
        return taskList;
    }
}
