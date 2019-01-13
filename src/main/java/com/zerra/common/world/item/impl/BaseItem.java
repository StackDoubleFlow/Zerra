package com.zerra.common.world.item.impl;

import com.zerra.common.world.item.Item;

public abstract class BaseItem extends Item
{

	public BaseItem(String registryName)
	{
		super(registryName);
		this.setCanBeGrouped(true);
	}
}
