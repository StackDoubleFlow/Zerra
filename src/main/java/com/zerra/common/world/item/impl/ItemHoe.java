package com.zerra.common.world.item.impl;

import com.zerra.common.world.item.tool.Hoe;
import com.zerra.common.world.item.tool.ZerraToolTypes;

public class ItemHoe extends BaseTool implements Hoe
{

	public ItemHoe(String registryName)
	{
		super(registryName);
		this.setToolType(ZerraToolTypes.HOE);
	}

}
