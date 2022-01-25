package xyz.oli.pathing.model.path.finder.strategy.chunks;

import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import xyz.oli.pathing.model.path.finder.strategy.chunks.utils.LegacyVersionHelper;

import java.util.UUID;

public class ChunkUtils {

    private static String version = null;

    public static long getChunkKey(final UUID world, final int x, final int z) {
        return (x & 0xFFFF) | ((z & 0xFF) << 16) | ((world.getMostSignificantBits() & 0xFF) << 24);
    }

    public static Material getMaterial(ChunkSnapshot chunkSnapshot, int x, int y, int z) {

        if (version == null) {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        }

        if (Integer.valueOf(Bukkit.getBukkitVersion().split("\\.")[1]) < 13) {
            return LegacyVersionHelper.getMaterial(chunkSnapshot, x, y, z);
        }
        return chunkSnapshot.getBlockType(x,y,z);
    }

}
