package com.sim.elkSim.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sim.elkSim.core.geo.Coordinate;
import com.sim.elkSim.entities.types.IEntityType;
import com.sim.elkSim.entities.weapons.IWeapon;
import com.sim.elkSim.entities.sensors.ISensor;
//import ITask;
//import Task;

public class Entity implements IEntity {

    protected String id;                            // 前端可表示为label
    protected IEntity.STATUS status;                // INIT, RETURNING, DESTROYED, END
    protected long behavior_mode;
    protected String team;
    protected String force;
    protected String type;
    protected double cost;
    protected double health;

    protected IEntityType entType;

    protected Coordinate current_position;
    protected Coordinate start_position;

    protected List<ISensor> sensorL;
    protected Map<IWeapon, Integer> weaponDb;

    protected boolean detected_status;
    protected boolean available_status;

//    protected Coordinate target_position;
//    protected List<Coordinate> currentRoute;
//    protected Task currentTask;
//    protected List<ITask> taskL;

    public Entity(String id, long behavior_mode, String team, String force, double cost, String type,
                  IEntityType entType, Coordinate current_position, Coordinate start_position) {
        super();
        this.id = id;
        this.status = IEntity.STATUS.READY;
        this.behavior_mode = behavior_mode;         // 0 静默 1 自由攻击 2 指定攻击
        this.team = team;           // 所属阵营
        this.force = force;         // 所属军兵种
        this.cost = cost;           // ?
        this.health = 1.0;
        this.type = type;
        this.entType = entType;     // 本体类型, IEntity类中定义

        this.current_position = current_position;
        this.start_position = start_position;

        this.sensorL = new ArrayList<>();           //挂载各类传感器
        this.weaponDb = new HashMap<>();            // 格式上应为key：武器, value: 当前武器数量

        this.detected_status = false;
        this.available_status = true;               // 当前是否在可用状态,创建默认为可用状态
//      this.taskL = new ArrayList<>();

    }


    public String getId() {
        return id;
    }

    public IEntity.STATUS getStatus() {
        return status;
    }

    public void setStatus(IEntity.STATUS status) {
        this.status = status;
    }

    public long getBehaviorMode() {
        return behavior_mode;
    }

    public String getTeam() {
        return team;
    }

    public String getForce() {
        return force;
    }

    public double getCost() {
        return cost;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public IEntityType getEntType() {
        return entType;
    }

    public String getType() {
        return type;
    }

    public Coordinate getCurrentPosition() {
        return current_position;
    }

    public void setCurrentPosition(Coordinate currentPos) {
        this.current_position = currentPos;
    }

    public Coordinate getInitialPosition() {
        return this.start_position;
    }

    public List<ISensor> getSensors() {
        return sensorL;
    }           //获取传感器状态

    public void addSensor(ISensor sensor) {
        this.sensorL.add(sensor);
    }

    public void addWeapon(IWeapon weapon, int qtd) {
        this.weaponDb.put(weapon, Integer.valueOf(qtd));
    }       // 变量qtd表示武器弹药数量

    public int getWeaponQuantity(IWeapon weapon) {
        int qtd = this.weaponDb.get(weapon);
        return qtd;
    }

    // 计算武器系统弹药消耗
    public int decrementWeapon(IWeapon weapon) {
        int qtd = this.weaponDb.get(weapon);
        if (qtd > 0) {
            qtd = qtd - 1;
            this.weaponDb.replace(weapon, Integer.valueOf(qtd));
            return qtd;
        } else
            return 0;

    }

    public List<IWeapon> getWeaponList() {
        List<IWeapon> weaponList = new ArrayList<IWeapon>();
        weaponList.addAll(weaponDb.keySet());
        return weaponList;
    }

    public void setDetectedStatus(boolean if_detected) {
        this.detected_status = if_detected;
    }

    public boolean getDetectedStatus() {
        return detected_status;
    }

    public void setAvailableStatus(boolean if_available) {
        this.available_status = if_available;

    }

    public boolean getAvailableStatus() {
        return available_status;
    }

}
