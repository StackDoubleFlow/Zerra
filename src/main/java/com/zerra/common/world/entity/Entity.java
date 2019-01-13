package com.zerra.common.world.entity;

import com.zerra.common.event.entity.EntityEvent;
import com.zerra.common.event.entity.EntityUpdateEvent;
import com.zerra.common.world.World;
import com.zerra.common.world.entity.facing.Direction;
import com.zerra.common.world.storage.plate.WorldLayer;
import org.joml.*;

import java.lang.Math;

public abstract class Entity {

    protected World world;

    private String registryName;

    private int ticksExisted = 0;

    private int layer;
    private Vector3f entityPosition = new Vector3f();
    private Vector2i tilePosition = new Vector2i();
    private Vector3f velocity = new Vector3f();

    private Vector2f entitySize = new Vector2f();

    private boolean isInvisible;
    private boolean isInWater;
    private boolean isOnFire;

    // Used in determining starting point of collision box sizing.
    private float anchorPoint = 0f;

    private Direction direction;

    public Entity(WorldLayer worldLayer) {
        // Set world.
        this.world = worldLayer.getWorld();

        // Set layer
        this.layer = worldLayer.getLayerId();

        // Set to origin.
        this.entityPosition.set(0f, 0f, 0f);
        this.tilePosition.set(0, 0);

        // Set direction.
        this.direction = Direction.SOUTH;

        // Set velocity.
        this.velocity.set(0F, 0F, 0F);

        this.init();
    }

    /**
     * Sets the registry name for this entity
     *
     * @param name The registry name
     */
    public void setRegistryName(String name) {
        registryName = name;
    }

    /**
     * Gets the registry name for this entity
     *
     * @return The registry name
     */
    public String getRegistryName() {
        return registryName;
    }

    /**
     * Updates the entity per tick.
     */
    public void update() {
        EntityUpdateEvent updateEvent = EntityEvent.onEntityUpdate(this);
        updateEvent.call();
        if (updateEvent.isCancelled())
            return;
        this.ticksExisted++;
    }

    /**
     * Called during entity construction.
     */
    public void init() {

    }

    /**
     * Gets the facing of this entity
     *
     * @return Entity facing
     */
    public Direction getFacingDirection() {
        return this.direction;
    }

    /**
     * Sets the facing of this entity
     *
     * @param direction Entity facing
     */
    public void setFacingDirection(Direction direction) {
        this.direction = direction;
    }

    public void setPosition(Vector3fc position) {
        this.entityPosition.set(position);
        this.tilePosition.set((int) Math.floor(position.x()), (int) Math.floor(position.z()));
    }

    /**
     * Gets this entity's tile position X component
     *
     * @return Tile position X component
     */
    public int getXTilePos() {
        return this.tilePosition.x();
    }

    /**
     * Gets this entity's tile position Y component
     *
     * @return Tile position Y component
     */
    public int getYTilePos() {
        return this.tilePosition.y();
    }

    /**
     * Gets this entity's actual position X component
     *
     * @return Actual position X component
     */
    public float getXEntityPos() {
        return this.entityPosition.x();
    }

    /**
     * Gets this entity's actual position Y component
     *
     * @return Actual position Y component
     */
    public float getYEntityPos() {
        return this.entityPosition.y();
    }

    /**
     * Gets this entity's actual position Z component
     *
     * @return Actual position Z component
     */
    public float getZEntityPos() {
        return this.entityPosition.z();
    }

    /**
     * Gets the tile position that this entity is currently at as a read-only object
     *
     * @return Tile position
     */
    public Vector2ic getTilePosition() {
        return this.tilePosition;
    }

    /**
     * Gets the actual position of this entity as a read-only object
     *
     * @return Actual position
     */
    public Vector3fc getActualPosition() {
        return this.entityPosition;
    }

    /**
     * Gets the layer that this entity is in
     *
     * @return World layer
     */
    public int getLayer() {
        return layer;
    }

    /**
     * Sets the size of the entity.
     *
     * @param width       Entity width
     * @param height      Entity height
     * @param anchorPoint Entity collision box start point offset
     */
    public void setSize(float width, float height, float anchorPoint) {
        this.entitySize.x = width;
        this.entitySize.y = height;
        this.anchorPoint = anchorPoint;
    }

    /**
     * Gets the entity's width
     *
     * @return Entity width
     */
    public float getWidth() {
        return this.entitySize.x();
    }

    /**
     * Gets the entity's height
     *
     * @return Entity height
     */
    public float getHeight() {
        return this.entitySize.y();
    }

    /**
     * Sets the velocity of this entity
     *
     * @param velocity Velocity
     */
    public void setVelocity(Vector3fc velocity) {
        this.velocity.set(velocity);
    }

    /**
     * Adds to the velocity of this entity
     *
     * @param velocity Velocity to add
     */
    public void addVelocity(Vector3fc velocity) {
        this.velocity.add(velocity);
    }

    /**
     * Gets the velocity of this entity as a read-only object
     *
     * @return Entity velocity
     */
    public Vector3fc getVelocity() {
        return this.velocity;
    }

    /**
     * Calculates the distance of this entity to another entity
     *
     * @param entity Other entity
     * @return Distance to the other entity
     */
    public float getDistanceTo(Entity entity) {
        return getDistanceTo(entity.getActualPosition());
    }

    /**
     * Calculates the distance of this entity to a position in the current world layer
     *
     * @param pos Position in the world layer
     * @return Distance to the position
     */
    public float getDistanceTo(Vector3fc pos) {
        return (float) Math.sqrt(Math.pow(pos.x() - this.getXEntityPos(), 2) +
                Math.pow(pos.y() - this.getYEntityPos(), 2) +
                Math.pow(pos.z() - this.getZEntityPos(), 2));
    }

    public void spawn() {
    }

    public void despawn() {
        // TODO: Save entity to data on spawn IF it should be saved.
    }

    /**
     * Gets if this entity is invisible
     *
     * @return If this entity is invisible
     */
    public boolean isInvisible() {
        return isInvisible;
    }

    /**
     * Sets if this entity should be invisible
     *
     * @param invisible Whether this entity should be invisible
     */
    public void setInvisible(boolean invisible) {
        isInvisible = invisible;
    }

    /**
     * Gets if this entity is in water
     *
     * @return If this entity is in water
     */
    public boolean isInWater() {
        return this.isInWater;
    }

    /**
     * Sets if this entity is in water
     *
     * @param isInWater Whether this entity is in water
     */
    public void setInWater(boolean isInWater) {
        this.isInWater = isInWater;
    }

    /**
     * Gest if this entity is on fire
     *
     * @return If this entity is on fire
     */
    public boolean isOnFire() {
        return this.isOnFire;
    }

    /**
     * Sets if this entity should be on fire
     *
     * @param isOnFire Whether this entity should be on fire
     */
    public void setOnFire(boolean isOnFire) {
        this.isOnFire = isOnFire;
    }

    /**
     * Gest if this entity can be pushed
     *
     * @return If this entity can be pushed
     */
    public boolean canBePushed() {
        return true;
    }

    /**
     * Gest if this entity can spawn at the current position
     *
     * @return If this entity can spawn at the current position
     */
    public boolean canSpawnHere() {
        return true;
    }

    /**
     * Gets how many ticks this entity has existed for
     *
     * @return How many ticks this entity has existed for
     */
    public int getTicksExisted() {
        return ticksExisted;
    }
}
