package com.zerra.client.view;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;

import com.zerra.Launch;
import com.zerra.client.MenuState;
import com.zerra.client.StateManager;
import com.zerra.client.WorldState;
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
	
	private float speedAdjust = 0f;
	
	private boolean controllerIsOffset = false;
	private double xOffset;
	private double yOffset;

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

				if(!this.controllerIsOffset && joystick.getX() != 0 && joystick.getY() != 0 && Display.isCreated()) {
					xOffset = joystick.getX();
					yOffset = joystick.getY();
					this.controllerIsOffset = true;
				}
				
				if(joystick.getX() < 0) {
					if(!(joystick.getX() > -xOffset - 0.01f)) {
						this.position.x += joystick.getX();
					}
				}else {
					if(!(joystick.getX() < xOffset + 0.01f)) {
						this.position.x += joystick.getX();
					}
				}
				
				if(joystick.getY() < 0) {
					if(!(joystick.getY() > -yOffset - 0.01f)) {
						this.position.y += joystick.getY();
					}
				}else {
					if(!(joystick.getY() < yOffset + 0.01f)) {
						this.position.y += joystick.getY();
					}
				}
			}
		} else {
			if (inputHandler.isKeyPressed(GLFW.GLFW_KEY_W) || inputHandler.isKeyPressed(GLFW.GLFW_KEY_UP)) {
				this.position.y -= 1 + this.speedAdjust;
			}
			if (inputHandler.isKeyPressed(GLFW.GLFW_KEY_S) || inputHandler.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
				this.position.y += 1 + this.speedAdjust;
			}
			if (inputHandler.isKeyPressed(GLFW.GLFW_KEY_A) || inputHandler.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
				this.position.x -= 1 + this.speedAdjust;
			}
			if (inputHandler.isKeyPressed(GLFW.GLFW_KEY_D) || inputHandler.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
				this.position.x += 1 + this.speedAdjust;
			}
			if (inputHandler.isKeyPressed(GLFW.GLFW_KEY_ESCAPE))
			{
				if (StateManager.getActiveState() instanceof WorldState)
				{
					System.out.println("Switching to menu state...");
					StateManager.setActiveState(new MenuState());
				}
			}
			
			// Useful keys to adjust the movement speed of the camera when not locked to the player.
			if(Launch.IS_DEVELOPMENT_BUILD) {
				if (inputHandler.isKeyPressed(GLFW.GLFW_KEY_EQUAL)) {
					this.speedAdjust += (this.speedAdjust < 10) ? 0.07f : 0.0f;
				}
				if (inputHandler.isKeyPressed(GLFW.GLFW_KEY_MINUS)) {
					this.speedAdjust -= (this.speedAdjust > -0.75f) ? 0.07f : 0.0f;
				}
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