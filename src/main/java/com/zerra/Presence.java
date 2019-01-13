package com.zerra;

import com.zerra.client.Zerra;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

public class Presence
{
	private static DiscordRPC lib;
	private static DiscordRichPresence drp;
	private DiscordEventHandlers handlers;
	
	String appId;
	
	public Presence()
	{
		lib = DiscordRPC.INSTANCE;
		appId = "532051034453966858"; //TODO: We may need to hide this.

		handlers = new DiscordEventHandlers();

		handlers.ready = (user) -> { Zerra.logger().info("Discord Rich Presence is ready!"); };

		lib.Discord_Initialize(appId, handlers, true, null);

		drp = new DiscordRichPresence();

		

		// in a worker thread
		new Thread(() ->
		{
			while (!Thread.currentThread().isInterrupted() && Zerra.getInstance() != null)
			{
				lib.Discord_RunCallbacks();
				try
				{
					Thread.sleep(2000);
				} catch (InterruptedException ignored)
				{
				}
			}
		}, "DRP-Callback-Handler").start();
	}

	public void setPresence(String details, String imageKeyLarge, String imageKeySmall)
	{
		drp.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
		drp.details = details;
		drp.largeImageKey = imageKeyLarge;
		drp.smallImageKey = imageKeySmall;
		
		lib.Discord_UpdatePresence(drp);
	}
}
