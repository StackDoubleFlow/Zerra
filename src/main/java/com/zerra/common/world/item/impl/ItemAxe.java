package com.zerra.common.world.item.impl;

import com.zerra.common.world.item.tool.Axe;
import com.zerra.common.world.item.tool.ZerraToolTypes;

public class ItemAxe extends BaseTool implements Axe
{

	public ItemAxe(String registryName)
	{
		super(registryName);
		this.setToolType(ZerraToolTypes.AXE);
	}

}
