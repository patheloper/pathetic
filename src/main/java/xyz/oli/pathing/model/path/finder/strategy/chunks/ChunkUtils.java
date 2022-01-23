package xyz.oli.pathing.model.path.finder.strategy.chunks;

import java.util.UUID;

public class ChunkUtils {

    public static long getChunkKey(final UUID world, final int x, final int z) {
        return (x & 0xFFFF) | ((z & 0xFF) << 16) | ((world.getMostSignificantBits() & 0xFF) << 24);
    }

}
