package com.zerra.common.world.tile;

import com.zerra.common.world.item.tool.ZerraToolTypes;

public class ZerraTileTypes {

	public static final TileType AIR = new TileType("type_air", 0f, 0f, ZerraToolTypes.NONE);
	public static final TileType STONE = new TileType("type_stone", 2f, 2f, ZerraToolTypes.SHOVEL);
    public static final TileType GRASS = new TileType("type_grass", 1f, 1f, ZerraToolTypes.SHOVEL);
    public static final TileType SAND = new TileType("type_sand", 1f, 1f, ZerraToolTypes.SHOVEL);
}
