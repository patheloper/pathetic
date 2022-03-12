package xyz.ollieee.api.material;

import lombok.NonNull;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;

public interface MaterialParser {

    /**
     * @param snapshot The snapshot go get a value from
     * @param x The x to check
     * @param y The y to check
     * @param z The z to check
     * @return Material the material at that position
     */
    @NonNull
    Material getMaterial(@NonNull ChunkSnapshot snapshot, int x, int y, int z);

    /**
     * Checks the block for being air
     * @param block the block to check
     * @return Boolean True = Air, False = Not Air
     */
    boolean isAir(@NonNull Block block);

    /**
     * Returns whether a block is liquid
     * @param block The block to check
     * @return Boolean True = liquid, False = Not liquid
     */
    boolean isLiquid(@NonNull Block block);

    /**
     * Returns whether a block is passable
     * @param block The block to check
     * @return Boolean True = passable, False = Not passable
     */
    boolean isPassable(@NonNull Block block);

    /**
     * Returns whether a block is solid
     * @param block The block to check
     * @return Boolean True = solid, False = Not solid
     */
    boolean isSolid(@NonNull Block block);

    /**
     * Returns whether a material is solid
     * @param material The material to check
     * @return Boolean True = solid, False = Not solid
     */
    boolean isSolid(@NonNull Material material);

    /**
     * Returns whether a material is air
     * @param material The material to check
     * @return Boolean True = Air, False = Not Air
     */
    boolean isAir(@NonNull Material material);
}
