package xyz.ollieee.util;

import lombok.experimental.UtilityClass;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import xyz.ollieee.Pathetic;

@UtilityClass
public class ChunkUtils {

    public long getChunkKey(final int x, final int z) {
        return x & 0xFFFFFFFFL | (z & 0xFFFFFFFFL) << 32;
    }

    public Material getMaterial(ChunkSnapshot chunkSnapshot, int x, int y, int z) {
        return Pathetic.getMaterialParser().getMaterial(chunkSnapshot, x, y, z);
    }

}
