package com.zerra.client;

public class WorldLoadState extends State
{

	private Zerra zerra;

	public WorldLoadState()
	{
		super("worldload");
		zerra = Zerra.getInstance();
		zerra.getPresence().setPresence("Loading World", "512x512", "none");
	}

	@Override
	public void update()
	{
		// TODO: Update the loading bar for the world?
	}

	@Override
	public void render()
	{
		// TODO: World loading screen.
	}
}
