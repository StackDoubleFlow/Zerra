package com.zerra.client.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import com.zerra.client.Zerra;
import com.zerra.client.gfx.model.Model;
import com.zerra.client.gfx.texture.BasicTexture;
import com.zerra.client.gfx.texture.ITexture;
import com.zerra.client.gfx.texture.cubemap.CubeMapTextureData;
import com.zerra.client.gfx.texture.cubemap.CubemapTexture;

/**
 * <em><b>Copyright (c) 2019 The Zerra Team.</b></em>
 * 
 * <br>
 * </br>
 * 
 * Has the capability to load data to and from memory.
 * 
 * @author Ocelot5836
 */
public class Loader {

	private static List<Integer> vaos = new ArrayList<Integer>();
	private static List<Integer> vbos = new ArrayList<Integer>();
	private static List<Integer> textures = new ArrayList<Integer>();

	/**
	 * Deletes the vertex arrays, vertex buffer objects, and textures from memory.
	 */
	public static void cleanUp() {
		for (Integer vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}

		for (Integer vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}

		for (Integer texture : textures) {
			GL11.glDeleteTextures(texture);
		}
	}

	public static void deleteVertexArrays(int... ids) {
		GL30.glDeleteVertexArrays(ids);
		for (Integer vao : ids) {
			vaos.remove(vao);
		}
	}

	public static void deleteBuffers(int... ids) {
		GL15.glDeleteBuffers(ids);
		for (Integer vbo : ids) {
			vbos.remove(vbo);
		}
	}

	public static void deleteTextures(int... ids) {
		GL11.glDeleteTextures(ids);
		for (Integer texture : ids) {
			textures.remove(texture);
		}
	}

