package org.patheloper.provider;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.DataPaletteBlock;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.patheloper.api.snapshot.ChunkDataProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class v1_18UpChunkDataProviderImpl implements ChunkDataProvider {

  private static final Field blockIDField;
  private static final Class<?> craftChunkClass;

  static {
    try {
      craftChunkClass = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".CraftChunk");
      blockIDField = craftChunkClass.getDeclaredField("emptyBlockIDs");
      blockIDField.setAccessible(true);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public ChunkSnapshot getSnapshot(World world, int chunkX, int chunkZ) {
    try {
      WorldServer server = ((CraftWorld) world).getHandle();
      Constructor<?> constructor = craftChunkClass.getConstructor(WorldServer.class, int.class, int.class);
      Object newCraftChunk = constructor.newInstance(server, chunkX, chunkZ);

      server.l().a(chunkX, chunkZ, ChunkStatus.n, true);
      DataPaletteBlock<IBlockData> dataDataPaletteBlock =
        (DataPaletteBlock<IBlockData>) blockIDField.get(newCraftChunk);

      dataDataPaletteBlock.b();
      dataDataPaletteBlock.a();
      Method getChunkSnapshotMethod = craftChunkClass.getMethod("getChunkSnapshot");
      ChunkSnapshot chunkSnapshot = (ChunkSnapshot) getChunkSnapshotMethod.invoke(newCraftChunk);
      dataDataPaletteBlock.b();

      return chunkSnapshot;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public BlockState getBlockState(ChunkSnapshot snapshot, int x, int y, int z) {
    return snapshot.getBlockData(x, y, z).createBlockState();
  }
}
