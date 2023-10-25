package com.sim.elkSim.core.engines;

import java.util.List;
import java.util.Random;

import com.sim.elkSim.core.geo.Coordinate;
import com.sim.elkSim.entities.Entity;
import com.sim.elkSim.entities.types.IEntityType;
import com.sim.elkSim.entities.types.WeaponEntity;
import com.sim.elkSim.entities.weapons.IWeapon;
import com.sim.elkSim.core.utils.GeoUtils;
import com.sim.elkSim.entities.IEntity;
import com.sim.elkSim.entities.IEntity.STATUS;
import com.sim.elkSim.entities.sensors.ISensor;
import com.sim.elkSim.entities.weapons.dynamic_weapon.Missile;
import com.sim.elkSim.entities.weapons.dynamic_weapon.Torpedo;
import com.sim.elkSim.task.ITask;

import static com.sim.elkSim.sim.SimulationManager.entDB;
import static com.sim.elkSim.sim.SimulationManager.taskDB;

public class EffectEngine {

    // 对于移动单位,要么不断排查敌方单位,要么仅对目标单位进行攻击
    public static void mobile_effect(String task_id, List<IEntity> entList, long simu_cur_time, long time_step) {
        ITask task = taskDB.get(task_id);
        String executor_id = task.getExecutor_id();
        String target_id = task.getTarget_id();

        IEntity executor = entDB.get(executor_id);

        if (executor.getBehaviorMode() == 1 || task.getType().equals("PATROL") || task.getType().equals("DETECT")) {               // 攻击模式下或巡逻任务下将视为固定交战
            static_effect(task_id, entList, simu_cur_time, time_step);
        } else {
            detecting(executor, entList);
            if (entDB.get(target_id).getStatus().toString().equals(STATUS.DESTROYED.toString()) || !entDB.get(target_id).getDetectedStatus()) {      // 当敌方处于摧毁或未被侦测状态,按照既定路线继续进行
                //System.out.print(executor_id + " has no target or target been destroyed\n");
            } else {
                to_engaging(executor, entDB.get(target_id));
            }
        }
    }

    // 对于固定单位,不断排查附近是否存在敌方单位
    public static void static_effect(String task_id, List<IEntity> entList, long simu_cur_time, long time_step) {
        ITask task = taskDB.get(task_id);
        String executor_id = task.getExecutor_id();
        IEntity executor = entDB.get(executor_id);
        List<IWeapon> weaponList = executor.getWeaponList();
        String teamType = executor.getTeam();

        detecting(executor, entList);
        if (weaponList.size() > 0) {
            for (IEntity entity : entList) {
                if (entity.getTeam().equals(teamType)) continue;
                if (entity.getStatus().toString().equals(STATUS.DESTROYED.toString()) || !entity.getDetectedStatus()) {      // 当敌方处于摧毁或未被侦测状态,按照既定路线继续进行
                    //System.out.print(executor_id + " has no target or target been destroyed\n");
                } else {
                    to_engaging(executor, entity);
                }
            }
        }
    }

    // 侦察任务,更新任务状态
    private static void detecting(IEntity executor, List<IEntity> entList) {
        List<ISensor> sensorList = executor.getSensors();
        if (sensorList.size() == 0) return;
        if (executor.getBehaviorMode() != 0) {                                // 表示不主动侦测状态
            for (ISensor sensor : sensorList) {
                for (IEntity enemy : entList) {
                    if (!enemy.getTeam().equals(executor.getTeam()) && sensor.isSense(executor, enemy)) {
                        if (!enemy.getDetectedStatus()) {
                            System.out.println(enemy.getId() + " has been detected!");
                        }
                        enemy.setDetectedStatus(true);                      // 敌方单位被侦察
                        entDB.put(enemy.getId(), enemy);
                    }
                }
            }
        }
    }

    private static void to_engaging(IEntity attacker, IEntity defender) {

        double engage_distance = GeoUtils.calculateHorizontalDistanceMeters(attacker.getCurrentPosition(),
                defender.getCurrentPosition());
        IWeapon attacker_weapon = getWeapon(attacker, engage_distance);
        IWeapon defender_weapon = getWeapon(defender, engage_distance);

        engage(attacker, defender, attacker_weapon, defender_weapon);
    }

