package xyz.oli.pathing.model.path.finder.strategy.chunks.materialparser;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;

import xyz.oli.pathing.material.MaterialParser;

import org.jetbrains.annotations.NotNull;

public class ModernMaterialParser extends MaterialParser {

    @Override
    public @NotNull Material getMaterial(@NotNull ChunkSnapshot snapshot, int x, int y, int z) {
        return snapshot.getBlockType(x,y,z);
    }

    @Override
    public boolean isAir(@NotNull Block block) {
        return block.getType().isAir();
    }

    @Override
    public boolean isLiquid(@NotNull Block block) {
        return block.isLiquid();
    }

    @Override
    public boolean isPassable(@NotNull Block block) {
        return block.isPassable();
    }

    @Override
    public boolean isSolid(@NotNull Block block) {
        return block.getType().isSolid();
    }

    @Override
    public boolean isSolid(@NotNull Material material) {
        return material.isSolid();
    }

    @Override
    public boolean isAir(@NotNull Material material) {
        return material.isAir();
    }
}
