package com.sim.elkSim.entities.weapons;

public interface IWeapon {

	public String getId();

	public String getDomain();

	public long getRange();

	public String getType();

	public double getPrecision();

	public double getReliability();

	public double getIntensity();
}
