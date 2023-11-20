package org.patheloper.api.wrapper;

import lombok.Getter;
import lombok.Value;
import org.bukkit.Material;

/**
 * Enum to represent the block type of wrapped blocks
 */
@Getter
@Value
public class PathBlockType {

    Material material;
    PathBlockCondition condition;

    public PathBlockType(Material material) {
        this.material = material;
        this.condition = PathBlockCondition.fromMaterial(material);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof PathBlockType)) return false;

        return this.material == ((PathBlockType) obj).material;
    }

    public enum PathBlockCondition {

        /**
         * Represents an air block
         */
        AIR,
        /**
         * A block that is either Lava or Water
         */
        LIQUID,
        /**
         * Anything that is deemed not passable
         */
        SOLID,
        /**
         * Represents all blocks that could be walked through, but are not explicitly air
         */
        OTHER;

        /**
         * @return {@link PathBlockType} the block type of the given block
         */
        public static PathBlockCondition fromMaterial(Material material) {

            if (material == Material.AIR) // can't check for cave or void air due to backwards compatibility
                return AIR;

            if(material.isSolid()) {
                return SOLID;
            }

            if(material == Material.LAVA || material == Material.WATER) {
                return LIQUID;
            }

            return OTHER;
        }
    }
}
