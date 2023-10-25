package com.sim.elkSim.order;

import com.sim.elkSim.core.geo.Coordinate;
import com.sim.elkSim.entities.IEntity;

import java.util.List;
import java.util.Map;

// 支持多执行者指令下达
public interface IOrder {
    // 指令的基本意图：进攻、防卫、侦察(固定设施开启雷达、移动装备进行侦察)、巡逻、补给、机动
    public enum INTENTION {
        ATTACK, DEFENCE, DETECT, PATROL, SUPPLY, MOVE
    }

    public String getId();

    public double getOrderTime();

    public void setOrderType(INTENTION intention);

    public INTENTION getOrderType();

    public IEntity getTarget();

    public List<IEntity> getExecutors();

    public void setRoutes(Map<IEntity,List<Coordinate>> mul_routes);

    public Map<IEntity,List<Coordinate>> getRoutes();

}
