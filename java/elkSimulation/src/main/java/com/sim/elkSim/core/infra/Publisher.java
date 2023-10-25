package com.sim.elkSim.core.infra;

import com.sim.elkSim.core.infra.messages.Message;

public abstract class Publisher {
	private Channel.TOPIC topic;

	public Publisher(Channel.TOPIC t) {
		this.topic = t;
	}

	public void publish(Message m, Channel channel) {
		channel.sendMessage(this.topic, m);
	}

}
