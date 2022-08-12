package xyz.ollieee.api.snapshot;

import org.bukkit.ChunkSnapshot;
import org.bukkit.World;

public interface NMSInterface {

    /**
     * Returns a {@link ChunkSnapshot} of the chunk at the given coordinates.
     *
     * @param world  The {@link World} to get the {@link ChunkSnapshot} from
     * @param chunkX The x-coordinate of the chunk
     * @param chunkZ The z-coordinate of the chunk
     * @return The {@link ChunkSnapshot} of the chunk at the given coordinates
     */
    ChunkSnapshot getSnapshot(World world, int chunkX, int chunkZ);
}
