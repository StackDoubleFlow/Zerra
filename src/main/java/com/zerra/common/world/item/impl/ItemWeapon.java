package com.zerra.common.world.item.impl;

import com.zerra.common.world.item.tool.Weapon;
import com.zerra.common.world.item.tool.ZerraToolTypes;

public class ItemWeapon extends BaseTool implements Weapon
{

	private float damage = 1.0f;

	public ItemWeapon(String registryName)
	{
		super(registryName);
		this.setToolType(ZerraToolTypes.WEAPON);
	}

	@Override
	public float getDamage()
	{
		return this.damage;
	}

	@Override
	public Weapon setDamage(float damage)
	{
		this.damage = damage;
		return this;
	}

}
