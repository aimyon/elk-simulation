package com.sim.elkSim.core.engines;

import com.sim.elkSim.core.geo.Coordinate;
import com.sim.elkSim.core.utils.GeoUtils;
import com.sim.elkSim.entities.IEntity;
import com.sim.elkSim.entities.IEntity.STATUS;
import com.sim.elkSim.entities.types.MobilityEntity;
import com.sim.elkSim.task.ITask;

import static com.sim.elkSim.sim.SimulationManager.entDB;
import static com.sim.elkSim.sim.SimulationManager.taskDB;

public class MoveEngine {
    private static final double AIR_HOR_ERR = 300;  // 可容忍的误差上下限
    private static final double AIR_VERT_ERR = 304; // 100ft

    private static final double NAVY_HOR_ERR = 300;
    private static final double NAVY_VERT_ERR = 304; // 100ft

    private static final double ARMY_HOR_ERR = 300;
    private static final double ARMY_VERT_ERR = 304; // 100ft

    // 水平坐标错误标限
    public static double horizontalErrorDefined(String force) {
        double hor_error = 0;

        if (force.equals("AF")) {
            hor_error = AIR_HOR_ERR;
        } else if (force.equals("LF")) {
            hor_error = ARMY_HOR_ERR;
        } else if (force.equals("SS")) {
            hor_error = NAVY_HOR_ERR;
        } else if (force.equals("SB")) {
            hor_error = NAVY_HOR_ERR;
        }

        return hor_error;
    }

    // 竖直坐标错误标限
    public static double verticalErrorDefined(String force) {
        double vert_error = 0;

        if (force.equals("AF")) {
            vert_error = AIR_VERT_ERR;
        } else if (force.equals("LF")) {
            vert_error = ARMY_VERT_ERR;
        } else if (force.equals("SS")) {
            vert_error = NAVY_VERT_ERR;
        } else if (force.equals("SB")) {
            vert_error = NAVY_VERT_ERR;
        }

        return vert_error;

    }

    public static Coordinate move(IEntity entity, Coordinate target_position, long time_step) {

        double distance_mt_vertical = diff_vertCal(entity, time_step);
        double distance_mt_horiz = diff_horizCal(entity, time_step);

        double vert_difference = target_position.getAltitude() - entity.getCurrentPosition().getAltitude();
        double hor_difference = GeoUtils.calculateHorizontalDistanceMeters(entity.getCurrentPosition(), target_position);

        if (vert_difference > 0) {                                                // 当前接近目标高度
            distance_mt_horiz = distance_mt_horiz - distance_mt_horiz * 0.2;
            if (vert_difference < distance_mt_vertical) {
                distance_mt_vertical = vert_difference;
            }
        } else if (vert_difference < 0) {
            if (Math.abs(vert_difference) < distance_mt_vertical) {
                distance_mt_vertical = Math.abs(vert_difference);
            }
            distance_mt_vertical = (-1) * distance_mt_vertical;
        } else {
            distance_mt_vertical = 0;
        }

        if (hor_difference <= distance_mt_horiz) {
            distance_mt_horiz = hor_difference;
        }

        double bearing = GeoUtils.calculateBearing(entity.getCurrentPosition(), target_position);       // 计算方位角,起始位置至目标位置方位角指向

        Coordinate newPos = GeoUtils.calculateNewPosition(entity.getCurrentPosition(),
                distance_mt_horiz, distance_mt_vertical, bearing);

        return newPos;
    }

    private static double diff_horizCal(IEntity entity, long time_step) {
        double distance_mt_horiz = 0;
        MobilityEntity mob_entity = (MobilityEntity) entity.getEntType();
        distance_mt_horiz = mob_entity.getSpeed() / 1000 * time_step;

        return distance_mt_horiz;
    }

    private static double diff_vertCal(IEntity entity, long time_step) {
        double distance_mt_vertical = 0;

        MobilityEntity mob_entity = (MobilityEntity) entity.getEntType();
        double climb_rate = mob_entity.getClimbRate();

        if (climb_rate > 0) {
            if (entity.getForce().equals("AF") || entity.getForce().equals("SB")) {       // 军兵种AF(航空单位)和SB(水下舰艇)
                distance_mt_vertical = climb_rate / 1000 * time_step;
            }
        }

        return distance_mt_vertical;
    }

