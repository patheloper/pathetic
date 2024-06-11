package org.patheloper.paper;

import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.patheloper.api.snapshot.NMSInterface;

public class PaperNMSInterface implements NMSInterface {
  
  @Override
  public ChunkSnapshot getSnapshot(World world, int chunkX, int chunkZ) {
    if (Bukkit.isPrimaryThread()) {
      return world.getChunkAt(chunkX, chunkZ).getChunkSnapshot();
    } else {
      return world.getChunkAtAsyncUrgently(chunkX, chunkZ).join().getChunkSnapshot();
    }
  }
  
  @Override
  public BlockState getBlockState(ChunkSnapshot snapshot, int x, int y, int z) {
    return snapshot.getBlockData(x, y, z).createBlockState();
  }
}
