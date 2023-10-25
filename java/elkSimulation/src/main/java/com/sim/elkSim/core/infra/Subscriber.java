package com.sim.elkSim.core.infra;

import com.sim.elkSim.core.infra.messages.Message;

public abstract class Subscriber {

	public Subscriber(Channel channel, Channel.TOPIC...topics) {

    }

    public abstract void receivedMessage(Channel.TOPIC t, Message m);

}
