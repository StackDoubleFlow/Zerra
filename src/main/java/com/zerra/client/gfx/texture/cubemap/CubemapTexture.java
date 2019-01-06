package com.zerra.client.gfx.texture.cubemap;

import com.zerra.client.gfx.texture.ITexture;
import com.zerra.client.util.Loader;

public class CubemapTexture implements ITexture {

	private int textureId;

	public CubemapTexture(int textureId) {
		this.textureId = textureId;
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
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}
}