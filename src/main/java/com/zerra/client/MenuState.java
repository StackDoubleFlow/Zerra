package com.zerra.client;

public class MenuState extends State
{

	private Zerra zerra;

	public MenuState()
	{
		super("menu");
		zerra = Zerra.getInstance();
		zerra.getPresence().setPresence("In Menu", "512x512", "none");
	}
	
	@Override
	public void update()
	{
		Zerra.getInstance().camera.update();
		Zerra.getInstance().inputHandler.updateGamepad();
	}

	@Override
	public void render()
	{
		// TODO: Render a background and buttons here!
	}
}
