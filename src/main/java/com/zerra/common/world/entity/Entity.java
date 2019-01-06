package com.zerra.common.world.entity;

import org.joml.Vector3f;
import org.joml.Vector3i;

import com.zerra.common.world.entity.facing.Direction;

public interface Entity
{

	/**
	 * Getters and setters for tile positions of entities.
	 * 
	 * @return Coordinate point along the respective axis.
	 */
	public int getXTilePos();

	public int getYTilePos();

	public int getZTilePos();

	/**
	 * Loose positioning for entities, not tile bound.
	 * 
	 * @return Coordinate point along the respective axis.
	 */
	public float getXEntityPos();

	public float getYEntityPos();

	public float getZEntityPos();

	/**
	 * Get the tile position of the entity.
	 */
	public Vector3i getTilePosition();

	/**
	 * Get the loose position of the entity.
	 */
	public Vector3f getActualPosition();

	/**
	 * Velocity related functions.
	 */
	public void setVelocity(float xVel, float yVel, float zVel);

	public void addVelocity(float xVel, float yVel, float zVel);

	public Vector3f getVelocity();

	/**
	 * Sets the size of the entity.
	 * 
	 * @param width - The width of the entity.
	 * @param height
	 * @param anchorPoint
	 */
	public void setSize(float width, float height, float anchorPoint);

	public float getWidth();

	public float getHeight();

	/**
	 * All entities should have a facing direction.
	 */
	public Direction getFacingDirection();
	
	/**
	 * Get how far the entity is from another entity.
	 * 
	 * @param entity - The entity to get the distance to.
	 * @return The distance in tiles.
	 */
	public float getDistanceTo(Entity entity);
	
	/**
	 * Get how far the entity is from another entity.
	 * 
	 * @param entity - The entity to get the distance to.
	 * @return The distance in tiles.
	 */
	public float getDistanceTo(int xPos, int yPos, int zPos);
}
