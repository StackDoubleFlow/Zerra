package com.zerra.common.event;

import java.util.function.Consumer;

public class CallbackExecutor extends EventExecutor {

	private final Consumer<Event> callback;
	
	public CallbackExecutor(Consumer<Event> callback) {
		this.callback = callback;
	}
	
	@Override
	public void execute(Event event) {
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
