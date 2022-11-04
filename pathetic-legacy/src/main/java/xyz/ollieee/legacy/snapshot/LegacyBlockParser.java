package xyz.ollieee.legacy.snapshot;

import lombok.NonNull;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;
import xyz.ollieee.api.snapshot.BlockParser;

public class LegacyBlockParser implements BlockParser {

    @Override
    public boolean isAir(@NonNull Block block) {
        return block.isEmpty();
    }

    @Override
    public boolean isLiquid(@NonNull Block block) {
        return block.isLiquid();
    }

    @Override
    public boolean isPassable(@NonNull Block block) {
        return !block.getType().isSolid();
    }

    @Override
    public boolean isSolid(@NonNull Block block) {
        return block.getType().isSolid();
    }

    @Override
    public @NonNull Material getBlockMaterialAt(@NonNull ChunkSnapshot snapshot, int x, int y, int z) {
        return Material.getMaterial(snapshot.getBlockTypeId(x,y,z));
    }

}
