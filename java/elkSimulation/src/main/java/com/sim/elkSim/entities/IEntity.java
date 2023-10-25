package com.sim.elkSim.entities;

import java.util.List;

import com.sim.elkSim.entities.weapons.IWeapon;
import com.sim.elkSim.core.geo.Coordinate;
import com.sim.elkSim.entities.sensors.ISensor;
import com.sim.elkSim.entities.types.IEntityType;

public interface IEntity {

    // 创建本体对应状态的枚举：待命, 执行任务中, 返回, 被摧毁, 结束
    enum STATUS {
        READY, EXECUTING, RETURNING, DESTROYED, END
    }

    public String getId();

    public long getBehaviorMode();

    public String getTeam();

    public String getForce();

    public double getCost();

    public IEntityType getEntType();

    public String getType();

    public void setStatus(IEntity.STATUS status);

    public IEntity.STATUS getStatus();

    public void setHealth(double health);

    public double getHealth();

    public void setCurrentPosition(Coordinate currentPos);

    public Coordinate getCurrentPosition();

    public Coordinate getInitialPosition();

    public List<ISensor> getSensors();

    public void addSensor(ISensor sensor);

    public void addWeapon(IWeapon weapon, int qtd);

    public int decrementWeapon(IWeapon weapon);

    public int getWeaponQuantity(IWeapon weapon);

    public List<IWeapon> getWeaponList();

    public boolean getDetectedStatus();

    public void setDetectedStatus(boolean if_detected);

    public boolean getAvailableStatus();

    public void setAvailableStatus(boolean if_available);
}
