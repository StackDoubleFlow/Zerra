package com.zerra.api.mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zerra.api.mod.info.ModInfo;

public interface Mod
{
	public default Logger getLogger()
	{
		return LogManager.getLogger(getModInfo().getModName());
	}

	public void init();

	public void postInit();

	public ModInfo getModInfo();
}
