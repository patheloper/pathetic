package xyz.oli.pathing.model.path.finder.strategy.chunks;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;

import xyz.oli.pathing.PathfindingPlugin;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ChunkUtils {

    public static long getChunkKey(final @NotNull UUID world, final int x, final int z) {
        return (x & 0xFFFF) | ((z & 0xFF) << 16) | ((world.getMostSignificantBits() & 0xFF) << 24);
    }

    public static Material getMaterial(ChunkSnapshot chunkSnapshot, int x, int y, int z) {
        return PathfindingPlugin.getInstance().getParser().getMaterial(chunkSnapshot, x, y, z);
    }

}
