package xyz.ollieee.util;

import lombok.experimental.UtilityClass;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import xyz.ollieee.Pathetic;

@UtilityClass
public class ChunkUtils {

    public long getChunkKey(final int x, final int z) {
        return getaLong(x, z);
    }

    public Material getMaterial(ChunkSnapshot chunkSnapshot, int x, int y, int z) {
        return Pathetic.getMaterialParser().getMaterial(chunkSnapshot, x, y, z);
    }

    private long getaLong(final int x, final int z) {
        return (long) x & 0xFFFFFFFFL | ((long) z & 0xFFFFFFFFL) << 32;
    }

}
