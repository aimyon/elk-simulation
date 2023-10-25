package com.sim.elkSim.service.impl;

import com.sim.elkSim.service.SimExecutorService;
import com.sim.elkSim.exe.Exercise;
import com.sim.elkSim.sim.SimulationManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SimExecutorImpl implements SimExecutorService {

    @Override
    @Scheduled(initialDelay = 1000, fixedRate = Long.MAX_VALUE) // 隔多久启动
    public void start() {
        System.out.println("starting the simulation....");
        Exercise exe = new Exercise();
        SimulationManager simulationManager = SimulationManager.getInstance(exe);
        simulationManager.init();
        Thread thread = new Thread(simulationManager);
        thread.start();
    }
}
