package com.zerra.client.view;

import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.image.BufferedImage;
import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import com.zerra.client.Zerra;
import com.zerra.client.util.Loader;
import com.zerra.client.util.LoadingUtils;
import com.zerra.client.util.ResourceLocation;

/**
 * <em><b>Copyright (c) 2018 The Zerra Team.</b></em>
 * 
 * <br>
 * </br>
 * 
 * Creates and manages a display.
 * 
 * @author Ocelot5836
 */
public class Display {

	private static String title;
	private static int width;
	private static int height;
	private static boolean fullscreen;

	protected static byte[] joysticksPresent;

	private static DoubleBuffer mouseXBuffer = BufferUtils.createDoubleBuffer(1);
	private static DoubleBuffer mouseYBuffer = BufferUtils.createDoubleBuffer(1);

	private static long windowID;
	private static long cursorID;

	private Display() {
	}

	/**
	 * Creates a new window.
	 * 
	 * @param title
	 *            The title of the window
	 * @param width
	 *            The width of the window
	 * @param height
	 *            The height of the window
	 */
	public static void createDisplay(String title, int width, int height) {
		createDisplay(title, width, height, false);
	}

	/**
	 * Creates a new window.
	 * 
	 * @param title
	 *            The title of the window
	 * @param width
	 *            The width of the window
	 * @param height
	 *            The height of the window
	 * @param borderless
	 *            Whether or not to make the window borderless
	 */
	private static void createDisplay(String title, int width, int height, boolean borderless) {
		Display.title = title;
		Display.width = width;
		Display.height = height;
		Display.fullscreen = borderless;
		Display.joysticksPresent = new byte[GLFW.GLFW_JOYSTICK_LAST + 1];

		if (!GLFW.glfwInit()) {
			throw new RuntimeException("Failed to initialize GLFW");
		}

		if (isCreated())
			throw new RuntimeException("Display was already created!");
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, borderless ? GLFW.GLFW_FALSE : GLFW.GLFW_TRUE);

		windowID = GLFW.glfwCreateWindow(width, height, title, NULL, NULL);

		if (windowID == NULL) {
			throw new RuntimeException("Could not create window");
		}

		GLFW.glfwShowWindow(windowID);
		GLFW.glfwFocusWindow(windowID);

		GLFW.glfwSetKeyCallback(windowID, Listeners.KEY_CALLBACK);
		GLFW.glfwSetMouseButtonCallback(windowID, Listeners.MOUSE_CALLBACK);
		GLFW.glfwSetScrollCallback(windowID, Listeners.SCROLL_CALLBACK);
		GLFW.glfwSetJoystickCallback(Listeners.JOYSTICK_CALLBACK);

		for (int jid = 0; jid < Display.joysticksPresent.length; jid++) {
			Display.joysticksPresent[jid] = (byte) (GLFW.glfwJoystickPresent(GLFW.GLFW_JOYSTICK_1 + jid) ? 1 : 0);
			if (Display.joysticksPresent[jid] == 1) {
				Zerra.logger().info("Controller " + jid + " was connected");
			}
		}

		GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		GLFW.glfwSetWindowPos(windowID, (int) ((vidMode.width() * 0.5) - (width * 0.5)), (int) ((vidMode.height() * 0.5) - (height * 0.5)));

