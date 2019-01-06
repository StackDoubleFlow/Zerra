package com.zerra.client.gfx.texture;

import com.zerra.client.util.Loader;

public class BasicTexture implements ITexture {

	private int textureId;
	private int width;
	private int height;

	public BasicTexture(int textureId, int width, int height) {
		this.textureId = textureId;
		this.width = width;
		this.height = height;
	}

	@Override
	public void delete() {
		if (this.textureId != -1) {
			Loader.deleteTextures(this.textureId);
			this.textureId = -1;
		}
	}

	@Override
	public int getId() {
		return textureId;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
}