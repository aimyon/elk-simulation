package com.sim.elkSim.task;

import java.util.List;

import com.sim.elkSim.core.geo.Coordinate;

public interface ITask {
    // 任务状态:创建 执行 结束(成功|失败)
    public enum STATUS {
        INIT, EXECUTING, FAIL, SUCCESSFUL
    }

    public String getId();

    public STATUS getStatus();

    public void setStatus(STATUS status);

    public String getTarget_id();

    public String getExecutor_id();

    public String getType();

    public double getEffectiveness();       // 获取任务效率系数(保留/不保留)

    public List<Coordinate> getRoutes();

    public boolean isIs_time_on_target();

    public long getTime_on_target();

    public List<String> getRequiredSensor();

    public List<String> getRequiredWeapons();

    public void setIs_time_on_target(boolean is_time_on_target);

    public void setTime_on_target(long time_on_target);

    public void addRoute(Coordinate coord);

    public void addRequiredWeapon(String wp);

    public void addRequiredSensor(String sens);

    public void setStartTime(long startTime);

    public long getStartTime();

    public long getSetTime();

    public boolean hasMoreWaypoints();

    public Coordinate curWaypoint();

    public void nextWaypoint();

    public void update_executor();    // 本体状态更新

    public void update_task();  // 任务状态变更

//    public void update_time();// 时间更新
}
