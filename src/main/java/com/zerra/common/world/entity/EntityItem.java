package com.zerra.common.world.entity;

import com.zerra.common.world.item.Item;
import com.zerra.common.world.item.ItemGroup;
import com.zerra.common.world.storage.plate.WorldLayer;

public class EntityItem extends Entity
{
	private ItemGroup group;

    public EntityItem(WorldLayer worldLayer, Item item) {
        this(worldLayer, new ItemGroup(item, 1, 0));
    }

    public EntityItem(WorldLayer worldLayer, ItemGroup group) {
        super(worldLayer);
	}

	public ItemGroup getGroup()
	{
		return group;
	}
}