    // 遭遇对抗,更新任务状态
    private static void engage(IEntity attacker, IEntity defender,
                               IWeapon attacker_weapon, IWeapon defender_weapon) {

        Random rand = new Random(System.currentTimeMillis());                                   // 设置随机数

        double param_attacker = 0;                                  // 为0表示武器不可用
        double param_defender = 0;
        if (attacker_weapon != null)
            param_attacker = attacker.getEntType().getLethalityFactor() * attacker_weapon.getIntensity()
                    * attacker_weapon.getPrecision() * rand.nextDouble();
        if (defender_weapon != null) {
            param_defender = defender.getEntType().getResilianceFactor() * defender_weapon.getReliability()
                    / Math.pow(2, defender.getEntType().getVulnerabilityFactor()) * rand.nextDouble();
        }
        if (!attacker.getDetectedStatus()) param_defender = 0;
        if (!defender.getDetectedStatus()) param_attacker = 0;

        double result = param_attacker - param_defender;
        if (param_attacker != 0 && param_defender != 0) {
            attacker.decrementWeapon(attacker_weapon);
            defender.decrementWeapon(defender_weapon);
            if (result > 0) {
                double defender_health = defender.getHealth() - result;
                defender.setHealth(defender_health);
                if (defender_health < 0.1) {
                    defender.setStatus(STATUS.DESTROYED);
                    System.out.println(defender.getId() + " DESTROYED by " + attacker.getId());
                }
            } else {
                double attacker_health = attacker.getHealth() + result;
                attacker.setHealth(attacker_health);
                if (attacker_health < 0.1) {
                    attacker.setStatus(STATUS.DESTROYED);
                    System.out.println(attacker.getId() + " DESTROYED by " + defender.getId());
                }
            }
        } else if (param_attacker == 0 && param_defender != 0) {
            defender.decrementWeapon(defender_weapon);
            double attacker_health = attacker.getHealth() + result;
            attacker.setHealth(attacker_health);
            if (attacker_health < 0.1) {
                attacker.setStatus(STATUS.DESTROYED);
                System.out.println(attacker.getId() + " DESTROYED by " + defender.getId());
            }

        } else if (param_attacker != 0 && param_defender == 0) {
            attacker.decrementWeapon(attacker_weapon);
            double defender_health = defender.getHealth() - result;
            defender.setHealth(defender_health);
            if (defender_health < 0.1) {
                defender.setStatus(STATUS.DESTROYED);
                System.out.println(defender.getId() + " DESTROYED by " + attacker.getId());
            }
        }

        // 更新执行者与任务目标状态
//        System.out.println(attacker.getId() + " " + attacker.getStatus().toString());
//        System.out.println(defender.getId() + " " + defender.getStatus().toString());

        entDB.put(attacker.getId(), attacker);
        entDB.put(defender.getId(), defender);
    }

    private static IWeapon getWeapon(IEntity entity, double desired_range) {                  // 可发射且射程内武器
        if (entity.getWeaponList() != null) {
            List<IWeapon> weaponList = entity.getWeaponList();
            for (IWeapon wp : weaponList) {
                int qtd = entity.getWeaponQuantity(wp);
                double weapon_range = wp.getRange();
                if (qtd > 0 && weapon_range >= desired_range) {
                    return wp;
                }
            }
        }
        return null;
    }


    private static void launchWeapon(IEntity attacker, String lockTarget, IWeapon weapon) {    // 发射动态武器,生成武器实体
        if (weapon instanceof Torpedo) {

        } else if (weapon instanceof Missile) {

        } else {

        }
        attacker.decrementWeapon(weapon);
        createWeaponEnt(weapon, attacker.getCurrentPosition());


    }

    // 武器添加实体
    private static void createWeaponEnt(IWeapon weapon, Coordinate position) {
        int num = 1;
        String weaponId = weapon.getId() + Integer.toString(num);
        while (entDB.containsKey(weaponId)) {
            num++;
            weaponId = weapon.getId() + Integer.toString(num);
        }
        double speed = -1;
        double climb_rate = -1;
        if (weapon instanceof Missile) {
            speed = ((Missile) weapon).getSpeed();
            climb_rate = ((Missile) weapon).getClimb_rate();
        } else if (weapon instanceof Torpedo) {
            speed = ((Torpedo) weapon).getSpeed();
        }

        IEntityType weaponEntType = new WeaponEntity(weapon.getId(), weapon.getDomain(),
                0.0, 0.0, 0.0, speed, climb_rate);

        IEntity weapon_hitter = new Entity(weaponId, 2, "",
                weapon.getDomain(), 0.0, weapon.getType(), weaponEntType,
                position, position);
        entDB.put(weaponId, weapon_hitter);
    }

    // 武器添加任务
    private static void createWeaponTask(String lock_on_target) {
        String weaponTaskId;


    }

    private static void damageConfirm(IEntity entity, IEntity weapon_hitter) {                         // 确认毁伤效果

    }

}
