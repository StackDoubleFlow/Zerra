package com.zerra.common.world.item.impl;

import com.zerra.common.world.item.Item;

public abstract class BaseConsumable extends Item
{

	private float healingAmount;

	private float consumeSpeed;

	public BaseConsumable(String registryName)
	{
		super(registryName);
	}

	public float getHealingAmount()
	{
		return healingAmount;
	}

	public void setHealingAmount(float healingAmount)
	{
		this.healingAmount = healingAmount;
	}

	public float getConsumeSpeed()
	{
		return consumeSpeed;
	}

	public void setConsumeSpeed(float consumeSpeed)
	{
		this.consumeSpeed = consumeSpeed;
	}

	public void onConsuming()
	{

	}

	public void onFinishedConsuming()
	{

	}
}
