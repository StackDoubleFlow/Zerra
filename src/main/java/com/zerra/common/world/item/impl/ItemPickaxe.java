package com.zerra.common.world.item.impl;

import com.zerra.common.world.item.tool.Pickaxe;
import com.zerra.common.world.item.tool.ZerraToolTypes;

public class ItemPickaxe extends BaseTool implements Pickaxe
{

	public ItemPickaxe(String registryName)
	{
		super(registryName);
		this.setToolType(ZerraToolTypes.PICKAXE);
	}

}
