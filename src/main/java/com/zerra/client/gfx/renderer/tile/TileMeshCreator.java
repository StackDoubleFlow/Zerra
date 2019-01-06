package com.zerra.client.gfx.renderer.tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL30;

import com.zerra.client.Zerra;
import com.zerra.client.gfx.model.Model;
import com.zerra.client.gfx.texture.map.TextureMapSprite;
import com.zerra.client.util.Loader;
import com.zerra.client.util.ResourceLocation;
import com.zerra.common.world.storage.plate.Plate;
import com.zerra.common.world.tile.Tile;

public class TileMeshCreator {

	private Map<Plate, PlateMeshData> generatedPlates;
	private Map<Plate, Model> platesMesh;
	private List<Plate> requestedPlates;

	public TileMeshCreator() {
		this.generatedPlates = new ConcurrentHashMap<Plate, PlateMeshData>();
		this.platesMesh = new ConcurrentHashMap<Plate, Model>();
		this.requestedPlates = new ArrayList<Plate>();
	}

	// TODO use indices where possible perhaps
	@Deprecated
	private void generate2DPlateMesh(Plate plate, int index) {
		int size = Plate.SIZE + 1;
		float[] vertices = new float[size * size * 12];
		float[] textureCoords = new float[size * size * 12];
		Map<Tile, ResourceLocation> textureCache = new HashMap<Tile, ResourceLocation>();

		long lastTime = System.currentTimeMillis();
		int vertexPointer = 0;
		for (int x = 0; x < size; x++) {
			for (int z = 0; z < size; z++) {
				Vector2i pos = new Vector2i(x, z);
				Tile tile = plate.getTileAt(pos);
				if (!textureCache.containsKey(tile)) {
					textureCache.put(tile, tile.getTexture());
				}
				TextureMapSprite sprite = Zerra.getInstance().getTextureMap().getSprite(textureCache.get(tile));
				vertices[vertexPointer * 12] = x;
				vertices[vertexPointer * 12 + 1] = z;
				vertices[vertexPointer * 12 + 2] = x;
				vertices[vertexPointer * 12 + 3] = z + 1;
				vertices[vertexPointer * 12 + 4] = x + 1;
				vertices[vertexPointer * 12 + 5] = z;
				vertices[vertexPointer * 12 + 6] = x + 1;
				vertices[vertexPointer * 12 + 7] = z;
				vertices[vertexPointer * 12 + 8] = x;
				vertices[vertexPointer * 12 + 9] = z + 1;
				vertices[vertexPointer * 12 + 10] = x + 1;
				vertices[vertexPointer * 12 + 11] = z + 1;
				textureCoords[vertexPointer * 12] = (float) sprite.getXMin();
				textureCoords[vertexPointer * 12 + 1] = (float) sprite.getYMin();
				textureCoords[vertexPointer * 12 + 2] = (float) sprite.getXMin();
				textureCoords[vertexPointer * 12 + 3] = (float) sprite.getYMax();
				textureCoords[vertexPointer * 12 + 4] = (float) sprite.getXMax();
				textureCoords[vertexPointer * 12 + 5] = (float) sprite.getYMin();
				textureCoords[vertexPointer * 12 + 6] = (float) sprite.getXMax();
				textureCoords[vertexPointer * 12 + 7] = (float) sprite.getYMin();
				textureCoords[vertexPointer * 12 + 8] = (float) sprite.getXMin();
				textureCoords[vertexPointer * 12 + 9] = (float) sprite.getYMax();
				textureCoords[vertexPointer * 12 + 10] = (float) sprite.getXMax();
				textureCoords[vertexPointer * 12 + 11] = (float) sprite.getYMax();
				vertexPointer++;
			}
		}
		Zerra.logger().info("Generated " + Plate.SIZE + "x" + Plate.SIZE + " mesh in " + (System.currentTimeMillis() - lastTime) / 1000.0 + " seconds");

		this.generatedPlates.put(plate, new PlateMeshData(vertices, textureCoords));
	}

	public void prepare() {
		if (this.generatedPlates.size() > 0) {
			Map<Plate, PlateMeshData> map = new ConcurrentHashMap<Plate, PlateMeshData>(this.generatedPlates);
			for (Plate plate : map.keySet()) {
				PlateMeshData data = map.get(plate);
				if (plate.isLoaded()) {
					this.platesMesh.put(plate, Loader.loadToVAO(data.getPositions(), data.getTextureCoords(), 2));
				}
				this.requestedPlates.remove(plate);
				this.generatedPlates.remove(plate);
			}
			map.clear();
		}
	}

	@Nullable
	public Model getModel(Plate plate) {
		return !this.ready(plate) ? null : this.platesMesh.get(plate);
	}

	public boolean ready(Plate plate) {
		if (!plate.isLoaded()) {
			if (this.platesMesh.containsKey(plate)) {
				GL30.glDeleteVertexArrays(this.platesMesh.remove(plate).getVaoID());
			}
		} else {
			if (this.platesMesh.containsKey(plate)) {
				if (plate.requiresRenderUpdate() && !this.requestedPlates.contains(plate)) {
					this.requestedPlates.add(plate);
				}
				return true;
			}
			if (!this.requestedPlates.contains(plate)) {
				this.requestedPlates.add(plate);
				Zerra.getInstance().schedule(() -> this.generate2DPlateMesh(plate, this.requestedPlates.size() - 1));
			}
		}
		return false;
	}

	private static class PlateMeshData {

		private float[] positions;
		private float[] textureCoords;

		public PlateMeshData(float[] positions, float[] textureCoords) {
			this.positions = positions;
			this.textureCoords = textureCoords;
		}

		public float[] getPositions() {
			return positions;
		}

		public float[] getTextureCoords() {
			return textureCoords;
		}
	}
}