package com.zerra.client.gfx.renderer.tile;

import com.zerra.client.Zerra;
import com.zerra.client.gfx.model.Model;
import com.zerra.client.gfx.renderer.Renderer;
import com.zerra.client.gfx.shader.TileShader;
import com.zerra.client.util.Maths;
import com.zerra.client.view.Display;
import com.zerra.client.view.ICamera;
import com.zerra.common.world.World;
import com.zerra.common.world.storage.Layer;
import com.zerra.common.world.storage.plate.Plate;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class TileRenderer {

	private Matrix4f projectionMatrix;
	private TileShader shader;
	private TileMeshCreator meshCreator;

	public TileRenderer() {
		this.projectionMatrix = new Matrix4f().ortho(0, Display.getWidth() / (Renderer.SCALE * 16), Display.getHeight() / (Renderer.SCALE * 16), 0, 0.3f, 1000.0f);
		this.shader = new TileShader();
		this.shader.start();
		this.shader.loadProjectionMatrix(this.projectionMatrix);
		this.shader.loadLights();
		this.shader.stop();
		this.meshCreator = new TileMeshCreator();
	}

	public void renderTiles(ICamera camera, World world, int layer) {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		this.meshCreator.prepare();
		Layer worldLayer = world.getLayer(layer);
		if (worldLayer != null) {
			Plate[] plates = worldLayer.getLoadedPlates();
			for (int i = 0; i < plates.length; i++) {
				Plate plate = plates[i];
				if (this.meshCreator.ready(plate)) {
					Model model = this.meshCreator.getModel(plate);
					Zerra.getInstance().getTextureManager().bind(Zerra.getInstance().getTextureMap().getLocation());
					this.shader.start();
                    this.shader.loadTransformationMatrix(Maths.createTransformationMatrix(plate.getPlatePos().x() * (Plate.SIZE + 1), plate.getPlatePos().z() * (Plate.SIZE + 1), 0, 0, 1, 1));
					this.shader.loadViewMatrix(camera);
					GL30.glBindVertexArray(model.getVaoID());
					GL20.glEnableVertexAttribArray(0);
					GL20.glEnableVertexAttribArray(1);
					GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
					GL20.glDisableVertexAttribArray(1);
					GL20.glDisableVertexAttribArray(0);
					GL30.glBindVertexArray(0);
					this.shader.stop();
				}
			}
		}
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
}