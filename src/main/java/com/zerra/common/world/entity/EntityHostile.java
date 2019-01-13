package com.zerra.common.world.entity;

import com.zerra.common.world.storage.plate.WorldLayer;

public abstract class EntityHostile extends EntityLivingBase implements HostileEntity
{

	private Entity attackTarget;

    public EntityHostile(WorldLayer worldLayer) {
        super(worldLayer);
	}

	@Override
	public Entity getAttackTarget()
	{
		return this.attackTarget;
	}

	@Override
	public void setAttackTarget(Entity target)
	{
		this.attackTarget = target;
	}
}
