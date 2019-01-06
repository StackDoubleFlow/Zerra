package com.zerra.common.world.tile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TileType {

    private final float hardness;

    private final String typeName;

    private final String toolType;

    private final float harvestLevel;

    /**
     * Constructs an tileType
     * @see ZerraTileTypes
     *
     * @param typeName unique identifier in text for this type
     * @param hardness the hardness of the tile (how long it takes to be broken) unless specified by
     *                 the tool/item used the breaktime will be ((hardness * harvestLevel) / toolLevel) * 0.5 seconds
     * @param harvestLevel the minimum level of tool you need to be able to 'harvest' (collect drops) by breaking this tile
     * @param toolType a String specifying the type of tool that can break this Tile. See DefaultToolTypes.class
     */
    public TileType(String typeName, float hardness, float harvestLevel, String toolType){
      this.typeName = typeName;
      this.hardness = hardness;
      this.harvestLevel = harvestLevel;
      this.toolType = toolType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TileType tileType = (TileType) o;

        return new EqualsBuilder()
                .append(getHardness(), tileType.getHardness())
                .append(getHarvestLevel(), tileType.getHarvestLevel())
                .append(getTypeName(), tileType.getTypeName())
                .append(getToolType(), tileType.getToolType())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getHardness())
                .append(getTypeName())
                .append(getToolType())
                .append(getHarvestLevel())
                .toHashCode();
    }

    public float getHardness() {
        return hardness;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getToolType() {
        return toolType;
    }

    public float getHarvestLevel() {
        return harvestLevel;
    }
}
