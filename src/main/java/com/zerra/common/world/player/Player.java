package com.zerra.common.world.player;

import com.zerra.common.world.World;
import com.zerra.common.world.entity.EntityLivingBase;

public class Player extends EntityLivingBase
{

	public Player(World world)
	{
		super(world);
	}
	
	public String getPlayerName()
	{
		return super.getEntityName();
	}
}
