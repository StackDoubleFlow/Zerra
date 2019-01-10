package com.zerra.common.world.gamevents.events.client;

import com.zerra.common.world.gamevents.EventBase;

public class KeyPressedEvent extends EventBase {

	private int keyCode;
	
	public KeyPressedEvent(int keyCode) {
		this.keyCode = keyCode;
	}
	
	public int getKeyCode() {
		return keyCode;
	}
}
