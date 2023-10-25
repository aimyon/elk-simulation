package com.sim.elkSim.entities.weapons.dynamic_weapon;

import com.sim.elkSim.entities.weapons.Weapon;

public class Missile extends Weapon {
    private double missile_speed;
    private double climb_rate;

    public Missile(String id, String domain, long range, String type,
                   double precision, double reliability, double intensity,
                   double speed, double climb_rate) {
        super(id, domain, range, type, precision, reliability, intensity);
        this.missile_speed = speed;
        this.climb_rate = climb_rate;
    }

    public double getSpeed() {
        return missile_speed;
    }

    public double getClimb_rate() {
        return climb_rate;
    }
}
