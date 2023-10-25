package com.sim.elkSim.entities.weapons.dynamic_weapon;

import com.sim.elkSim.entities.weapons.Weapon;

public class Torpedo extends Weapon {
    private double torpedo_speed;

    public Torpedo(String id, String domain, long range, String type,
                   double precision, double reliability, double intensity,
                   double speed) {
        super(id, domain, range, type, precision, reliability, intensity);
        this.torpedo_speed = speed;
    }

    public double getSpeed() {
        return torpedo_speed;
    }
}
