package com.zerra.api.mod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The class where mods loaded onto the classpath will be handled and managed.
 * 
 * @author Arpaesis
 */
public class ModManager
{

	public Map<String, Mod> loadedMods = new HashMap<>();
	public Map<Integer, Mod> modGroupOrder = new HashMap<>();

	public Set<String> initializedMods = new HashSet<>();

	public ModLoader loader;

	public ModManager()
	{
		loader = new ModLoader(this);
	}

	public void process(String domain)
	{

		String[] dependencies = loadedMods.get(domain).getModInfo().getDependencies();

		if (dependencies != null)
		{
			for (int i = 0; i < dependencies.length; i++)
			{
				this.process(dependencies[i]);
			}
		}

		if (!initializedMods.contains(domain))
		{
			loadedMods.get(domain).init();
			initializedMods.add(domain);
		}
	}

	public void setupMods()
	{
		loader.loadMods("data/mods/");

		for (String modDomain : this.loadedMods.keySet())
		{
			this.process(modDomain);
		}

	}
}
