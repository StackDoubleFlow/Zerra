package com.zerra.client.input;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.glfw.GLFW;

import com.zerra.client.Zerra;
import com.zerra.client.input.gamepad.Gamepad;
import com.zerra.client.view.Display;

/**
 * <em><b>Copyright (c) 2019 The Zerra Team.</b></em>
 * 
 * <br>
 * </br>
 * 
 * Handles all input passed in from the {@link Display}'s handlers into {@link Zerra}.
 * 
 * @author Ocelot5836
 */
public class InputHandler {

	private byte[] keys;
	private byte[] mouseButtons;
	private Map<Integer, Gamepad> gamepads;

	public InputHandler() {
		this.keys = new byte[GLFW.GLFW_KEY_LAST];
		this.mouseButtons = new byte[GLFW.GLFW_MOUSE_BUTTON_LAST];
		this.gamepads = new HashMap<Integer, Gamepad>();
		for (int jid = 0; jid < Display.getAllPresentJoysticks().length; jid++) {
			if (Display.isJoystickPresent(jid)) {
				this.onGamepadConnected(jid);
			}
		}
	}

	/**
	 * Checks to see if the key code supplied has been pressed.
	 * 
	 * @param keyCode
	 *            The code of the key
	 * @return Whether or not that key was pressed
	 */
	public boolean isKeyPressed(int keyCode) {
		if(keyCode < 0 || keyCode >= this.keys.length)
			return false;
		return this.keys[keyCode] == 1;
	}

	/**
	 * Checks to see if the mouse button supplied is pressed.
	 * 
	 * @param mouseButton
	 *            The button to check
	 * @return Whether or not that button is pressed
	 */
	public boolean isMouseButtonPressed(int mouseButton) {
		if(mouseButton < 0 || mouseButton >= this.mouseButtons.length)
			return false;
		return this.mouseButtons[mouseButton] == 1;
	}

	/**
	 * Sets the key with the supplied key code to be pressed or not.
	 * 
	 * @param keyCode
	 *            The key code to set pressed
	 * @param pressed
	 *            Whether or not the key should be pressed
	 */
	public void setKeyPressed(int keyCode, boolean pressed) {
		if(keyCode < 0 || keyCode >= this.keys.length)
			return;
		this.keys[keyCode] = (byte) (pressed ? 1 : 0);
	}

	/**
	 * Sets the supplied mouse button to be pressed or not.
	 * 
	 * @param mouseButton
	 *            The button to set to be pressed
	 * @param pressed
	 *            Whether or not the button should be pressed
	 */
	public void setMouseButtonPressed(int mouseButton, boolean pressed) {
		if(mouseButton < 0 || mouseButton >= this.mouseButtons.length)
			return;
		this.mouseButtons[mouseButton] = (byte) (pressed ? 1 : 0);
	}

	/**
	 * Updates all of the gamepads connected to the device.
	 */
	public void updateGamepad() {
		for (int jid : this.gamepads.keySet()) {
			Gamepad gamepad = this.gamepads.get(jid);
			if (GLFW.glfwJoystickPresent(jid)) {
				gamepad.update();
			} else {
				this.onGamepadDisconnected(jid);
			}
		}
	}

	/**
	 * Registers a gamepad as being connected.
	 * 
	 * @param jid
	 *            The id of the joystick
	 */
	public void onGamepadConnected(int jid) {
		if (this.gamepads.containsKey(jid)) {
			this.gamepads.get(jid).invalidate();
			this.gamepads.remove(jid);
		}
		this.gamepads.put(jid, new Gamepad(jid));
	}

	/**
	 * Registers a joystick as being disconnected.
	 * 
	 * @param jid
	 *            The id of the joystick
	 */
	public void onGamepadDisconnected(int jid) {
		this.gamepads.remove(jid);
	}

	/**
	 * Checks to see if a gamepad is connected.
	 * 
	 * @param jid
	 *            The id of the joystick
	 * @return Whether or not the gamepad is connected
	 */
	public boolean isGamepadConnected(int jid) {
		return this.gamepads.containsKey(jid);
	}

	/**
	 * Checks to see if a gamepad is connected to the device.
	 * 
	 * @param jid
	 *            The id of the joystick
	 * @return The gamepad or null if it is not connected
	 */
	@Nullable
	public Gamepad getGamepad(int jid) {
		return this.gamepads.get(jid);
	}
}