	/**
	 * Loads an image to a byte buffer. Used when loading textures.
	 * 
	 * @param image
	 *            The image to load to a byte buffer
	 * @return The buffer created from the image or null if the image was null
	 * @throws NullPointerException
	 *             Throws this if the image was null
	 */
	public static ByteBuffer loadToByteBuffer(BufferedImage image) throws NullPointerException {
		int width = image.getWidth();
		int height = image.getHeight();
		int[] pixels = new int[width * height];
		pixels = image.getRGB(0, 0, width, height, null, 0, width);

		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int color = pixels[x + y * width];
				buffer.put((byte) ((color >> 16) & 0xff));
				buffer.put((byte) ((color >> 8) & 0xff));
				buffer.put((byte) (color & 0xff));
				buffer.put((byte) ((color >> 24) & 0xff));
			}
		}
		buffer.flip();
		return buffer;
	}

	/**
	 * Loads a texture to memory.
	 *
	 * @param location
	 *            The location of said texture
	 * @return The texture created
	 */
	public static ITexture loadTexture(ResourceLocation location) {
		try {
			return Loader.loadTexture(ImageIO.read(location.getInputStream()));
		} catch (Exception e) {
			Zerra.logger().warn("Could not find image at \'" + location + "\'");
			return Loader.loadTexture(LoadingUtils.createMissingImage(256, 256));
		}
	}

	/**
	 * Loads a buffered image to memory.
	 *
	 * @param image
	 *            The image to load to memory
	 * @return The texture created
	 * @throws NullPointerException
	 *             Throws this if the image was null
	 */
	public static ITexture loadTexture(BufferedImage image) throws NullPointerException {
		int width = image.getWidth();
		int height = image.getHeight();

		ByteBuffer pixels = loadToByteBuffer(image);
		int textureID = GL11.glGenTextures();
		textures.add(textureID);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		// GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);

		return new BasicTexture(textureID, width, height);
	}

	/**
	 * Loads a font texture to memory.
	 *
	 * @param location
	 *            The location of said texture
	 * @return The texture created
	 */
	public static ITexture loadFontTexture(ResourceLocation location) {
		try {
			return Loader.loadFontTexture(ImageIO.read(location.getInputStream()));
		} catch (Exception e) {
			Zerra.logger().warn("Could not find image at \'" + location + "\'");
			return Loader.loadTexture(LoadingUtils.createMissingImage(256, 256));
		}
	}

	/**
	 * Loads a font buffered image to memory.
	 *
	 * @param image
	 *            The image to load to memory
	 * @return The texture created
	 * @throws NullPointerException
	 *             Throws this if the image was null
	 */
	public static ITexture loadFontTexture(BufferedImage image) throws NullPointerException {
		int width = image.getWidth();
		int height = image.getHeight();

		ByteBuffer pixels = loadToByteBuffer(image);
		int textureID = GL11.glGenTextures();
		textures.add(textureID);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);

		return new BasicTexture(textureID, width, height);
	}

	/**
	 * Loads a cube map texture to memory using the supplied images in the folder supplied.
	 *
	 * @param folder
	 *            The folder to get the images from
	 * @return The texture created
	 */
	public static ITexture loadCubemapTexture(ResourceLocation folder) {
		ResourceLocation right = new ResourceLocation(folder, "right.png");
		ResourceLocation left = new ResourceLocation(folder, "left.png");
		ResourceLocation top = new ResourceLocation(folder, "top.png");
		ResourceLocation bottom = new ResourceLocation(folder, "bottom.png");
		ResourceLocation back = new ResourceLocation(folder, "back.png");
		ResourceLocation front = new ResourceLocation(folder, "front.png");
		return loadCubemapTexture(LoadingUtils.loadImage(right.toString(), right.getInputStream()), LoadingUtils.loadImage(left.toString(), left.getInputStream()), LoadingUtils.loadImage(top.toString(), top.getInputStream()), LoadingUtils.loadImage(bottom.toString(), bottom.getInputStream()), LoadingUtils.loadImage(back.toString(), back.getInputStream()), LoadingUtils.loadImage(front.toString(), front.getInputStream()));
	}

	/**
	 * Loads a cube map texture to memory using all the images supplied.
	 *
	 * @param right
	 *            The image to be applied to the right
	 * @param left
	 *            The image to be applied to the left
	 * @param top
	 *            The image to be applied to the top
	 * @param bottom
	 *            The image to be applied to the bottom
	 * @param back
	 *            The image to be applied to the back
	 * @param front
	 *            The image to be applied to the front
	 * @return The texture created
	 * @throws NullPointerException
	 *             If any image was null
	 */
	public static ITexture loadCubemapTexture(BufferedImage right, BufferedImage left, BufferedImage top, BufferedImage bottom, BufferedImage back, BufferedImage front) throws NullPointerException {
		int textureID = GL11.glGenTextures();
		textures.add(textureID);

		CubeMapTextureData[] datas = new CubeMapTextureData[6];
		datas[0] = new CubeMapTextureData(loadToByteBuffer(right), right.getWidth(), right.getHeight());
		datas[1] = new CubeMapTextureData(loadToByteBuffer(left), left.getWidth(), left.getHeight());
		datas[2] = new CubeMapTextureData(loadToByteBuffer(top), top.getWidth(), top.getHeight());
		datas[3] = new CubeMapTextureData(loadToByteBuffer(bottom), bottom.getWidth(), bottom.getHeight());
		datas[4] = new CubeMapTextureData(loadToByteBuffer(back), back.getWidth(), back.getHeight());
		datas[5] = new CubeMapTextureData(loadToByteBuffer(front), front.getWidth(), front.getHeight());

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID);
		for (int i = 0; i < datas.length; i++) {
			CubeMapTextureData data = datas[i];
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);

		return new CubemapTexture(textureID);
	}

	/**
	 * Loads a sound into memory.
	 *
	 * @param location
	 *            The location of the sound
	 * @param buffer
	 *            The buffer that will hold the sound
	 * @return The length of the audio file
	 * @throws UnsupportedAudioFileException
	 *             If the type of audio is not supported
	 * @throws IOException
	 *             If the file could not be found
	 */
	public static long loadSound(ResourceLocation location, int buffer) throws UnsupportedAudioFileException, IOException {
		// shortcut finals:
		final int MONO = 1, STEREO = 2;

		AudioInputStream stream = AudioSystem.getAudioInputStream(location.getInputStream());
		AudioFormat format = stream.getFormat();
		if (format.isBigEndian())
			throw new UnsupportedAudioFileException("Can't handle Big Endian formats yet");

		// load stream into byte buffer
		int openALFormat = -1;
		switch (format.getChannels()) {
		case MONO:
			switch (format.getSampleSizeInBits()) {
			case 8:
				openALFormat = AL10.AL_FORMAT_MONO8;
				break;
			case 16:
				openALFormat = AL10.AL_FORMAT_MONO16;
				break;
			}
			break;
		case STEREO:
			switch (format.getSampleSizeInBits()) {
			case 8:
				openALFormat = AL10.AL_FORMAT_STEREO8;
				break;
			case 16:
				openALFormat = AL10.AL_FORMAT_STEREO16;
				break;
			}
			break;
		}

		// load data into a byte buffer
		byte[] b = IOUtils.toByteArray(stream);
		ByteBuffer data = BufferUtils.createByteBuffer(b.length).put(b);
		data.flip();

		// load audio data into appropriate system space....
		AL10.alBufferData(buffer, openALFormat, data, (int) format.getSampleRate());

		// and return the rough notion of length for the audio stream!
		return (long) (1000f * stream.getFrameLength() / format.getFrameRate());
	}

	/**
	 * Generates a new vertex array.
	 * 
	 * @return The id of the array
	 */
	public static int generateVertexArray() {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		return vaoID;
	}

	/**
	 * Loads the supplied data to a VAO.
	 *
	 * @param positions
	 *            The positions to load
	 * @param indices
	 *            The indices to load
	 * @param textureCoords
	 *            The texture coords to load
	 * @param normals
	 *            The normals to load
	 * @return The model created
	 */
	public static Model loadToVAO(float[] positions, int[] indices, float[] textureCoords, float[] normals) {
		int vaoID = generateVertexArray();
		GL30.glBindVertexArray(vaoID);
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		GL30.glBindVertexArray(0);
		return new Model(vaoID, indices.length, true);
	}

	/**
	 * Loads different dimensions of positions to memory.
	 *
	 * @param positions
	 *            The positions to load
	 * @param textureCoords
	 *            The texture coords to load
	 * @param dimensions
	 *            The dimensions of plane. Ex 3d coords will be 3 and 2d coords are 2
	 * @return The model created
	 */
	public static Model loadToVAO(float[] positions, float[] textureCoords, int dimensions) {
		int vaoID = generateVertexArray();
		GL30.glBindVertexArray(vaoID);
		storeDataInAttributeList(0, dimensions, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		GL30.glBindVertexArray(0);
		return new Model(vaoID, positions.length / dimensions, false);
	}

	/**
	 * Loads different dimensions of positions to memory.
	 *
	 * @param positions
	 *            The positions to load
	 * @param indices
	 *            The indices to load
	 * @param textureCoords
	 *            The texture coords to load
	 * @param dimensions
	 *            The dimensions of plane. Ex 3d coords will be 3 and 2d coords are 2
	 * @return The model created
	 */
	public static Model loadToVAO(float[] positions, int[] indices, float[] textureCoords, int dimensions) {
		int vaoID = generateVertexArray();
		GL30.glBindVertexArray(vaoID);
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, dimensions, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		GL30.glBindVertexArray(0);
		return new Model(vaoID, indices.length, true);
	}

	/**
	 * Loads different dimensions of positions to memory.
	 *
	 * @param positions
	 *            The positions to load
	 * @param indices
	 *            The indices to load
	 * @param dimensions
	 *            The dimensions of plane. Ex 3d coords will be 3 and 2d coords are 2
	 * @return The model created
	 */
	public static Model loadToVAO(float[] positions, int[] indices, int dimensions) {
		int vaoID = generateVertexArray();
		GL30.glBindVertexArray(vaoID);
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, dimensions, positions);
		GL30.glBindVertexArray(0);
		return new Model(vaoID, indices.length, true);
	}

	/**
	 * Loads different dimensions of positions to memory.
	 *
	 * @param positions
	 *            The positions to load
	 * @param dimensions
	 *            The dimensions of plane. Ex 3d coords will be 3 and 2d coords are 2
	 * @return The model created
	 */
	public static Model loadToVAO(float[] positions, int dimensions) {
		int vaoID = generateVertexArray();
		GL30.glBindVertexArray(vaoID);
		storeDataInAttributeList(0, dimensions, positions);
		GL30.glBindVertexArray(0);
		return new Model(vaoID, positions.length / dimensions, false);
	}

	/**
	 * Creates an empty VBO that can be used to dynamically update with.
	 * 
	 * @param floatCount
	 *            The number of floats inside
	 * @return The VBO created
	 */
	public static int createEmptyVBO(int floatCount) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vboID;
	}

	/**
	 * Stores an instanced attribute into the supplied VBO.
	 * 
	 * @param vao
	 *            The VAO to load the VBO into
	 * @param vbo
	 *            The VBO that will hold the data
	 * @param attributeNumber
	 *            The position this will be held at
	 * @param dataSize
	 *            The size of the data
	 * @param instancedDataLength
	 *            The length of each point of data.
	 * @param offset
	 *            The offset of the data
	 */
	public static void storeInstancedDataInAttributeList(int vao, int vbo, int attributeNumber, int dataSize, int instancedDataLength, int offset) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL30.glBindVertexArray(vao);
		GL20.glVertexAttribPointer(attributeNumber, dataSize, GL11.GL_FLOAT, false, instancedDataLength * 4, offset * 4);
		GL33.glVertexAttribDivisor(attributeNumber, 1);
		GL30.glBindVertexArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	/**
	 * Updates VBO data.
	 * 
	 * @param vbo
	 *            The VBO that is being updated
	 * @param data
	 *            The data that is going to replace the old data
	 * @param buffer
	 *            The buffer to put the data into
	 */
	public static void updateVboData(int vbo, float[] data, FloatBuffer buffer) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * 4, GL15.GL_DYNAMIC_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	/**
	 * Stores data in an attribute list. Uses the currently bound VAO.
	 * 
	 * @param attributeNumber
	 *            The position this will be held at
	 * @param dataSize
	 *            The size of the data
	 * @param data
	 *            The data to put into the list
	 */
	public static void storeDataInAttributeList(int attributeNumber, int dataSize, float[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = Buffers.storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, dataSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private static void bindIndicesBuffer(int[] indices) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = Buffers.storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
}