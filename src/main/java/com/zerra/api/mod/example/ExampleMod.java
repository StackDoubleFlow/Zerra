package com.zerra.api.mod.example;

import com.zerra.api.mod.Mod;
import com.zerra.api.mod.info.ModInfo;
import com.zerra.api.mod.info.ModInfoBuilder;

public class ExampleMod implements Mod
{

	ModInfoBuilder builder = new ModInfoBuilder("examplemod", "Example Mod", "1.0", "0.0.4");
	
	@Override
	public void init()
	{
		this.getLogger().info(this.getModInfo().getModName() + " has finished initialization.");
	}

	@Override
	public void postInit()
	{
	}

	@Override
	public ModInfo getModInfo()
	{
		return builder
				.setAuthors("Arpaesis")
				.setCredits("Credits to the Zerra development team for making the game!")
				.setDependencies("exampledependency")
				.setModDescription("A simple example mod.")
				.setWebsiteURL("https://www.example.com")
				.build();
	}
}

