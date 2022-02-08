package xyz.oli.pathing.model.world.material;

import lombok.NonNull;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;

import xyz.oli.api.material.MaterialParser;

public class ModernMaterialParser implements MaterialParser {

    @Override
    public @NonNull Material getMaterial(@NonNull ChunkSnapshot snapshot, int x, int y, int z) {
        return snapshot.getBlockType(x,y,z);
    }

    @Override
    public boolean isAir(@NonNull Block block) {
        return block.getType().isAir();
    }

    @Override
    public boolean isLiquid(@NonNull Block block) {
        return block.isLiquid();
    }

    @Override
    public boolean isPassable(@NonNull Block block) {
        return block.isPassable();
    }

    @Override
    public boolean isSolid(@NonNull Block block) {
        return block.getType().isSolid();
    }

    @Override
    public boolean isSolid(@NonNull Material material) {
        return material.isSolid();
    }

    @Override
    public boolean isAir(@NonNull Material material) {
        return material.isAir();
    }
}
