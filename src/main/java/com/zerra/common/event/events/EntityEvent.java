package com.zerra.common.event.events;

import com.zerra.common.world.entity.Entity;

public class EntityEvent
{

	public static EntityUpdateEvent onEntityUpdate(Entity entity)
	{
		return new EntityUpdateEvent(entity);
	}
}
