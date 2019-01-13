package com.zerra.common.event.entity;

import com.zerra.common.event.Event;
import com.zerra.common.world.entity.Entity;

public class EntityUpdateEvent extends Event
{

	private Entity entity;

	public EntityUpdateEvent(Entity entity)
	{
		this.entity = entity;
	}

	public Entity getEntity()
	{
		return entity;
	}
}
