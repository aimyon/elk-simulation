package com.sim.elkSim.entities.types;

public class MobilityEntity extends IEntityType {
	private double speed;
	private double climb_rate;
	private double autononomy;
	//定义可动本体
	public MobilityEntity(String id, String force, double lethality_factor,
			double resilience_factor,
			double vulnerability_factor,
			double speed, double climb_rate, double autonomy) {
		super.id = id;
		super.force = force;
		super.lethality_factor = lethality_factor;
		super.resilience_factor = resilience_factor;
		super.vulnerability_factor = vulnerability_factor;
		this.speed=speed;
		this.climb_rate=climb_rate;
		this.autononomy=autonomy;
	}

	private void setCurSpeed(double speed){
		this.speed = speed;
	}

	private void setClimb_rate(double climb_rate){
		this.climb_rate = climb_rate;
	}

	public double getSpeed() {
		return speed;
	}

	public double getClimbRate() {
		return climb_rate;
	}

	public double getAutononomy() {
		return autononomy;
	}
}
