package com.sim.elkSim.sim;

import java.util.ArrayList;
import java.util.List;

import com.sim.elkSim.core.engines.EffectEngine;
import com.sim.elkSim.entities.IEntity;
import com.sim.elkSim.task.ITask;
import com.sim.elkSim.entities.types.MobilityEntity;

public class EffectManager implements Runnable {

    private boolean running = false;
    private static EffectManager instance;

    private EffectManager() {
        running = true;
    }

    public static EffectManager getInstance() {
        if (instance == null) {
            instance = new EffectManager();
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

    public void effectProcess(long simu_start_time, long simu_cur_time, long time_step, String task_id) {
        List<IEntity> entList = new ArrayList<>(SimulationManager.entDB.values());
        ITask task = SimulationManager.taskDB.get(task_id);
        IEntity executor = SimulationManager.entDB.get(task.getExecutor_id());

        if (executor.getEntType() instanceof MobilityEntity) {
            EffectEngine.mobile_effect(task_id, entList, simu_cur_time, time_step);
        } else {
            EffectEngine.static_effect(task_id, entList, simu_cur_time, time_step);
        }

    }


}
