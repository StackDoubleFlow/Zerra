package com.zerra.common.world;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3i;

import com.zerra.common.world.storage.IOManager.WorldStorageManager;
import com.zerra.client.Zerra;
import com.zerra.common.world.gamevents.listeners.PlateLoadEventListener;
import com.zerra.common.world.storage.Layer;
import com.zerra.common.world.storage.plate.Plate;
import com.zerra.common.world.storage.plate.WorldLayer;

public class World {

	private Logger logger;
	private String name;
	private Random random;
	private Layer[] layers;
	private WorldStorageManager storageManager;
	private ExecutorService pool;

	public World(String name) {
		this.logger = LogManager.getLogger("World-" + name);
		this.name = name;
		this.random = new Random();
		this.random.setSeed(random.nextLong());
		this.layers = new Layer[6];
		for (int i = 0; i < 6; i++) {
			this.layers[i] = new WorldLayer(this, i);
		}
		this.storageManager = new WorldStorageManager(this);
		this.pool = Executors.newCachedThreadPool();
		Zerra.getInstance().getEventHandler().registerEventListener(new PlateLoadEventListener());
	}

	public void schedule(Runnable command) {
		this.pool.execute(command);
	}

	public void stop() {
		for (int i = 0; i < this.layers.length; i++) {
			int layerId = i;
			this.pool.execute(() -> this.save(layerId));
		}
		this.pool.shutdown();
	}

	public void save(int layerId) {
		long startTime = System.currentTimeMillis();
		Layer layer = this.getLayer(layerId);
		if (layer != null) {
			for (Plate plate : layer.getLoadedPlates()) {
				this.storageManager.writePlateSafe(layerId, plate);
			}
		}
		this.logger.info("Saved layer \'" + layerId + "\' in " + (double) (System.currentTimeMillis() - startTime) / 1000.0 + "s");
	}

	public void save(int layerId, Vector3i pos) {
		Layer layer = this.getLayer(layerId);
		if (layer != null) {
			if (layer.isPlateLoaded(pos)) {
				this.storageManager.writePlateSafe(layerId, layer.getPlate(pos));
			}
		}
	}

	@Nullable
	public Plate load(int layerId, Vector3i pos) {
		Layer layer = this.getLayer(layerId);
		if (layer != null) {
			if (layer.isPlateLoaded(pos)) {
				return this.storageManager.readPlateSafe(layerId, pos);
			}
		}
		return null;
	}

	public Logger logger() {
		return logger;
	}

	public String getName() {
		return name;
	}

	public Random getRandom() {
		return random;
	}

	public Layer[] getLayers() {
		return layers;
	}

	@Nullable
	public Layer getLayer(int layer) {
		if (layer < 0 || layer >= this.layers.length)
			return null;
		return this.layers[layer];
	}

	public WorldStorageManager getStorageManager() {
		return storageManager;
	}
}