package com.zerra.common.world.entity;

import com.zerra.common.event.entity.EntityDeathEvent;
import com.zerra.common.event.entity.EntityLivingEvent;
import com.zerra.common.world.entity.attrib.SharedLivingAttributes;
import com.zerra.common.world.storage.plate.WorldLayer;

public class EntityLivingBase extends Entity
{
	private SharedLivingAttributes attributes;

    public EntityLivingBase(WorldLayer worldLayer) {
        super(worldLayer);

		this.attributes = new SharedLivingAttributes();
	}

	public SharedLivingAttributes getSharedAttributes()
	{
		return attributes;
	}

	public void death()
	{
		EntityDeathEvent deathEvent = EntityLivingEvent.onEntityDeath(this);
		deathEvent.call();
		if (deathEvent.isCancelled())
			return;
	}
}
