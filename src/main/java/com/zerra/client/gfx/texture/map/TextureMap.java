package com.zerra.client.gfx.texture.map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.zerra.client.Zerra;
import com.zerra.client.gfx.texture.ITexture;
import com.zerra.client.gfx.texture.TextureManager;
import com.zerra.client.util.Loader;
import com.zerra.client.util.LoadingUtils;
import com.zerra.client.util.ResourceLocation;
import com.zerra.common.world.storage.IOManager;

public class TextureMap implements ITexture {

	private ResourceLocation location;
	private int textureId;
	private int width;
	private int height;
	private List<ResourceLocation> spriteLocations;
	private Map<ResourceLocation, TextureMapSprite> sprites;

	public TextureMap(ResourceLocation location, TextureManager textureManager) {
		this.location = location;
		this.textureId = GL11.glGenTextures();
		this.width = 128;
		this.height = 128;
		this.spriteLocations = new ArrayList<ResourceLocation>();
		this.sprites = new HashMap<ResourceLocation, TextureMapSprite>();
		textureManager.loadTexture(location, this);
	}

	@Override
	public void delete() {
		if (this.textureId != -1) {
			Loader.deleteTextures(this.textureId);
			this.textureId = -1;
		}
	}

	public void stitch() {
		int startingSize = 128;
		Map<Vector2i, List<BufferedImage>> imageBatch = new HashMap<Vector2i, List<BufferedImage>>();
		Map<BufferedImage, ResourceLocation> foundImages = new HashMap<BufferedImage, ResourceLocation>();

		BufferedImage missingImage = LoadingUtils.createMissingImage(16, 16);
		this.addImage(missingImage, imageBatch);
		foundImages.put(missingImage, TextureManager.MISSING_TEXTURE_LOCATION);

		long startTime = System.currentTimeMillis();
		Zerra.logger().info("Stitching " + (this.spriteLocations.size() + 1) + " sprite(s) into atlas \'" + this.location.toString() + "\'");
		for (ResourceLocation location : this.spriteLocations) {
			try {
				BufferedImage image = ImageIO.read(location.getInputStream());
				this.addImage(image, imageBatch);
				foundImages.put(image, location);
				if (image.getWidth() > startingSize)
					startingSize = image.getWidth();
				if (image.getHeight() > startingSize)
					startingSize = image.getHeight();
			} catch (Exception e) {
				Zerra.logger().warn("Could not find atlas sprite at \'" + location + "\'. Skipping!");
			}
		}

		List<Vector2i> orderedSizes = imageBatch.keySet().stream().sorted((o1, o2) -> {
			return o2.x * o2.y - o1.x * o1.y;
		}).collect(Collectors.toList());

		int startingExponent = (int) Math.ceil(Math.log(startingSize) / Math.log(2));
		int currentWidth = (int) Math.pow(2, startingExponent + 1);
		int currentHeight = (int) Math.pow(2, startingExponent + 1);
		int xStart = 0;
		int yStart = 0;
		int currentY = 0;
		int xPointer = 0;
		int yPointer = 0;
		Vector2i previousSize = new Vector2i();
		BufferedImage atlas = new BufferedImage(currentWidth, currentHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = atlas.createGraphics();

		try {
			for (Vector2i size : orderedSizes) {
				List<BufferedImage> images = imageBatch.get(size);
				if (images != null) {
					for (BufferedImage image : images) {
						if (xPointer + image.getWidth() > currentWidth) {
							yPointer = yStart;
							xPointer = xStart;
							currentY = yStart;
							yStart = 0;
						} else {
							if (yStart == 0 || image.getHeight() < yStart) {
								yStart = currentY + image.getHeight();
							}
							if (previousSize.y > image.getHeight()) {
								xStart = xPointer;
							}
						}
						if (yPointer + image.getHeight() > currentHeight) {
							xStart = (int) Math.pow(2, startingExponent + 1);
							xPointer = xStart;
							yPointer = 0;
							yStart = 0;
							currentY = 0;
							startingExponent++;
							currentWidth = (int) Math.pow(2, startingExponent + 1);
							currentHeight = (int) Math.pow(2, startingExponent + 1);
							BufferedImage newImage = new BufferedImage(currentWidth, currentHeight, BufferedImage.TYPE_INT_ARGB);
							for (int y = 0; y < atlas.getHeight(); y++) {
								for (int x = 0; x < atlas.getWidth(); x++) {
									newImage.setRGB(x, y, atlas.getRGB(x, y));
								}
							}
							atlas = newImage;
							g.dispose();
							g = atlas.createGraphics();
						}
						g.drawImage(image, xPointer, yPointer, null);
						this.sprites.put(foundImages.get(image), new TextureMapSprite(xPointer, yPointer, image.getWidth(), image.getHeight()));
						xPointer += image.getWidth();
						previousSize.x = image.getWidth();
						previousSize.y = image.getHeight();
					}
				}
			}
		} catch (Throwable t) {
			Zerra.logger().fatal("Could not create texture atlas \'" + this.location + "\'!", t);
			Zerra.getInstance().stop();
			return;
		}
		g.dispose();

		this.width = atlas.getWidth();
		this.height = atlas.getHeight();
		for (ResourceLocation location : this.sprites.keySet()) {
			this.sprites.get(location).setAtlasSize(this.width, this.height);
		}

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
		// GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, this.width, this.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, Loader.loadToByteBuffer(atlas));

		Zerra.logger().info("Created " + atlas.getWidth() + "x" + atlas.getHeight() + " atlas in " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds");
		try {
			File file = new File(IOManager.getInstanceDirectory(), "debug/atlas/" + this.location.getDomain() + "-" + this.location.getLocation() + ".png");
			FileUtils.touch(file);
			ImageIO.write(atlas, "PNG", new FileOutputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addImage(BufferedImage image, Map<Vector2i, List<BufferedImage>> imageBatch) {
		Vector2i size = new Vector2i(image.getWidth(), image.getHeight());
		List<BufferedImage> list = imageBatch.get(size);
		if (list == null) {
			list = new ArrayList<BufferedImage>();
			imageBatch.put(size, list);
		}
		list.add(image);
	}

	public void register(ResourceLocation location) {
		this.spriteLocations.add(location);
	}

	public TextureMapSprite getSprite(ResourceLocation location) {
		return this.sprites.getOrDefault(location, this.sprites.get(TextureManager.MISSING_TEXTURE_LOCATION));
	}

	public ResourceLocation getLocation() {
		return location;
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