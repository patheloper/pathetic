package xyz.ollieee.api.snapshot;

import lombok.NonNull;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;

public interface BlockParser {

    /**
     * Checks the block for being air
     * @param block the {@link Block} to check
     * @return True = Air, False = Not Air
     */
    boolean isAir(@NonNull Block block);

    /**
     * Returns whether a block is liquid
     * @param block The {@link Block} to check
     * @return True = liquid, False = Not liquid
     */
    boolean isLiquid(@NonNull Block block);

    /**
     * Returns whether a block is passable
     * @param block The {@link Block} to check
     * @return True = passable, False = Not passable
     */
    boolean isPassable(@NonNull Block block);

    /**
     * Returns whether a block is solid
     * @param block The {@link Block} to check
     * @return True = solid, False = Not solid
     */
    boolean isSolid(@NonNull Block block);

    /**
     * Gets the material from a block in the given snapshot
     * @param snapshot The {@link ChunkSnapshot} go get the block from
     * @param x The x to check
     * @param y The y to check
     * @param z The z to check
     * @return The blocks {@link Material} at that position
     */
    @NonNull
    Material getBlockMaterialAt(@NonNull ChunkSnapshot snapshot, int x, int y, int z);
}
