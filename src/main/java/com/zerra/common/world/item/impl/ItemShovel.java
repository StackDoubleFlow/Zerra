package com.zerra.common.world.item.impl;

import com.zerra.common.world.item.tool.Shovel;
import com.zerra.common.world.item.tool.ZerraToolTypes;

public class ItemShovel extends BaseTool implements Shovel
{

	public ItemShovel(String registryName)
	{
		super(registryName);
		this.setToolType(ZerraToolTypes.SHOVEL);
	}

}
