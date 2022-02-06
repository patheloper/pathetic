package xyz.oli.pathing.model.path.finder.strategy.chunks;

import lombok.experimental.UtilityClass;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;

import xyz.oli.PathingAPI;

@UtilityClass
public class ChunkUtils {

    public long getChunkKey(final int x, final int z) {
        return getaLong(x, z);
    }

    public Material getMaterial(ChunkSnapshot chunkSnapshot, int x, int y, int z) {
        return PathingAPI.getParser().getMaterial(chunkSnapshot, x, y, z);
    }

    private long getaLong(final int x, final int z) {
        return (long) x & 0xFFFFFFFFL | ((long) z & 0xFFFFFFFFL) << 32;
    }

}
