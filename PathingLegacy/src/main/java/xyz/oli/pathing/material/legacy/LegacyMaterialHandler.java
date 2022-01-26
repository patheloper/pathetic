package xyz.oli.pathing.material.legacy;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;

import xyz.oli.pathing.material.MaterialParser;

public class LegacyMaterialHandler extends MaterialParser {

    @Override
    public Material getMaterial(ChunkSnapshot snapshot, int x, int y, int z) {
        return Material.getMaterial(snapshot.getBlockTypeId(x,y,z));
    }

    @Override
    public boolean isAir(Block block) {
        return block.isEmpty();
    }

    @Override
    public boolean isLiquid(Block block) {
        return block.isLiquid();
    }

    @Override
    public boolean isPassable(Block block) {
        return !block.getType().isBlock();
    }

    @Override
    public boolean isSolid(Block block) {
        return block.getType().isSolid();
    }

    @Override
    public boolean isSolid(Material material) {
        return material.isSolid();
    }

    @Override
    public boolean isAir(Material material) {
        return material == Material.AIR;
    }

}
