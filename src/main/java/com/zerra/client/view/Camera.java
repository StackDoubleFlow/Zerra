package com.zerra.client.view;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;

import com.zerra.client.Zerra;
import com.zerra.client.input.InputHandler;
import com.zerra.client.input.gamepad.Gamepad;
import com.zerra.client.input.gamepad.Joystick;
import com.zerra.common.world.World;
import com.zerra.common.world.storage.Layer;
import com.zerra.common.world.storage.plate.Plate;

/**
 * <em><b>Copyright (c) 2019 The Zerra Team.</b></em>
 * 
 * <br>
 * </br>
 * 
 * A simple implementation of {@link ICamera}.
 * 
 * @author Ocelot5836
 */
public class Camera implements ICamera {

	private Vector3i lastPlatePosition;
	private Vector3i platePosition;
	private Vector3f renderPosition;
	private Vector3f lastPosition;
	private Vector3f position;
	private Vector3f renderRotation;
	private Vector3f lastRotation;
	private Vector3f rotation;

	public Camera() {
		this.lastPlatePosition = new Vector3i();
		this.platePosition = new Vector3i();
		this.renderPosition = new Vector3f();
		this.lastPosition = new Vector3f();
		this.position = new Vector3f();
		this.renderRotation = new Vector3f();
		this.lastRotation = new Vector3f();
		this.rotation = new Vector3f();
	}

	/**
	 * Updates the camera's position and rotation.
	 */
	public void update() {
		this.lastPlatePosition.set(this.platePosition);
		this.lastPosition.set(this.position);
		this.lastRotation.set(this.rotation);

		InputHandler inputHandler = Zerra.getInstance().getInputHandler();
		if (inputHandler.isGamepadConnected(GLFW.GLFW_JOYSTICK_1)) {
			Gamepad gamepad = inputHandler.getGamepad(GLFW.GLFW_JOYSTICK_1);
			Joystick joystick = gamepad.getJoystick(0);
			if (joystick != null) {
				this.position.y += joystick.getY();
				this.position.x += joystick.getX();
			}
		} else {
			if (inputHandler.isKeyPressed(GLFW.GLFW_KEY_W)) {
				this.position.y--;
			}
			if (inputHandler.isKeyPressed(GLFW.GLFW_KEY_S)) {
				this.position.y++;
			}
			if (inputHandler.isKeyPressed(GLFW.GLFW_KEY_A)) {
				this.position.x--;
			}
			if (inputHandler.isKeyPressed(GLFW.GLFW_KEY_D)) {
				this.position.x++;
			}
		}

		this.platePosition.set((int) (this.position.x / (float) (Plate.SIZE + 1)), (int) this.position.z, (int) (this.position.y / (float) (Plate.SIZE + 1)));
		if (!this.platePosition.equals(this.lastPlatePosition)) {
			World world = Zerra.getInstance().getWorld();
			Layer layer = world.getLayer(0);
			List<Vector3i> loadedPositions = new ArrayList<Vector3i>();
			for (int x = 0; x < 3; x++) {
				for (int z = 0; z < 3; z++) {
					Vector3i newPos = this.platePosition.add(x - 1, 0, z - 1, new Vector3i());
					layer.loadPlate(newPos);
					loadedPositions.add(newPos);
				}
			}
			for(Plate plate : layer.getLoadedPlates()) {
				if(!loadedPositions.contains(plate.getPlatePos())) {
					layer.unloadPlate(plate.getPlatePos());
				}
			}
		}
	}

	@Override
	public Vector3f getPosition() {
		return this.renderPosition.set(this.lastPosition.x + (this.position.x - this.lastPosition.x) * Zerra.getInstance().getRenderPartialTicks(), this.lastPosition.y + (this.position.y - this.lastPosition.y) * Zerra.getInstance().getRenderPartialTicks(), this.lastPosition.z + (this.position.z - this.lastPosition.z) * Zerra.getInstance().getRenderPartialTicks());
	}

	@Override
	public Vector3f getRotation() {
		return this.renderRotation.set(this.renderRotation.x + (this.rotation.x - this.renderRotation.x) * Zerra.getInstance().getRenderPartialTicks(), this.renderRotation.y + (this.rotation.y - this.renderRotation.y) * Zerra.getInstance().getRenderPartialTicks(), this.renderRotation.z + (this.rotation.z - this.renderRotation.z) * Zerra.getInstance().getRenderPartialTicks());
	}
}