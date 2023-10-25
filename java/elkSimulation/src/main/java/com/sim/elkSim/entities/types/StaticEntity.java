package com.sim.elkSim.entities.types;

public class StaticEntity extends IEntityType{
	//定义静态本体
	public StaticEntity(String id, String force, double lethality_factor,
			double resilience_factor,
			double vulnerability_factor) {
		super.id = id;
		super.force = force;
		super.lethality_factor = lethality_factor;
		super.resilience_factor = resilience_factor;
		super.vulnerability_factor = vulnerability_factor;
	}


}
