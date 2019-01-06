package com.zerra.common.world.entity;

import com.zerra.common.world.World;
import com.zerra.common.world.entity.attrib.LivingAttributes;

public class EntityLivingBase extends EntityBase
{
	private World world;

	private LivingAttributes attributes;

	public EntityLivingBase(World world)
	{
		super(world);
		
		this.attributes = new LivingAttributes();
	}

	public World getWorld()
	{
		return world;
	}

	public LivingAttributes getSharedAttributes()
	{
		return attributes;
	}
}
