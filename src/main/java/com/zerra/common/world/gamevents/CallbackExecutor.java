package com.zerra.common.world.gamevents;

import java.util.function.Consumer;

class CallbackExecutor extends EventExecutor {

	private final Consumer<EventBase> callback;
	
	public CallbackExecutor(Consumer<EventBase> callback) {
		this.callback = callback;
	}
	
	@Override
	public void execute(EventBase event) {
		this.callback.accept(event);
	}
	
	@Override
	public boolean equals(Object obj) {
		return callback.equals(obj);
	}

	@Override
	public int hashCode() {
		return callback.hashCode();
	}
}
