package org.patheloper.nms;

import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.patheloper.api.snapshot.NMSInterface;

public class NMSInterface_Paper implements NMSInterface {

    @Override
    public ChunkSnapshot getSnapshot(World world, int chunkX, int chunkZ) {
        if (Bukkit.isPrimaryThread()) {
            // If we are currently on the primary thread and call getChunkAtAsync on paper
            // we would enter a deadlock when using .join()
            return world.getChunkAt(chunkX, chunkZ).getChunkSnapshot();
        } else {
            return world.getChunkAtAsync(chunkX, chunkZ).join().getChunkSnapshot();
        }
    }
}
