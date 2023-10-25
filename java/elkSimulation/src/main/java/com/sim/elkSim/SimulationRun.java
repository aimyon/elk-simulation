package com.sim.elkSim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SimulationRun {

    public static void main(String args[]) {
        SpringApplication.run(SimulationRun.class, args);
    }
}