		GLFW.glfwMakeContextCurrent(windowID);
		GLFW.glfwSwapInterval(1);
		GL.createCapabilities();
	}

	/**
	 * Updates the display.
	 */
	public static void update() {
		GLFW.glfwPollEvents();
		GLFW.glfwSwapBuffers(windowID);
	}

	/**
	 * Disposes of the window.
	 */
	private static void dispose() {
		if (cursorID != NULL) {
			GLFW.glfwDestroyCursor(cursorID);
			cursorID = NULL;
		}
		if (windowID != NULL) {
			GLFW.glfwDestroyWindow(windowID);
			windowID = NULL;
		}
		GLFW.glfwTerminate();
	}

	/**
	 * Deletes the display and terminates GLFW.
	 */
	public static void destroy() {
		dispose();
		Listeners.KEY_CALLBACK.free();
		Listeners.MOUSE_CALLBACK.free();
		Listeners.SCROLL_CALLBACK.free();
		Listeners.JOYSTICK_CALLBACK.free();
	}

	/**
	 * Closes the display.
	 */
	public static void close() {
		GLFW.glfwSetWindowShouldClose(windowID, true);
	}

	/**
	 * Sets the cursor in the display.
	 * 
	 * @param image
	 *            The cursor image
	 * @param xhot
	 *            The x position in the image that equals 0,0
	 * @param yhot
	 *            The y position in the image that equals 0,0
	 */
	public static void setCursor(BufferedImage image, int xhot, int yhot) {
		GLFWImage cursorImage = GLFWImage.create();
		cursorImage.width(image.getWidth());
		cursorImage.height(image.getHeight());
		cursorImage.pixels(Loader.loadToByteBuffer(image));
		cursorID = GLFW.glfwCreateCursor(cursorImage, xhot, yhot);
		if (cursorID != NULL) {
			GLFW.glfwSetCursor(windowID, cursorID);
		}
	}

	/**
	 * Sets the icon for the window.
	 * 
	 * @param icons
	 *            The locations of the images that will become the icon
	 */
	public static void setIcon(ResourceLocation... icons) {
		GLFWImage.Buffer buffer = GLFWImage.create(icons.length);
		for (int i = 0; i < icons.length; i++) {
			BufferedImage image = LoadingUtils.loadImage("displayIcon" + i, icons[i].getInputStream());
			GLFWImage icon = GLFWImage.create();
			icon.width(image.getWidth());
			icon.height(image.getHeight());
			icon.pixels(Loader.loadToByteBuffer(image));
			buffer.put(icon);
		}
		buffer.flip();
		GLFW.glfwSetWindowIcon(windowID, buffer);
	}

	/**
	 * Makes the display full screen. This does not work yet and crashes the game.
	 */
	public static void setFullscreen() {
		GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		dispose();
		createDisplay(title, vidMode.width(), vidMode.height(), true);
	}

	/**
	 * Sets the window to be a moveable window again.
	 */
	public static void setWindowed() {
		dispose();
		createDisplay(title, width, height);
	}

	/**
	 * @return The title of the window
	 */
	public static String getTitle() {
		return title;
	}

	/**
	 * @return The width of the window
	 */
	public static int getWidth() {
		return width;
	}

	/**
	 * @return The height of the window
	 */
	public static int getHeight() {
		return height;
	}

	/**
	 * @return Whether or not the window is in fullscreen
	 */
	public static boolean isFullscreen() {
		return fullscreen;
	}

	/**
	 * @return The x position of the mouse
	 */
	public static double getMouseX() {
		GLFW.glfwGetCursorPos(windowID, mouseXBuffer, mouseYBuffer);
		return mouseXBuffer.get(0);
	}

	/**
	 * @return The y position of the mouse
	 */
	public static double getMouseY() {
		GLFW.glfwGetCursorPos(windowID, mouseXBuffer, mouseYBuffer);
		return mouseYBuffer.get(0);
	}

	/**
	 * @return The button currently pressed on the mouse
	 */
	public static int getMouseButton() {
		return GLFW.glfwGetMouseButton(windowID, 0) == GLFW.GLFW_TRUE ? 0 : GLFW.glfwGetMouseButton(windowID, 1) == GLFW.GLFW_TRUE ? 1 : GLFW.glfwGetMouseButton(windowID, 2) == GLFW.GLFW_TRUE ? 2 : -1;
	}

	/**
	 * @return Whether or not there is a window on the screen
	 */
	public static boolean isCreated() {
		return windowID != NULL;
	}

	/**
	 * @return Whether or not the window wants to close
	 */
	public static boolean isCloseRequested() {
		return GLFW.glfwWindowShouldClose(windowID);
	}

	/**
	 * Checks if a key is pressed.
	 * 
	 * @param key
	 *            The key to check
	 * @return Whether or not that key is pressed
	 */
	public static boolean isKeyPressed(int key) {
		return GLFW.glfwGetKey(windowID, key) == GLFW.GLFW_PRESS;
	}

	/**
	 * @return Whether or not there was a joystick detected
	 */
	public static boolean isJoystickPresent(int joystick) {
		return joysticksPresent[joystick] == 1;
	}

	/**
	 * @return All of the joysticks that may or may not be detected
	 */
	public static byte[] getAllPresentJoysticks() {
		return joysticksPresent;
	}

	/**
	 * Sets the title of the window.
	 * 
	 * @param title
	 *            The new title for the window
	 */
	public static void setTitle(String title) {
		GLFW.glfwSetWindowTitle(windowID, title);
		Display.title = title;
	}

	/**
	 * Sets the size of the window.
	 * 
	 * @param width
	 *            The new width for the window
	 * @param height
	 *            The new height for the window
	 */
	public static void setSize(int width, int height) {
		GLFW.glfwSetWindowSize(windowID, width, height);
		Display.width = width;
		Display.height = height;
	}
}