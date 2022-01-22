package xyz.oli.pathing.model.path.finder.strategy.chunks;

import org.bukkit.Chunk;

public class ChunkUtils {

    public static long getChunkKey(final Chunk chunk) {
        return (chunk.getX() & 0xFFFF) | ((chunk.getZ() & 0xFF) << 16) | ((chunk.getWorld().getUID().getMostSignificantBits() & 0xFF) << 24);
    }

}
