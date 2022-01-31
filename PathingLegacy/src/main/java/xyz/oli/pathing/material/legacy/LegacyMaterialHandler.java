package xyz.oli.pathing.material.legacy;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;

import org.jetbrains.annotations.NotNull;
import xyz.oli.pathing.material.MaterialParser;

public class LegacyMaterialHandler extends MaterialParser {

    @Override
    public @NotNull Material getMaterial(@NotNull ChunkSnapshot snapshot, int x, int y, int z) {
        return Material.getMaterial(snapshot.getBlockTypeId(x,y,z));
    }

    @Override
    public boolean isAir(@NotNull Block block) {
        return block.isEmpty();
    }

    @Override
    public boolean isLiquid(@NotNull Block block) {
        return block.isLiquid();
    }

    @Override
    public boolean isPassable(@NotNull Block block) {
        return !block.getType().isBlock();
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
        return material == Material.AIR;
    }

}
