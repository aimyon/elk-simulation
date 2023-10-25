package com.sim.elkSim.core.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sim.elkSim.core.geo.Coordinate;
import com.sim.elkSim.entities.Entity;
import com.sim.elkSim.entities.IEntity;
import com.sim.elkSim.entities.sensors.ISensor;
import com.sim.elkSim.entities.sensors.Sensor;
import com.sim.elkSim.entities.types.IEntityType;
import com.sim.elkSim.entities.types.MobilityEntity;
import com.sim.elkSim.entities.types.StaticEntity;
import com.sim.elkSim.entities.weapons.IWeapon;
import com.sim.elkSim.entities.weapons.Weapon;
import com.sim.elkSim.entities.weapons.dynamic_weapon.Missile;
import com.sim.elkSim.entities.weapons.dynamic_weapon.Torpedo;
import com.sim.elkSim.task.ITask;
import com.sim.elkSim.task.Task;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonUtils {

    public static Map<String, ISensor> loadSensors() {
        Map<String, ISensor> sensorDict = new ConcurrentHashMap<>();
        String sensorJson = Variables.SENSOR_FILE;

        try {
            Object obj = new JSONParser().parse(new FileReader(sensorJson));

            JSONArray joArr = (JSONArray) obj;

            for (Object aJoArr : joArr) {
                JSONObject jo = (JSONObject) aJoArr;
                String id = (String) jo.get("id");
                String domain = (String) jo.get("domain");
                long range = (long) jo.get("range");
                double precision = (double) jo.get("precision");
                double reliability = (double) jo.get("reliability");
                double intensity = (double) jo.get("intensity");

                ISensor sensor = new Sensor(id, domain, range, precision, reliability, intensity);
                sensorDict.put(id, sensor);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return sensorDict;
    }

    public static Map<String, IWeapon> loadWeapons() {
        Map<String, IWeapon> weaponDict = new ConcurrentHashMap<>();
        String weaponJson = Variables.WEAPON_FILE;

        try {
            Object obj = new JSONParser().parse(new FileReader(weaponJson));

            JSONArray joArr = (JSONArray) obj;

            for (Object aJoArr : joArr) {
                JSONObject jo = (JSONObject) aJoArr;
                String id = (String) jo.get("id");
                String domain = (String) jo.get("domain");
                long range = (long) jo.get("range");
                String type = (String) jo.get("type");
                double precision = (double) jo.get("precision");
                double reliability = (double) jo.get("reliability");
                double intensity = (double) jo.get("intensity");

                if (type.equals("MISSILE")) {
                    double missile_speed = (double) jo.get("speed");
                    double climb_rate = (double) jo.get("climb_rate");
                    Missile missile = new Missile(id, domain, range, type, precision, reliability, intensity, missile_speed, climb_rate);
                    weaponDict.put(id, missile);
                } else if (type.equals("TORPEDO")) {
                    double torpedo_speed = (double) jo.get("speed");
                    Torpedo torpedo = new Torpedo(id, domain, range, type, precision, reliability, intensity, torpedo_speed);
                    weaponDict.put(id, torpedo);

                } else {
                    IWeapon weapon = new Weapon(id, domain, range, type, precision, reliability, intensity);
                    weaponDict.put(id, weapon);
                }
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return weaponDict;
    }

    public static Map<String, IEntityType> loadEntityTypes() {
        Map<String, IEntityType> entityTypeDict = new ConcurrentHashMap<>();
        entityTypeDict.clear();
        // 放入
        entityTypeDict.putAll(loadStaticEntityTypes());
        entityTypeDict.putAll(loadMobilityEntityTypes());

        return entityTypeDict;
    }

    public static Map<String, IEntityType> loadStaticEntityTypes() {
        Map<String, IEntityType> entityTypeDict = new ConcurrentHashMap<>();
        String StaticTypeJson = Variables.ENT_STATIC_TYPE_FILE;

        try {
            Object obj = new JSONParser().parse(new FileReader(StaticTypeJson));

            JSONArray joArr = (JSONArray) obj;

            for (Object aJoArr : joArr) {
                JSONObject jo = (JSONObject) aJoArr;
                String id = (String) jo.get("id");
                String force = (String) jo.get("force");
                double lethality_factor = (double) jo.get("lethality_factor");
                double resilience_factor = (double) jo.get("resilience_factor");
                double vulnerability_factor = (double) jo.get("vulnerability_factor");

                IEntityType entType = new StaticEntity(id, force, lethality_factor, resilience_factor,
                        vulnerability_factor);
                entityTypeDict.put(id, entType);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return entityTypeDict;
    }

    public static Map<String, IEntityType> loadMobilityEntityTypes() {
        Map<String, IEntityType> entityTypeDict = new ConcurrentHashMap<>();
        String MobTypeJson = Variables.ENT_MOBILE_TYPE_FILE;

        try {
            Object obj = new JSONParser().parse(new FileReader(MobTypeJson));

            JSONArray joArr = (JSONArray) obj;

            for (Object aJoArr : joArr) {
                JSONObject jo = (JSONObject) aJoArr;
                String id = (String) jo.get("id");
                String force = (String) jo.get("force");
                double lethality_factor = (double) jo.get("lethality_factor");
                double resilience_factor = (double) jo.get("resilience_factor");
                double vulnerability_factor = (double) jo.get("vulnerability_factor");
                double speed = (double) jo.get("speed");
                double climb_rate = (double) jo.get("climb_rate");
                double autonomy = (double) jo.get("autonomy");

                IEntityType entType = new MobilityEntity(id, force, lethality_factor, resilience_factor,
                        vulnerability_factor, speed, climb_rate, autonomy);
                entityTypeDict.put(id, entType);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return entityTypeDict;
    }

    // 重写
    public static Map<String, IEntity> loadEntities(String team_name) {
        Map<String, IEntity> entitiesDict = new ConcurrentHashMap<>();
        String entityJson = null;

        switch (team_name) {
            case "B":
                entityJson = Variables.B_ENTITY_FILE;
                break;
            case "R":
                entityJson = Variables.R_ENTITY_FILE;
                break;
            default:
                entityJson = Variables.W_ENTITY_FILE;
        }

        try {
            Object obj = new JSONParser().parse(new FileReader(entityJson));

            JSONArray joArr = (JSONArray) obj;

            for (Object aJoArr : joArr) {
                JSONObject jo = (JSONObject) aJoArr;
                String id = (String) jo.get("id");
                String type = (String) jo.get("type");
                long behavior_mode = (long) jo.get("behavior_mode");
                String team = (String) jo.get("team");
                String force = (String) jo.get("force");

                double cost = (double) jo.get("cost");

                double latitude = (double) jo.get("latitude");
                double longitude = (double) jo.get("longitude");
                double altitude = 0.0;
                if (jo.get("altitude") != null)
                    altitude = (double) jo.get("altitude");
                Coordinate currentPosition = new Coordinate(latitude, longitude, altitude);
                Coordinate initPosition = new Coordinate(latitude, longitude, altitude);

                //String ent_type = (String) jo.get("ent_type");
                Map<String, IEntityType> entityTypeDict = loadEntityTypes();
                IEntityType entType = entityTypeDict.get(type);

                // 将entityType 属性加载入实体中
                IEntity entity = new Entity(id, behavior_mode, team, force, cost, type, entType, currentPosition,
                        initPosition);

                Map<String, ISensor> sensorMap = loadSensors();
                @SuppressWarnings("unchecked")
                List<String> sensorArr = (ArrayList<String>) jo.get("sensor");
                for (String sensorStr : sensorArr) {
                    Sensor sensor = (Sensor) sensorMap.get(sensorStr);
                    entity.addSensor(sensor);
                }
                Map<String, IWeapon> wpMap = loadWeapons();
                @SuppressWarnings("unchecked")
                List<String> wpArr = (ArrayList<String>) jo.get("weapon");
                for (String wpStr : wpArr) {
                    String[] wpTkn = wpStr.split(":");
                    String wpId = wpTkn[0];

                    int qt = Integer.parseInt(wpTkn[1]);

                    Weapon weapon = (Weapon) wpMap.get(wpId);

                    entity.addWeapon(weapon, qt);
                }

                entitiesDict.put(id, entity);

            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return entitiesDict;
    }


    // 修改部分
    public static Map<String, ITask> loadTasks(String team_name) {
        String taskJson = null;
        Map<String, ITask> taskDB = new ConcurrentHashMap<>();

        switch (team_name) {
            case "B":
                taskJson = Variables.B_TASK_FILE;
                break;
            case "R":
                taskJson = Variables.R_TASK_FILE;
                break;
            default:
                taskJson = Variables.W_TASK_FILE;                       // White任务池占位符
        }

        try {
            Object obj = new JSONParser().parse(new FileReader(taskJson));

            JSONArray joArr = (JSONArray) obj;

            for (Object aJoArr : joArr) {
                JSONObject jo = (JSONObject) aJoArr;

                String task_id = (String) jo.get("id");
                String task_executor = (String) jo.get("executor_id");
                String task_target = (String) jo.get("target_id");
                String task_type = (String) jo.get("type");
                double task_effectiveness = (double) jo.get("effectiveness");
                long start_time = (long) jo.get("set_time");

                ITask task = new Task(task_id, task_executor, task_target, start_time, task_type, task_effectiveness);

                @SuppressWarnings("unchecked")
                List<String> routeArr = (ArrayList<String>) jo.get("route");
                for (String routeStr : routeArr) {
                    String[] routeArrStr = routeStr.split(",");
                    double latitude = Double.parseDouble(routeArrStr[0]);
                    double longitude = Double.parseDouble(routeArrStr[1]);
                    double altitude = 0.0;
                    if (routeArrStr.length == 3) {
                        altitude = Double.parseDouble(routeArrStr[2]);
                    }

                    Coordinate coord = new Coordinate(latitude, longitude, altitude);
                    task.addRoute(coord);
                }

                @SuppressWarnings("unchecked")
                List<String> rWeaponArr = (ArrayList<String>) jo.get("required_weapon");
                for (String weaponName : rWeaponArr) {
                    task.addRequiredWeapon(weaponName);
                }

                List<String> rSensorArr = new ArrayList<>();
                if (jo.get("required_sensor") != null) {
                    rSensorArr = (ArrayList<String>) jo.get("required_sensor");
                }

                for (String sensorName : rSensorArr) {
                    task.addRequiredSensor(sensorName);
                }
                taskDB.put(task_id, task);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return taskDB;
    }

    public static Object[] loadServerParams() {
        Object[] server_params = null;

        try {
            String exeFile = Variables.EXE_FILE;
            Object obj = new JSONParser().parse(new FileReader(exeFile));

            JSONObject jo = (JSONObject) obj;

            double central_lat = (double) jo.get("central_lat");
            double central_long = (double) jo.get("central_long");
            Coordinate map_center = new Coordinate(central_lat, central_long, 0);
            String simu_addr = (String) jo.get("server_address");
            int simu_port = (int) ((long) jo.get("sim_port"));

            server_params = new Object[3];
            server_params[0] = simu_addr;
            server_params[1] = simu_port;
            server_params[2] = map_center;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return server_params;
    }

    /*
     * public static void main(String args[]) { Map<String, IMission> missions =
     * loadMissions("B"); List<IMission> missionL = new ArrayList<IMission>();
     * missionL.addAll(missions.values());
     *
     * for (IMission missionI : missionL) { Mission mission = (Mission) missionI;
     * System.out.println("mission - " + mission.getId()); for (IEntity ent :
     * mission.getPerformers()) { Entity entImpl = (Entity) ent;
     * System.out.println(entImpl.getId()); for (ITask task : entImpl.getTasks()) {
     * Task taskImpl = (Task) task; System.out.println(taskImpl.getRoutes().size() +
     * "routes"); ; } } System.out.println("$$$$$$");
     *
     * } }
     */

}
