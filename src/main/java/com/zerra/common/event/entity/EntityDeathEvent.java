package com.zerra.common.event.entity;

import com.zerra.common.event.Event;
import com.zerra.common.world.entity.Entity;

public class EntityDeathEvent extends Event
{

	private Entity entity;

	public EntityDeathEvent(Entity entity)
	{
		this.entity = entity;
	}

	public Entity getEntity()
	{
		return entity;
	}
}
