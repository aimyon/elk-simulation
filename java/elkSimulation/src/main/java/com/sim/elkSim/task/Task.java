package com.sim.elkSim.task;

import java.util.ArrayList;
import java.util.List;

import com.sim.elkSim.core.geo.Coordinate;
import com.sim.elkSim.time.Time;

public class Task implements ITask {

    protected String id;
    protected double effectiveness;
    protected String type;                          // 任务意图 'ATTACK', 'DEFENCE', 'DETECT', 'PATROL', 'SUPPLY', 'MOVE'
    protected STATUS status;                        // 任务状态 INIT, EXECUTING, FAIL, SUCCESSFUL
    protected List<Coordinate> route;               // 行进路线及节点

    protected String target_id;
    protected String executor_id;

    // 判断当前本体是否到达目标地点
    protected boolean is_time_on_target;
    protected long time_on_target;

    protected long set_time;
    protected long start_time;                    // 任务开始时间

    protected Time time;

    protected List<String> requiredSensor;          // 设置最低传感器要求
    protected List<String> requiredWeapons;         // 设置最低武器要求

    // 从entity分离同时向任务中添加执行者executor
    public Task(String id, String executor_id, String target_id,
                long set_time, String type, double effectiveness) {
        super();
        this.id = id;

        this.target_id = target_id;
        this.executor_id = executor_id;

        this.type = type;
        this.effectiveness = effectiveness;
        this.route = new ArrayList<>();

        this.requiredSensor = new ArrayList<>();
        this.requiredWeapons = new ArrayList<>();

        this.set_time = set_time;
        this.status = STATUS.INIT;

        time = new Time(set_time);
    }

    public String getId() {
        return id;
    }

    public STATUS getStatus() {
        return status;
    }                           // 判断任务状态,任务是否结束

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public String getTarget_id() {
        return target_id;
    }

    public String getExecutor_id() {
        return executor_id;
    }

    public String getType() {
        return type;
    }

    public double getEffectiveness() {
        return effectiveness;
    }

    public List<Coordinate> getRoutes() {
        return route;
    }

    public boolean isIs_time_on_target() {
        return is_time_on_target;
    }

    public long getTime_on_target() {
        return time_on_target;
    }

    public List<String> getRequiredSensor() {
        return requiredSensor;
    }

    public List<String> getRequiredWeapons() {
        return requiredWeapons;
    }

    public void setIs_time_on_target(boolean is_time_on_target) {
        this.is_time_on_target = is_time_on_target;
    }

    public void setTime_on_target(long time_on_target) {
        this.time_on_target = time_on_target;
    }

    public void addRoute(Coordinate coord) {
        this.route.add(coord);
    }

    public void addRequiredWeapon(String wp) {
        this.requiredWeapons.add(wp);
    }

    public void addRequiredSensor(String sens) {
        this.requiredSensor.add(sens);
    }

    public long getSetTime() {
        return set_time;
    }

    public void setStartTime(long start_time) {
        this.start_time = start_time;
    }

    public long getStartTime() {
        return start_time;
    }

    public boolean hasMoreWaypoints() {
        if (this.route == null || route.size() == 0)
            return false;
        else
            return true;
    }

    public Coordinate curWaypoint() {
        if (hasMoreWaypoints())
            return this.route.get(0);
        else return null;
    }

    public void nextWaypoint() {
        if (hasMoreWaypoints())
            this.route.remove(0);
    }

    // update_task()判断任务是否已经开始执行,更新任务列表中指令,添加新的指令(是否被探测到等),添加任务日志 new
    public void update_task() {

    }

    // 遍历任务以更新本体,update_entity()更新本体状态 new
    public void update_executor() {

    }
//    // update_time()更新时间,时间作为参数传给entity和order new
//    public void update_time(){
//
//    }
}
