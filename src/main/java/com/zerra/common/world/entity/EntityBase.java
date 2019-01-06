package com.zerra.common.world.entity;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import com.zerra.common.world.World;
import com.zerra.common.world.entity.facing.Direction;

public abstract class EntityBase implements Entity
{

	World					world;

	private Vector3f		entityPosition	= new Vector3f();
	private Vector3i		tilePosition	= new Vector3i();
	private Vector3f		velocity		= new Vector3f();

	private Vector2f		entitySize		= new Vector2f();

	// Used in determining starting point of collision box sizing.
	private float			anchorPoint		= 0f;

	private Direction	direction;

	public EntityBase(World world)
	{
		// Set world.
		this.world = world;

		// Set to origin.
		entityPosition.set(0f, 0f, 0f);
		tilePosition.set(Math.round(entityPosition.x()), Math.round(entityPosition.z()), Math.round(entityPosition.z()));

		// Set direction.
		direction = Direction.SOUTH;

		// Set velocity.
		velocity.set(0f, 0f, 0f);
	}

	@Override
	public Direction getFacingDirection()
	{
		return direction;
	}

	@Override
	public int getXTilePos()
	{
		return tilePosition.x;
	}

	@Override
	public int getYTilePos()
	{
		return tilePosition.y;
	}

	@Override
	public int getZTilePos()
	{
		return tilePosition.z;
	}

	@Override
	public float getXEntityPos()
	{
		return entityPosition.x;
	}

	@Override
	public float getYEntityPos()
	{
		return entityPosition.y;
	}

	@Override
	public float getZEntityPos()
	{
		return entityPosition.z;
	}

	@Override
	public Vector3i getTilePosition()
	{
		return this.tilePosition;
	}

	@Override
	public Vector3f getActualPosition()
	{
		return this.entityPosition;
	}

	@Override
	public void setSize(float width, float height, float anchorPoint)
	{
		entitySize.x = width;
		entitySize.y = height;
		this.anchorPoint = anchorPoint;
	}

	@Override
	public float getWidth()
	{
		return entitySize.x();
	}

	@Override
	public float getHeight()
	{
		return entitySize.y();
	}

	@Override
	public void setVelocity(float xVel, float yVel, float zVel)
	{
		velocity.x = xVel;
		velocity.y = yVel;
		velocity.z = zVel;
	}

	@Override
	public void addVelocity(float xVel, float yVel, float zVel)
	{
		velocity.x += xVel;
		velocity.y += yVel;
		velocity.z += zVel;
	}

	@Override
	public Vector3f getVelocity()
	{
		return velocity;
	}
}
