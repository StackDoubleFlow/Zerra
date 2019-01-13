package com.zerra.client;

public class State
{

	private String name;

	public State(String name)
	{
		this.name = name;
		Zerra.logger().info("Loading " + this.getName() + " state.");
	}

	public void update()
	{

	}

	public void render()
	{
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
