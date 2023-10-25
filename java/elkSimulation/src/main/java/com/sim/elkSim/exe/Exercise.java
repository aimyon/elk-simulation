package com.sim.elkSim.exe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sim.elkSim.core.geo.Coordinate;
import com.sim.elkSim.entities.IEntity;
import com.sim.elkSim.entities.types.IEntityType;
import com.sim.elkSim.entities.weapons.IWeapon;
import com.sim.elkSim.task.ITask;
import com.sim.elkSim.core.utils.JsonUtils;
import com.sim.elkSim.entities.sensors.ISensor;

public class Exercise {

    private String simu_addr;
    private int simu_port;

    private Coordinate map_center;

    private Map<String, IWeapon> weaponDB;
    private Map<String, ISensor> sensorDB;
    private Map<String, IEntityType> entityTypeDB;

    private Map<String, IEntity> blueTeam;
    private Map<String, IEntity> redTeam;
    private Map<String, IEntity> whiteTeam;


    private Map<String, ITask> blueTask;
    private Map<String, ITask> redTask;
    private Map<String, ITask> whiteTask;

    public Exercise() {

        Object simu_params[] = JsonUtils.loadServerParams();
        simu_addr = (String) simu_params[0];
        simu_port = (int) simu_params[1];
        map_center = (Coordinate) simu_params[2];

        //类型json文件存放在/resource/model中
        weaponDB = JsonUtils.loadWeapons();
        sensorDB = JsonUtils.loadSensors();
        // 放入对应静态和动态本体
        entityTypeDB = JsonUtils.loadEntityTypes();

        blueTeam = JsonUtils.loadEntities("B");
        redTeam = JsonUtils.loadEntities("R");
        whiteTeam = JsonUtils.loadEntities("W");

        blueTask = JsonUtils.loadTasks("B");
        redTask = JsonUtils.loadTasks("R");
        whiteTask = JsonUtils.loadTasks("W");

    }

    public String getSimuAddr() {
        return simu_addr;
    }

    public int getSimuPort() {
        return simu_port;
    }

    public Coordinate getMapcenter() {
        return map_center;
    }

    public Map<String, IWeapon> getWeaponDB() {
        return weaponDB;
    }

    public Map<String, ISensor> getSensorDB() {
        return sensorDB;
    }

    public Map<String, IEntityType> getEntityTypeDB() {
        return entityTypeDB;
    }

    public Map<String, IEntity> getBlueTeam() {
        return blueTeam;
    }

    public Map<String, IEntity> getRedTeam() {
        return redTeam;
    }

    public Map<String, IEntity> getWhiteTeam() {
        return whiteTeam;
    }

    public Map<String, ITask> getBlueTask() {
        return this.blueTask;
    }

    public Map<String, ITask> getRedTask() {
        return this.redTask;
    }

    public Map<String, ITask> getWhiteTask() {
        return this.whiteTask;
    }


    public Map<String, IEntity> getEntityDB() {
        Map<String, IEntity> entDB = new ConcurrentHashMap<>();
        entDB.putAll(redTeam);
        entDB.putAll(blueTeam);
        entDB.putAll(whiteTeam);
        return entDB;
    }

    public Map<String, ITask> getTaskDB() {
        Map<String, ITask> taskDB = new ConcurrentHashMap<>();
        taskDB.putAll(redTask);
        taskDB.putAll(blueTask);
        taskDB.putAll(whiteTask);
        return taskDB;
    }

    public void removeNode(IEntity ent, String force) {
        if (force.equals("B")) {
            this.blueTeam.remove(ent.getId());
        } else if (force.equals("R")) {
            this.redTeam.remove(ent.getId());

        }
    }

    public boolean end() {
        // 当本体中内容全部销毁时，推演结束
        if (redTeam.size() == 0 && blueTeam.size() == 0 && whiteTeam.size() == 0)
            return true;
        else
            return false;
    }


}
