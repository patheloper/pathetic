package xyz.ollieee.util;

import lombok.experimental.UtilityClass;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import xyz.ollieee.api.snapshot.BlockParser;
import xyz.ollieee.legacy.snapshot.LegacyBlockParser;
import xyz.ollieee.model.snapshot.world.ModernBlockParser;

@UtilityClass
public class ChunkUtils {

    private final BlockParser BLOCK_PARSER;

    static {
        if (BukkitVersionUtil.isUnder(13))
            BLOCK_PARSER = new LegacyBlockParser();
        else
            BLOCK_PARSER = new ModernBlockParser();
    }

    public long getChunkKey(final int x, final int z) {
        return x & 0xFFFFFFFFL | (z & 0xFFFFFFFFL) << 32;
    }

    /**
     * Get the block type from a chunk snapshot at the given coordinates
     */
    public Material getMaterial(ChunkSnapshot snapshot, int x, int y, int z) {
        return BLOCK_PARSER.getBlockMaterialAt(snapshot, x, y, z);
    }

}