    // 沿着任务路线,到达目标地点即返回
    public static void update_pos(String task_id, long simu_cur_time, long time_step) {
        ITask task = taskDB.get(task_id);
        String target_id = task.getTarget_id();
        String executor_id = task.getExecutor_id();

        IEntity ent_executor = entDB.get(executor_id);

        double hor_error = horizontalErrorDefined(ent_executor.getForce());
        double vert_error = verticalErrorDefined(ent_executor.getForce());
        Coordinate newPosition;

        if (ent_executor.getStatus() == STATUS.DESTROYED) {                       // 执行者被摧毁则退出
            task.setStatus(ITask.STATUS.FAIL);
            return;
        }

        if (ent_executor.getStatus() == STATUS.READY) {                           // 当执行者被部署时将Ready状态修改为Executing状态
            ent_executor.setStatus(STATUS.EXECUTING);
        }

        if (ent_executor.getStatus() == STATUS.RETURNING) {
            if (!GeoUtils.isSamePosition(ent_executor.getCurrentPosition(), ent_executor.getInitialPosition(), hor_error, vert_error)) {
                newPosition = move(ent_executor, ent_executor.getInitialPosition(), time_step);
                ent_executor.setCurrentPosition(newPosition);
            } else {
                task.setStatus(ITask.STATUS.SUCCESSFUL);
                ent_executor.setStatus(STATUS.END);                 // end表示任务结束状态,当可部署时为available状态
                return;
            }
        } else if (ent_executor.getStatus() == STATUS.EXECUTING) {
            if (!target_id.equals("")) {
                if (GeoUtils.isSamePosition(ent_executor.getCurrentPosition(),
                        entDB.get(target_id).getCurrentPosition(),
                        hor_error, vert_error) || entDB.get(target_id).getStatus().toString().equals(STATUS.DESTROYED.toString())) {      // 到达target位置
                    System.out.println(ent_executor.getId() + " is returning!");
                    ent_executor.setStatus(STATUS.RETURNING);
                    newPosition = move(ent_executor, ent_executor.getInitialPosition(), time_step);
                } else {
                    if (task.curWaypoint() != null) {                                   // 存在当前路线点,则前往路线点
                        if (GeoUtils.isSamePosition(ent_executor.getCurrentPosition(), task.curWaypoint(), hor_error, vert_error))      // 到达路线点则切换下一个点
                            task.nextWaypoint();
                        if (task.curWaypoint() != null) {                                                                               // 存在下一个点,则下一点移动
                            newPosition = move(ent_executor, task.curWaypoint(), time_step);
                        } else {                                                                                                      // 不存在下一个点,则目标移动
                            newPosition = move(ent_executor, entDB.get(target_id).getCurrentPosition(), time_step);
                        }
                    } else {                                                         // 不存在当前路线点,则目标移动
                        newPosition = move(ent_executor, entDB.get(target_id).getCurrentPosition(), time_step);
                    }
                }
                ent_executor.setCurrentPosition(newPosition);
            } else {
                if (task.curWaypoint() != null) {
                    if (GeoUtils.isSamePosition(ent_executor.getCurrentPosition(), task.curWaypoint(), hor_error, vert_error))
                        task.nextWaypoint();
                }
                if (task.curWaypoint() != null) {
                    newPosition = move(ent_executor, task.curWaypoint(), time_step);
                } else {
                    if(task.getType().equals("PATROL")){
                        System.out.println(ent_executor.getId() + " is returning!");
                        ent_executor.setStatus(STATUS.RETURNING);
                        newPosition = move(ent_executor, ent_executor.getInitialPosition(), time_step);
                    } else{
                        newPosition = ent_executor.getCurrentPosition();
                    }
                }
                ent_executor.setCurrentPosition(newPosition);
            }
        }

        System.out.println(ent_executor.getId() + ":" + ent_executor.getCurrentPosition().getLatitude()
                + "," + ent_executor.getCurrentPosition().getLongitude()
                + "," + ent_executor.getCurrentPosition().getAltitude());

        entDB.put(executor_id, ent_executor);
        taskDB.put(task_id, task);
    }


}
