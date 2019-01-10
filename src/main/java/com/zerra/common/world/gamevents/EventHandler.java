package com.zerra.common.world.gamevents;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class EventHandler {

	private final ConcurrentHashMap<Class<? extends EventBase>, HashSet<EventExecutor>> eventExecutors;

	public EventHandler() {
		this.eventExecutors = new ConcurrentHashMap<>();
	}

	public void registerEventListener(EventListener listener) {
		for (Method method : listener.getClass().getMethods()) {
			if (method.getParameterTypes().length != 1)
				continue;
			if (!method.isAnnotationPresent(Event.class))
				continue;
			method.setAccessible(true);
			@SuppressWarnings("unchecked")
			Class<? extends EventBase> eventClazz = (Class<? extends EventBase>) method.getParameterTypes()[0];
			HashSet<EventExecutor> methodSet = eventExecutors.getOrDefault(eventClazz, new HashSet<>());
			methodSet.add(new MethodExecutor(listener, method));
			eventExecutors.put(eventClazz, methodSet);
		}
	}
	
	public <T extends EventBase> void registerCallback(Class<? extends T> eventClass, Consumer<T> callback) {
		@SuppressWarnings("unchecked")
		CallbackExecutor executor = new CallbackExecutor((Consumer<EventBase>) callback);
		HashSet<EventExecutor> methodSet = eventExecutors.getOrDefault(eventClass, new HashSet<>());
		methodSet.add(executor);
		eventExecutors.put(eventClass, methodSet);
	}
	
	public void unregisterEventListener(EventListener listener) {
		for (HashSet<EventExecutor> set : eventExecutors.values()) {
			for (EventExecutor executor : set) {
				if (executor.equals(listener)) {
					set.remove(executor);
				}
			}
		}
	}

	public void callEvent(EventBase event) {
		for (Entry<Class<? extends EventBase>, HashSet<EventExecutor>> entry : eventExecutors.entrySet()) {
			if (entry.getKey().isInstance(event)) {
				for (EventExecutor executor : entry.getValue()) {
					if (event.isCancelled())
						continue;
					executor.execute(event);
					if (event.isConsumed())
						break;
				}
			}
		}
	}
}