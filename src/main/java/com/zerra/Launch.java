package com.zerra;

import com.zerra.client.Zerra;
import com.zerra.common.ArgsBuilder;
import com.zerra.server.ZerraServer;

/**
 * <em><b>Copyright (c) 2019 The Zerra Team.</b></em> <br>
 * </br>
 * The main starting point for the game.
 */
public class Launch
{

	public static final String NAME = "Zerra";
	public static final String VERSION = "0.0.4";
	public static final String DOMAIN = "zerra";
	public static final boolean IS_DEVELOPMENT_BUILD = true;

	public static void main(String[] args)
	{

		ArgsBuilder builder = ArgsBuilder.deserialize(args);
		if (builder.isServer())
		{
			new Thread(new ZerraServer(), "server").start();
		} else
		{
			new Thread(new Zerra(), "main").start();
		}
	}
}