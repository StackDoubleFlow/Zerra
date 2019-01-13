package com.zerra.common.world.storage.plate;

import com.zerra.common.world.World;
import com.zerra.common.world.entity.Entity;
import com.zerra.common.world.storage.Layer;
import com.zerra.common.world.tile.Tiles;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class WorldLayer implements Layer {

	private World world;
	private int layer;
    private Map<Vector3ic, Plate> loadedPlates;
    private List<Vector3ic> loadingPlates;
	private Set<Entity> loadedEntities;

	public WorldLayer(World world, int layer) {
		this.world = world;
		this.layer = layer;
        this.loadedPlates = new ConcurrentHashMap<Vector3ic, Plate>();
        this.loadingPlates = new ArrayList<Vector3ic>();
        this.loadedEntities = new HashSet<Entity>();
    }

    @Override
    public World getWorld() {
        return this.world;
	}

	private Plate generate(Vector3i pos) {
		Plate plate = new Plate(this);
		plate.setPlatePos(new Vector3i(pos));
		plate.fill(0, () -> this.world.getRandom().nextInt(3) == 0 ? Tiles.STONE : this.world.getRandom().nextInt(2) == 0 ? Tiles.GRASS : Tiles.SAND);
		return plate;
	}

	@Override
    public void loadPlate(Vector3ic pos) {
		Vector3i platePos = new Vector3i(pos);
		if (!this.loadingPlates.contains(platePos) && !this.isPlateLoaded(platePos)) {
            this.world.logger().info("Loaded plate at " + pos.x() + ", " + pos.y() + ", " + pos.z() + " in layer " + this.layer);
			this.loadingPlates.add(platePos);
			if (this.world.getStorageManager().isPlateGenerated(this.layer, platePos)) {
				this.world.schedule(() -> {
					this.loadedEntities.addAll(this.world.getStorageManager().readEntitiesSafe(this.layer, platePos));
					this.loadedPlates.put(platePos, this.world.getStorageManager().readPlateSafe(this.layer, platePos));
					this.loadingPlates.remove(platePos);
				});
			} else {
				this.world.schedule(() -> {
					this.loadedPlates.put(platePos, this.generate(platePos));
					this.loadingPlates.remove(platePos);
				});
			}
		}
	}

	@Override
    public void unloadPlate(Vector3ic pos) {
        this.world.logger().info("Unloaded plate at " + pos.x() + ", " + pos.y() + ", " + pos.z() + " in layer " + this.layer);
        Plate plate = this.getPlate(pos);
        if (plate != null) {
			plate.unload();
			this.world.schedule(() -> {
				this.world.save(this.layer, plate.getPlatePos());
                this.loadedEntities.removeIf(entity -> plate.isInsidePlate(entity.getTilePosition(), entity.getLayer()));
				this.loadedPlates.remove(pos);
			});
		}
	}

	@Override
	public Plate[] getLoadedPlates() {
		return this.loadedPlates.values().toArray(new Plate[0]);
	}

	@Override
	public Set<Entity> getEntities() {
		return loadedEntities;
	}

	@Override
	public Set<Entity> getEntities(Plate plate)
	{
        return loadedEntities.stream().filter(entity -> plate.isInsidePlate(entity.getTilePosition(), entity.getLayer())).collect(Collectors.toSet());
	}

	@Override
	public int getLayerId() {
		return layer;
	}

	@Override
    public Plate getPlate(Vector3ic pos) {
		if (!this.isPlateLoaded(pos))
			this.loadPlate(pos);
		return this.loadedPlates.get(pos);
	}

	@Override
    public boolean isPlateLoaded(Vector3ic pos) {
		return this.loadedPlates.containsKey(pos);
	}
}
