package com.sim.elkSim.sim;

import com.sim.elkSim.core.engines.MoveEngine;
import com.sim.elkSim.entities.IEntity;
import com.sim.elkSim.entities.types.MobilityEntity;
import com.sim.elkSim.task.ITask;

public class MovementManager implements Runnable {

    private boolean running = false;

    private static MovementManager instance;

    private MovementManager() {
        running = true;
    }

    public static MovementManager getInstance() {
        if (instance == null) {
            instance = new MovementManager();
        }

        return instance;

    }

    @Override
    public void run() {
        while (running) {

        }

    }

    public void close() {
        this.running = false;
    }


    public void moveProcess(long simu_start_time, long simu_cur_time, long time_step, String task_id) {      // 对应阵营的本体和任务

        ITask task = SimulationManager.taskDB.get(task_id);
        IEntity executor = SimulationManager.entDB.get(task.getExecutor_id());
        if (executor.getEntType() instanceof MobilityEntity) {
            MoveEngine.update_pos(task_id, simu_cur_time, time_step);
        }
    }
}


