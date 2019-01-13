package com.zerra.common.world.entity;

public interface HostileEntity
{
	/**
	 * @return Gets the target this entity is attacking.
	 */
	public Entity getAttackTarget();
	
	public void setAttackTarget(Entity entity);
}
