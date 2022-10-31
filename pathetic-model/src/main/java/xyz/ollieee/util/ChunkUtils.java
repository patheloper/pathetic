package xyz.ollieee.util;

import lombok.experimental.UtilityClass;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import xyz.ollieee.api.snapshot.MaterialParser;
import xyz.ollieee.legacy.snapshot.LegacyMaterialParser;
import xyz.ollieee.model.snapshot.world.ModernMaterialParser;

@UtilityClass
public class ChunkUtils {

    private final MaterialParser materialParser;

    static {
        if (BukkitVersionUtil.isUnder(13))
            materialParser = new LegacyMaterialParser();
        else
            materialParser = new ModernMaterialParser();
    }

    public long getChunkKey(final int x, final int z) {
        return x & 0xFFFFFFFFL | (z & 0xFFFFFFFFL) << 32;
    }

    public Material getMaterial(ChunkSnapshot chunkSnapshot, int x, int y, int z) {
        return materialParser.getMaterial(chunkSnapshot, x, y, z);
    }

}
