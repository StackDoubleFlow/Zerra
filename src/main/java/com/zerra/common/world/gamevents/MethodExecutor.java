package com.zerra.common.world.gamevents;

import java.lang.reflect.Method;

class MethodExecutor extends EventExecutor {

	private final EventListener listener;
	private final Method method;

	public MethodExecutor(EventListener listener, Method method) {
		this.listener = listener;
		this.method = method;
	}

	public void execute(EventBase event) {
		try {
			method.invoke(listener, event);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object obj) {
		return listener.equals(obj);
	}

	@Override
	public int hashCode() {
		return listener.hashCode();
	}

	@Override
	public String toString() {
		return listener.getClass().getSimpleName();
	}
}