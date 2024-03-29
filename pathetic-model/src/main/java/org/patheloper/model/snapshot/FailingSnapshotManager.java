package org.patheloper.model.snapshot;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.BlockInformation;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathEnvironment;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.model.snapshot.world.WorldDomain;
import org.patheloper.nms.NMSUtils;
import org.patheloper.util.BukkitVersionUtil;
import org.patheloper.util.ChunkUtils;
import org.patheloper.util.ErrorLogger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/**
 * The FailingSnapshotManager class implements the SnapshotManager interface and provides a default
 * implementation for retrieving block data snapshots from a Minecraft world. It utilizes chunk
 * snapshots to efficiently access block information, even in asynchronous contexts.
 *
 * <p>FailingSnapshotManager also uses NMS (net.minecraft.server) utilities to bypass the Spigot
 * AsyncCatcher and fetch snapshots natively from an asynchronous context. This allows for more
 * flexible and efficient access to world data.
 *
 * <p>Note: While this manager is designed to efficiently retrieve block data snapshots, it may
 * encounter failures or null results if the pathfinder is not permitted to load chunks or if chunks
 * are not loaded in the world. Developers using this manager should handle potential failures
 * gracefully.
 */
public class FailingSnapshotManager implements SnapshotManager {

  private static final Map<UUID, WorldDomain> SNAPSHOTS_MAP = new ConcurrentHashMap<>();

  private static final NMSUtils NMS_UTILS;

  static {
    BukkitVersionUtil.Version version = BukkitVersionUtil.getVersion();
    NMS_UTILS = new NMSUtils((int) version.major(), (int) version.minor());
  }

  public static void invalidateChunk(UUID worldUUID, int chunkX, int chunkZ) {
    if (SNAPSHOTS_MAP.containsKey(worldUUID)) {
      WorldDomain worldDomain = SNAPSHOTS_MAP.get(worldUUID);
      long chunkKey = ChunkUtils.getChunkKey(chunkX, chunkZ);

      worldDomain.removeSnapshot(chunkKey);
    }
  }

  private static Optional<PathBlock> fetchBlock(PathPosition position) {
    Optional<ChunkSnapshot> chunkSnapshotOptional = getChunkSnapshot(position);

    int chunkX = position.getBlockX() >> 4;
    int chunkZ = position.getBlockZ() >> 4;

    if (chunkSnapshotOptional.isPresent()) {
      int x = position.getBlockX() - chunkX * 16;
      int z = position.getBlockZ() - chunkZ * 16;

      Material material =
          ChunkUtils.getMaterial(chunkSnapshotOptional.get(), x, position.getBlockY(), z);
      BlockState blockState =
        NMS_UTILS.getNmsInterface().getBlockState(chunkSnapshotOptional.get(), x, position.getBlockY(), z);
      return Optional.of(new PathBlock(position, new BlockInformation(material, blockState)));
    }

    return Optional.empty();
  }

  private static Optional<ChunkSnapshot> getChunkSnapshot(PathPosition position) {
    int chunkX = position.getBlockX() >> 4;
    int chunkZ = position.getBlockZ() >> 4;

    if (SNAPSHOTS_MAP.containsKey(position.getPathEnvironment().getUuid())) {

      WorldDomain worldDomain = SNAPSHOTS_MAP.get(position.getPathEnvironment().getUuid());
      long chunkKey = ChunkUtils.getChunkKey(chunkX, chunkZ);

      Optional<ChunkSnapshot> snapshot = worldDomain.getSnapshot(chunkKey);
      if (snapshot.isPresent()) return snapshot;
    }

    World world = Bukkit.getWorld(position.getPathEnvironment().getUuid());
    if (world == null) return Optional.empty();

    if (world.isChunkLoaded(chunkX, chunkZ))
      return Optional.ofNullable(
          processChunkSnapshot(
              position,
              chunkX,
              chunkZ,
              NMS_UTILS.getNmsInterface().getSnapshot(world, chunkX, chunkZ)));

    return Optional.empty();
  }

  private static ChunkSnapshot processChunkSnapshot(
      PathPosition position, int chunkX, int chunkZ, ChunkSnapshot chunkSnapshot) {
    WorldDomain worldDomain =
        SNAPSHOTS_MAP.computeIfAbsent(
            position.getPathEnvironment().getUuid(), uuid -> new WorldDomain());
    worldDomain.addSnapshot(ChunkUtils.getChunkKey(chunkX, chunkZ), chunkSnapshot);
    return chunkSnapshot;
  }

  @Override
  public PathBlock getBlock(@NonNull PathPosition position) {
    Optional<PathBlock> block = fetchBlock(position);
    return block.orElse(null);
  }

  /**
   * The RequestingSnapshotManager is an inner class of FailingSnapshotManager, extending it. This
   * class provides additional functionality for ensuring that block data snapshots are available,
   * even if not initially loaded.
   */
  public static class RequestingSnapshotManager extends FailingSnapshotManager {

    private static ChunkSnapshot retrieveChunkSnapshot(
        PathEnvironment world, int chunkX, int chunkZ) {
      World bukkitWorld = Bukkit.getWorld(world.getUuid());
      return NMS_UTILS.getNmsInterface().getSnapshot(bukkitWorld, chunkX, chunkZ);
    }

    private static ChunkSnapshot retrieveSnapshot(PathPosition position) {
      int chunkX = position.getBlockX() >> 4;
      int chunkZ = position.getBlockZ() >> 4;

      Optional<ChunkSnapshot> chunkSnapshotOptional = getChunkSnapshot(position);

      return chunkSnapshotOptional.orElseGet(
          () -> {
            ChunkSnapshot chunkSnapshot =
                retrieveChunkSnapshot(position.getPathEnvironment(), chunkX, chunkZ);

            if (chunkSnapshot == null)
              throw ErrorLogger.logFatalError("Could not retrieve chunk snapshot --> BOOM!");

            processChunkSnapshot(position, chunkX, chunkZ, chunkSnapshot);
            return chunkSnapshot;
          });
    }

    private static PathBlock ensureBlock(PathPosition pathPosition) {
      int chunkX = pathPosition.getBlockX() >> 4;
      int chunkZ = pathPosition.getBlockZ() >> 4;

      ChunkSnapshot chunkSnapshot = retrieveSnapshot(pathPosition);
      int x = pathPosition.getBlockX() - chunkX * 16;
      int z = pathPosition.getBlockZ() - chunkZ * 16;

      Material material = ChunkUtils.getMaterial(chunkSnapshot, x, pathPosition.getBlockY(), z);
      BlockState blockState =
        NMS_UTILS.getNmsInterface().getBlockState(chunkSnapshot, x, pathPosition.getBlockY(), z);
      return new PathBlock(pathPosition, new BlockInformation(material, blockState));
    }

    @Override
    public PathBlock getBlock(@NonNull PathPosition position) {
      PathBlock block = super.getBlock(position);
      return block == null ? ensureBlock(position) : block;
    }
  }
}
