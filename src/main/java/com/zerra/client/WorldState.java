package com.zerra.client;

import org.lwjgl.opengl.GL11;

import com.zerra.client.gfx.renderer.GuiRenderer;
import com.zerra.client.view.Display;

public class WorldState extends State
{

	private Zerra zerra;

	public WorldState()
	{
		super("world");
		zerra = Zerra.getInstance();
		zerra.getPresence().setPresence("Playing on World \'" + zerra.getWorld().getName() + "\'", "512x512", "none");
	}

	@Override
	public void update()
	{
		zerra.camera.update();
		zerra.inputHandler.updateGamepad();
	}

	@Override
	public void render()
	{
		zerra.fbo.bindFrameBuffer();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		zerra.tileRenderer.renderTiles(zerra.camera, zerra.world, 0);
		zerra.fbo.unbindFrameBuffer();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, zerra.fbo.getColorTexture(0));
		zerra.guiRenderer.setProjectionMatrix(GuiRenderer.FBO_MATRIX);
		zerra.guiRenderer.renderTextureQuad(0, 0, Display.getWidth(), Display.getHeight(), 0, 0, 1, 1, 1, 1);
		zerra.guiRenderer.restoreProjectionMatrix();
	}
}
