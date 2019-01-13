package com.zerra.common.world.storage;

import com.zerra.common.world.World;
import com.zerra.common.world.entity.Entity;
import com.zerra.common.world.storage.plate.Plate;
import org.joml.Vector3ic;

import javax.annotation.Nullable;
import java.util.Set;

public interface Layer {

    /**
     * Gets the world this layer is in
     *
     * @return World
     */
    World getWorld();

	/**
	 * Loads a plate either from file or creates a new one.
     */
    void loadPlate(Vector3ic pos);

	/**
	 * Unloads the plate and writes it to file.
     */
    void unloadPlate(Vector3ic pos);
	
	/**
	 * @return The layer this is in the world
	 */
	int getLayerId();

	/**
	 * @return All of the plates currently loaded
	 */
	Plate[] getLoadedPlates();

    /**
     * Gets all of the entities currently loaded in this layer
     *
     * @return All entities loaded
     */
	Set<Entity> getEntities();

	/**
	 * Gets the entities loaded on the given plate
	 *
	 * @param plate the plate
	 * @return All entities currently on the plate
	 */
	Set<Entity> getEntities(Plate plate);

	/**
	 * Checks the loaded plates for the plate with the specified pos and loads it if it can not be found.
	 * 
	 * @param pos
	 *            The pos to check
	 * @return The plate found or null if it is loading
	 */
    @Nullable
    Plate getPlate(Vector3ic pos);

	/**
	 * Checks to see it a plate is loaded at the specified pos.
	 * 
	 * @param pos
	 *            The pos to check
	 * @return Whether or not the plate is loaded at the pos
     */
    boolean isPlateLoaded(Vector3ic pos);
}
