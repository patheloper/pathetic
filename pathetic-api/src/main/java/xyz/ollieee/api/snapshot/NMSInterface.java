package xyz.ollieee.api.snapshot;

import org.bukkit.ChunkSnapshot;
import org.bukkit.World;

public interface NMSInterface {

    ChunkSnapshot getSnapshot(World world, int chunkX, int chunkZ);
}
