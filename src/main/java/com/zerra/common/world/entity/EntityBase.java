package com.zerra.common.world.entity;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import com.zerra.common.world.World;
import com.zerra.common.world.entity.facing.Direction;

public abstract class EntityBase implements Entity
{

	World world;

	private Vector3f entityPosition = new Vector3f();
	private Vector3i tilePosition = new Vector3i();
	private Vector3f velocity = new Vector3f();

	private Vector2f entitySize = new Vector2f();

	// Used in determining starting point of collision box sizing.
	private float anchorPoint = 0f;

	private Direction direction;

	public EntityBase(World world)
	{
		// Set world.
		this.world = world;

		// Set to origin.
		this.entityPosition.set(0f, 0f, 0f);
		this.tilePosition.set(Math.round(this.entityPosition.x()), Math.round(this.entityPosition.z()), Math.round(this.entityPosition.z()));

		// Set direction.
		this.direction = Direction.SOUTH;

		// Set velocity.
		this.velocity.set(0f, 0f, 0f);
	}

	@Override
	public Direction getFacingDirection()
	{
		return this.direction;
	}

	@Override
	public int getXTilePos()
	{
		return this.tilePosition.x;
	}

	@Override
	public int getYTilePos()
	{
		return this.tilePosition.y;
	}

	@Override
	public int getZTilePos()
	{
		return this.tilePosition.z;
	}

	@Override
	public float getXEntityPos()
	{
		return this.entityPosition.x;
	}

	@Override
	public float getYEntityPos()
	{
		return this.entityPosition.y;
	}

	@Override
	public float getZEntityPos()
	{
		return this.entityPosition.z;
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
		this.entitySize.x = width;
		this.entitySize.y = height;
		this.anchorPoint = anchorPoint;
	}

	@Override
	public float getWidth()
	{
		return this.entitySize.x();
	}

	@Override
	public float getHeight()
	{
		return this.entitySize.y();
	}

	@Override
	public void setVelocity(float xVel, float yVel, float zVel)
	{
		this.velocity.x = xVel;
		this.velocity.y = yVel;
		this.velocity.z = zVel;
	}

	@Override
	public void addVelocity(float xVel, float yVel, float zVel)
	{
		this.velocity.x += xVel;
		this.velocity.y += yVel;
		this.velocity.z += zVel;
	}

	@Override
	public Vector3f getVelocity()
	{
		return this.velocity;
	}
	
	@Override
	public float getDistanceTo(Entity entity)
	{
		return (float) Math.sqrt(Math.pow(entity.getXEntityPos() - this.entityPosition.x, 2) + Math.pow(entity.getYEntityPos() - this.entityPosition.y, 2)
				+ Math.pow(entity.getZEntityPos() - this.entityPosition.z, 2));
	}
	
	@Override
	public float getDistanceTo(int xPos, int yPos, int zPos)
	{
		return (float) Math.sqrt(Math.pow(xPos - this.entityPosition.x, 2) + Math.pow(yPos - this.entityPosition.y, 2) + Math.pow(zPos - this.entityPosition.z, 2));
	}
}
