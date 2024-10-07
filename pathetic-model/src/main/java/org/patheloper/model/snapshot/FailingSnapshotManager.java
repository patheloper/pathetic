package org.patheloper.model.snapshot;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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
import org.patheloper.provider.ChunkDataProviderResolver;
import org.patheloper.util.BukkitVersionUtil;
import org.patheloper.util.ChunkUtils;
import org.patheloper.util.ErrorLogger;

public class FailingSnapshotManager implements SnapshotManager {

  private static final Map<UUID, WorldDomain> SNAPSHOTS_MAP = new ConcurrentHashMap<>();

  private static final ChunkDataProviderResolver CHUNK_DATA_PROVIDER_RESOLVER;

  static {
    BukkitVersionUtil.Version version = BukkitVersionUtil.getVersion();
    CHUNK_DATA_PROVIDER_RESOLVER =
        new ChunkDataProviderResolver((int) version.getMajor(), (int) version.getMinor());
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
          CHUNK_DATA_PROVIDER_RESOLVER
              .getChunkDataProvider()
              .getBlockState(chunkSnapshotOptional.get(), x, position.getBlockY(), z);
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
              CHUNK_DATA_PROVIDER_RESOLVER
                  .getChunkDataProvider()
                  .getSnapshot(world, chunkX, chunkZ)));

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

  @Override
  public PathBlock getHighestBlockAt(PathPosition position) {
    Optional<ChunkSnapshot> chunkSnapshotOptional = getChunkSnapshot(position);

    if (chunkSnapshotOptional.isPresent()) {
      ChunkSnapshot chunkSnapshot = chunkSnapshotOptional.get();

      int chunkX = position.getBlockX() >> 4;
      int chunkZ = position.getBlockZ() >> 4;

      int highestY = chunkSnapshot.getHighestBlockYAt(chunkX, chunkZ);

      PathPosition highestBlockPosition =
          new PathPosition(
              position.getPathEnvironment(), position.getBlockX(), highestY, position.getBlockZ());
      BlockState blockState =
          CHUNK_DATA_PROVIDER_RESOLVER
              .getChunkDataProvider()
              .getBlockState(chunkSnapshot, chunkX, highestY, chunkZ);

      Material material = chunkSnapshot.getBlockType(chunkX, highestY, chunkZ);
      return new PathBlock(highestBlockPosition, new BlockInformation(material, blockState));
    }

    // if no valid chunk snapshot was found
    return null;
  }

  public static class RequestingSnapshotManager extends FailingSnapshotManager {

    private static ChunkSnapshot retrieveChunkSnapshot(
        PathEnvironment world, int chunkX, int chunkZ) {
      World bukkitWorld = Bukkit.getWorld(world.getUuid());
      return CHUNK_DATA_PROVIDER_RESOLVER
          .getChunkDataProvider()
          .getSnapshot(bukkitWorld, chunkX, chunkZ);
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
          CHUNK_DATA_PROVIDER_RESOLVER
              .getChunkDataProvider()
              .getBlockState(chunkSnapshot, x, pathPosition.getBlockY(), z);
      return new PathBlock(pathPosition, new BlockInformation(material, blockState));
    }

    @Override
    public PathBlock getBlock(@NonNull PathPosition position) {
      PathBlock block = super.getBlock(position);
      return block == null ? ensureBlock(position) : block;
    }

    @Override
    public PathBlock getHighestBlockAt(@NonNull PathPosition position) {
      PathBlock block = super.getHighestBlockAt(position);
      return block == null ? ensureHighestBlock(position) : block;
    }

    private PathBlock ensureHighestBlock(PathPosition pathPosition) {
      ChunkSnapshot chunkSnapshot = retrieveSnapshot(pathPosition);

      int chunkX = pathPosition.getBlockX() >> 4;
      int chunkZ = pathPosition.getBlockZ() >> 4;

      int highestY = chunkSnapshot.getHighestBlockYAt(chunkX, chunkZ);

      PathPosition highestBlockPosition =
          new PathPosition(
              pathPosition.getPathEnvironment(),
              pathPosition.getBlockX(),
              highestY,
              pathPosition.getBlockZ());
      BlockState blockState =
          CHUNK_DATA_PROVIDER_RESOLVER
              .getChunkDataProvider()
              .getBlockState(chunkSnapshot, chunkX, highestY, chunkZ);

      Material material = chunkSnapshot.getBlockType(chunkX, highestY, chunkZ);
      return new PathBlock(highestBlockPosition, new BlockInformation(material, blockState));
    }
  }
}
