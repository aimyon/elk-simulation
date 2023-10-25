package com.sim.elkSim.entities.types;

public abstract class IEntityType {
	protected String id;
	protected String force;
	protected double lethality_factor;			//杀伤力因素
	protected double resilience_factor;			//恢复力因素
	protected double vulnerability_factor;		//脆弱性因素

	public String getId() {
		return id;
	}

	public String getForce() {
		return force;
	}

	public double getLethalityFactor() {
		return lethality_factor;
	}

	public double getResilianceFactor() {
		return resilience_factor;
	}

	public double getVulnerabilityFactor() {
		return vulnerability_factor;
	}

}
