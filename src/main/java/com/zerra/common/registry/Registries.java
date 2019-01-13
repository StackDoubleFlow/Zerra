package com.zerra.common.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zerra.common.world.item.Item;
import com.zerra.common.world.tile.Tile;

public class Registries {

	public static final IRegistry<Tile> TILES = new Tiles();

	public static final IRegistry<Item> ITEMS = new Items();

	public static void addEntryHolder(IEntryHolder<?> holder) {
		if (holder.getType() == Tile.class) {
			TILES.getEntries().add((Tile) holder);
		} else if (holder.getType() == Item.class) {
			ITEMS.getEntries().add((Item) holder);
		}
	}

	private static class Tiles implements IRegistry<Tile> {

		protected final List<IEntryHolder<Tile>> entryHolders = new ArrayList<>();

		@Override
		public List<IEntryHolder<Tile>> getEntryHolders() {
			return entryHolders;
		}

		@Override
		public List<Tile> getEntries() {
			List<Tile> entries = new ArrayList<>();
			for (IEntryHolder<Tile> entryHolder : entryHolders) {
				entries.addAll(Arrays.asList(entryHolder.getEntries()));
			}
			return entries;
		}
	}

	private static class Items implements IRegistry<Item> {

		List<IEntryHolder<Item>> entryHolders = new ArrayList<>();

		@Override
		public List<IEntryHolder<Item>> getEntryHolders() {
			return entryHolders;
		}

		@Override
		public List<Item> getEntries() {
			List<Item> entries = new ArrayList<>();
			for (IEntryHolder<Item> entryHolder : entryHolders) {
				entries.addAll(Arrays.asList(entryHolder.getEntries()));
			}
			return entries;
		}
	}
}
