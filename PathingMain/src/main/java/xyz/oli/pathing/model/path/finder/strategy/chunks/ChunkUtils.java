package xyz.oli.pathing.model.path.finder.strategy.chunks;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;

import xyz.oli.PathingAPI;

import java.util.UUID;

@UtilityClass
public class ChunkUtils {

    public long getChunkKey(final @NonNull UUID world, final int x, final int z) {
        return getaLong(world, x, z);
    }

    public Material getMaterial(ChunkSnapshot chunkSnapshot, int x, int y, int z) {
        return PathingAPI.getParser().getMaterial(chunkSnapshot, x, y, z);
    }

    private long getaLong(UUID world, int x, int z) {
        return x & 0xFFFF | (z & 0xFF) << 16 | (world.getMostSignificantBits() & 0xFF) << 24;
    }

}
