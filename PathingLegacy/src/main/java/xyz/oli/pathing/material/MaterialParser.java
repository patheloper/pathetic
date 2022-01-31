package xyz.oli.pathing.material;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;

import org.jetbrains.annotations.NotNull;

public abstract class MaterialParser {

    @NotNull
    public abstract Material getMaterial(@NotNull ChunkSnapshot snapshot, int x, int y, int z);

    public abstract boolean isAir(@NotNull Block block);

    public abstract boolean isLiquid(@NotNull Block block);

    public abstract boolean isPassable(@NotNull Block block);

    public abstract boolean isSolid(@NotNull Block block);

    public abstract boolean isSolid(@NotNull Material material);

    public abstract boolean isAir(@NotNull Material material);
}
