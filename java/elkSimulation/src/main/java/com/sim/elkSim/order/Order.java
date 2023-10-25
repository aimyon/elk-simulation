package com.sim.elkSim.order;

import com.sim.elkSim.core.geo.Coordinate;
import com.sim.elkSim.entities.IEntity;

import java.util.List;
import java.util.Map;

public class Order implements IOrder {
    protected String id;
    protected double orderTime;
    protected INTENTION intention;
    protected IEntity target;
    private List<IEntity> executors;
    protected Map<IEntity, List<Coordinate>> mul_routes;

    public Order(String id, double orderTime, IEntity target, List<IEntity> executors) {
        this.id = id;
        this.orderTime = orderTime;
        this.target = target;
        this.executors = executors;
    }

    public String getId() {
        return id;
    }

    public double getOrderTime() {
        return orderTime;
    }

    public void setOrderType(INTENTION intention) {
        this.intention = intention;
    }

    public INTENTION getOrderType() {
        return intention;
    }

    public IEntity getTarget() {
        return target;
    }

    public List<IEntity> getExecutors() {
        return executors;
    }

    public void setRoutes(Map<IEntity, List<Coordinate>> mul_routes) {
        this.mul_routes = mul_routes;
    }

    public Map<IEntity, List<Coordinate>> getRoutes() {
        return mul_routes;
    }

}
