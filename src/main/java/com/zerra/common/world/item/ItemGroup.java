package com.zerra.common.world.item;

public class ItemGroup
{
	private Item item;

	private int size;

	private int meta;

	public static final int MAX_SIZE = 99;

	public ItemGroup(Item item)
	{
		this(item, 0, 0);
	}

	public ItemGroup(Item item, int size, int meta)
	{
		this.item = item;

		if (size > ItemGroup.MAX_SIZE)
		{
			throw new RuntimeException("Given ItemGroup size (" + size + ") exceeds the maximum size (" + ItemGroup.MAX_SIZE + ") placed on ItemGroups.");
		} else
		{
			this.setSize(size);
		}

		this.size = size;
		this.meta = meta;
	}

	public int getSize()
	{
		return size;
	}

	public void setSize(int size)
	{
		this.size = size;
	}

	public Item getItem()
	{
		return item;
	}

	public int getMeta()
	{
		return meta;
	}

	public void addToSize(int amount)
	{
		this.size += amount;
	}
}
