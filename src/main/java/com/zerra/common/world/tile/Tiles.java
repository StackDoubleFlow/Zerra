package com.zerra.common.world.tile;

import java.util.Map;

import com.google.common.collect.Maps;
import com.zerra.client.util.ResourceLocation;
import com.zerra.common.world.tile.impl.TileGround;

public class Tiles {

	private static final Map<ResourceLocation, Tile> TILES = Maps.<ResourceLocation, Tile>newHashMap();

	public static final Tile STONE = new TileGround(ZerraTileTypes.STONE, 0x9a8a71, new ResourceLocation("textures/stone.png"), "stone");
	public static final Tile GRASS = new TileGround(ZerraTileTypes.GRASS, 0x577240, new ResourceLocation("textures/grass.png"), "grass");
	public static final Tile SAND = new TileGround(ZerraTileTypes.SAND, 0xf9d699, new ResourceLocation("textures/sand.png"), "sand");
	public static final Tile COBBLESTONE = new TileGround(ZerraTileTypes.STONE, 0x9a8a71, new ResourceLocation("textures/cobblestone.png"), "cobblestone");

	public static void registerTiles() {
		register(STONE);
		register(GRASS);
		register(SAND);
		register(COBBLESTONE);
	}

	private static void register(Tile tile) {
		if (tile == null || tile.getRegistryID() == null)
			throw new RuntimeException("Tile \'" + (tile == null ? tile : tile.getClass().getName()) + "\' was either null or had no registry name!");
		if (TILES.containsKey(tile.getRegistryID()))
			throw new RuntimeException("Tile \'" + tile.getRegistryID() + "\' is already registered!");
		TILES.put(tile.getRegistryID(), tile);
	}

	public static Tile[] getTiles() {
		return TILES.values().toArray(new Tile[0]);
	}

	public static Tile byId(ResourceLocation id) {
		return TILES.getOrDefault(id, Tile.NONE);
	}
}