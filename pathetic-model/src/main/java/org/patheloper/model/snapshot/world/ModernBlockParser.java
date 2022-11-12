package org.patheloper.model.snapshot.world;

import lombok.NonNull;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.patheloper.Pathetic;
import org.patheloper.api.snapshot.BlockParser;

public class ModernBlockParser implements BlockParser {

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
    public @NonNull Material getBlockMaterialAt(@NonNull ChunkSnapshot snapshot, int x, int y, int z) {
        try {
            return snapshot.getBlockType(x, y, z);
        }catch (Exception exception) {
            Pathetic.getPluginLogger().warning("Failed to get block type from chunk snapshot, falling back to block state");
            return snapshot.getBlockData(x, y, z).getMaterial();
        }
    }

}
