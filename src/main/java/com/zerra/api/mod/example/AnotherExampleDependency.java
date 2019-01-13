package com.zerra.api.mod.example;

import com.zerra.api.mod.Mod;
import com.zerra.api.mod.info.ModInfo;
import com.zerra.api.mod.info.ModInfoBuilder;

public class AnotherExampleDependency implements Mod
{

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
		return new ModInfoBuilder("anotherexampledependency", "Another Example Dependency", "1.0", "0.0.4")
				.setAuthors("Arpaesis")
				.setCredits("Credits to the Zerra development team for making the game!")
				.setModDescription("A simple example dependency.")
				.setWebsiteURL("https://www.example.com")
				.build();
	}
}

