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
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.model.snapshot.world.WorldDomain;
import org.patheloper.provider.ChunkDataProviderResolver;
import org.patheloper.util.BukkitVersionUtil;
import org.patheloper.util.ChunkUtils;
import org.patheloper.util.ErrorLogger;

/**
 * FailingSnapshotManager implements SnapshotManager to provide an implementation that retrieves
 * block data snapshots from a Minecraft world.
 */
public class FailingSnapshotManager implements SnapshotManager {

  private static final Map<UUID, WorldDomain> SNAPSHOTS_MAP = new ConcurrentHashMap<>();
  private static final ChunkDataProviderResolver CHUNK_DATA_PROVIDER_RESOLVER;

  static {
    CHUNK_DATA_PROVIDER_RESOLVER = initializeChunkDataProviderResolver();
  }

  /**
   * Initializes the ChunkDataProviderResolver based on the server version.
   *
   * @return the initialized ChunkDataProviderResolver
   */
  private static ChunkDataProviderResolver initializeChunkDataProviderResolver() {
    BukkitVersionUtil.Version version = BukkitVersionUtil.getVersion();
    return new ChunkDataProviderResolver((int) version.getMajor(), (int) version.getMinor());
  }

  /**
   * Invalidates the chunk snapshot in the SNAPSHOTS_MAP for a specific chunk in the specified
   * world.
   *
   * @param worldUUID the UUID of the world
   * @param chunkX the X coordinate of the chunk
   * @param chunkZ the Z coordinate of the chunk
   */
  public static void invalidateChunk(UUID worldUUID, int chunkX, int chunkZ) {
    if (isWorldInSnapshotsMap(worldUUID)) {
      WorldDomain worldDomain = getWorldDomain(worldUUID);
      long chunkKey = generateChunkKey(chunkX, chunkZ);
      removeSnapshotFromWorld(worldDomain, chunkKey);
    }
  }

  /**
   * Checks whether the world exists in the snapshots map.
   *
   * @param worldUUID the UUID of the world
   * @return true if the world exists in the snapshot map, false otherwise
   */
  private static boolean isWorldInSnapshotsMap(UUID worldUUID) {
    return SNAPSHOTS_MAP.containsKey(worldUUID);
  }

  /**
   * Retrieves the WorldDomain from the snapshot map for the specified world UUID.
   *
   * @param worldUUID the UUID of the world
   * @return the WorldDomain associated with the world UUID
   */
  private static WorldDomain getWorldDomain(UUID worldUUID) {
    return SNAPSHOTS_MAP.get(worldUUID);
  }

  /**
   * Generates a unique chunk key based on chunk coordinates.
   *
   * @param chunkX the X coordinate of the chunk
   * @param chunkZ the Z coordinate of the chunk
   * @return a unique chunk key as a long value
   */
  private static long generateChunkKey(int chunkX, int chunkZ) {
    return ChunkUtils.getChunkKey(chunkX, chunkZ);
  }

  /**
   * Removes the chunk snapshot from the specified WorldDomain for the given chunk key.
   *
   * @param worldDomain the WorldDomain containing the snapshot
   * @param chunkKey the unique chunk key
   */
  private static void removeSnapshotFromWorld(WorldDomain worldDomain, long chunkKey) {
    worldDomain.removeSnapshot(chunkKey);
  }

  @Override
  public PathBlock getBlock(@NonNull PathPosition position) {
    return fetchBlock(position).orElse(null);
  }

  /**
   * Fetches the PathBlock from a given PathPosition.
   *
   * @param position the PathPosition to retrieve the block from
   * @return an Optional containing the PathBlock, or empty if the block could not be fetched
   */
  private static Optional<PathBlock> fetchBlock(PathPosition position) {
    Optional<ChunkSnapshot> chunkSnapshotOptional = getChunkSnapshot(position);
    return chunkSnapshotOptional.flatMap(
        chunkSnapshot -> createPathBlockFromSnapshot(position, chunkSnapshot));
  }

  /**
   * Creates a PathBlock from the given ChunkSnapshot and PathPosition.
   *
   * @param position the PathPosition
   * @param chunkSnapshot the ChunkSnapshot to get block data from
   * @return an Optional containing the PathBlock
   */
  private static Optional<PathBlock> createPathBlockFromSnapshot(
      PathPosition position, ChunkSnapshot chunkSnapshot) {
    return PathBlockBuilder.build(position, chunkSnapshot);
  }

  /** Responsible for building PathBlock objects from ChunkSnapshot and PathPosition. */
  private static class PathBlockBuilder {

    /**
     * Builds a PathBlock from the given ChunkSnapshot and PathPosition.
     *
     * @param position the PathPosition
     * @param chunkSnapshot the ChunkSnapshot to get block data from
     * @return an Optional containing the PathBlock
     */
    static Optional<PathBlock> build(PathPosition position, ChunkSnapshot chunkSnapshot) {
      int localX = calculateLocalCoordinate(position.getBlockX());
      int localZ = calculateLocalCoordinate(position.getBlockZ());
      Material material = fetchMaterial(chunkSnapshot, position, localX, localZ);
      BlockState blockState = fetchBlockState(chunkSnapshot, position, localX, localZ);
      return createPathBlock(position, material, blockState);
    }

    /**
     * Calculates the local coordinate (X or Z) of the block within the chunk.
     *
     * @param blockCoordinate the global block coordinate (X or Z)
     * @return the local coordinate within the chunk
     */
    private static int calculateLocalCoordinate(int blockCoordinate) {
      int chunkCoordinate = blockCoordinate >> 4;
      return blockCoordinate - chunkCoordinate * 16;
    }

    /**
     * Retrieves the Material of the block from the ChunkSnapshot.
     *
     * @param chunkSnapshot the ChunkSnapshot containing the block data
     * @param position the PathPosition containing the block position
     * @param localX the local X coordinate within the chunk
     * @param localZ the local Z coordinate within the chunk
     * @return the Material of the block
     */
    private static Material fetchMaterial(
        ChunkSnapshot chunkSnapshot, PathPosition position, int localX, int localZ) {
      return ChunkUtils.getMaterial(chunkSnapshot, localX, position.getBlockY(), localZ);
    }

    /**
     * Retrieves the BlockState of the block from the ChunkSnapshot.
     *
     * @param chunkSnapshot the ChunkSnapshot containing the block data
     * @param position the PathPosition containing the block position
     * @param localX the local X coordinate within the chunk
     * @param localZ the local Z coordinate within the chunk
     * @return the BlockState of the block
     */
    private static BlockState fetchBlockState(
        ChunkSnapshot chunkSnapshot, PathPosition position, int localX, int localZ) {
      return CHUNK_DATA_PROVIDER_RESOLVER
          .getChunkDataProvider()
          .getBlockState(chunkSnapshot, localX, position.getBlockY(), localZ);
    }

    /**
     * Builds a PathBlock from the given PathPosition, Material, and BlockState.
     *
     * @param position the PathPosition of the block
     * @param material the Material of the block
     * @param blockState the BlockState of the block
     * @return an Optional containing the PathBlock
     */
    private static Optional<PathBlock> createPathBlock(
        PathPosition position, Material material, BlockState blockState) {
      return Optional.of(new PathBlock(position, new BlockInformation(material, blockState)));
    }
  }

  /**
   * Retrieves a ChunkSnapshot for the given PathPosition.
   *
   * @param position the PathPosition to retrieve the chunk snapshot for
   * @return an Optional containing the ChunkSnapshot, or empty if the chunk is not loaded
   */
  private static Optional<ChunkSnapshot> getChunkSnapshot(PathPosition position) {
    int chunkX = shiftRight(position.getBlockX());
    int chunkZ = shiftRight(position.getBlockZ());
    long chunkKey = generateChunkKey(chunkX, chunkZ);
    Optional<ChunkSnapshot> snapshot = fetchSnapshotFromCache(position, chunkKey);
    return snapshot.isPresent() ? snapshot : fetchSnapshotFromWorld(position, chunkX, chunkZ);
  }

  /**
   * Shifts the block coordinate right to get the chunk coordinate.
   *
   * @param blockCoordinate the global block coordinate
   * @return the chunk coordinate
   */
  private static int shiftRight(int blockCoordinate) {
    return blockCoordinate >> 4;
  }

  /**
   * Fetches the ChunkSnapshot from the cache if available.
   *
   * @param position the PathPosition
   * @param chunkKey the chunk key
   * @return an Optional containing the ChunkSnapshot, or empty if not found in cache
   */
  private static Optional<ChunkSnapshot> fetchSnapshotFromCache(
      PathPosition position, long chunkKey) {
    UUID worldUUID = position.getPathEnvironment().getUuid();

    if (!isWorldInCache(worldUUID)) {
      return Optional.empty();
    }

    WorldDomain worldDomain = getWorldDomainFromCache(worldUUID);
    return getSnapshotFromWorldDomain(worldDomain, chunkKey);
  }

  /**
   * Checks if the world exists in the cache.
   *
   * @param worldUUID the UUID of the world
   * @return true if the world is in the cache, false otherwise
   */
  private static boolean isWorldInCache(UUID worldUUID) {
    return SNAPSHOTS_MAP.containsKey(worldUUID);
  }

  /**
   * Retrieves the WorldDomain from the cache for the specified world UUID.
   *
   * @param worldUUID the UUID of the world
   * @return the WorldDomain associated with the world
   */
  private static WorldDomain getWorldDomainFromCache(UUID worldUUID) {
    return SNAPSHOTS_MAP.get(worldUUID);
  }

  /**
   * Retrieves the ChunkSnapshot from the specified WorldDomain and chunk key.
   *
   * @param worldDomain the WorldDomain containing the chunk snapshots
   * @param chunkKey the chunk key
   * @return an Optional containing the ChunkSnapshot, or empty if not found in the WorldDomain
   */
  private static Optional<ChunkSnapshot> getSnapshotFromWorldDomain(
      WorldDomain worldDomain, long chunkKey) {
    return worldDomain.getSnapshot(chunkKey);
  }

  /**
   * Fetches the ChunkSnapshot from the world if the chunk is loaded.
   *
   * @param position the PathPosition
   * @param chunkX the X coordinate of the chunk
   * @param chunkZ the Z coordinate of the chunk
   * @return an Optional containing the ChunkSnapshot, or empty if the chunk is not loaded
   */
  private static Optional<ChunkSnapshot> fetchSnapshotFromWorld(
      PathPosition position, int chunkX, int chunkZ) {
    World world = Bukkit.getWorld(position.getPathEnvironment().getUuid());
    if (world == null || !world.isChunkLoaded(chunkX, chunkZ)) {
      return Optional.empty();
    }
    return Optional.ofNullable(
        processChunkSnapshot(position, chunkX, chunkZ, fetchChunkSnapshot(world, chunkX, chunkZ)));
  }

  /**
   * Processes and caches the ChunkSnapshot.
   *
   * @param position the PathPosition
   * @param chunkX the X coordinate of the chunk
   * @param chunkZ the Z coordinate of the chunk
   * @param chunkSnapshot the ChunkSnapshot to be processed
   * @return the processed ChunkSnapshot
   */
  private static ChunkSnapshot processChunkSnapshot(
      PathPosition position, int chunkX, int chunkZ, ChunkSnapshot chunkSnapshot) {
    WorldDomain worldDomain =
        SNAPSHOTS_MAP.computeIfAbsent(
            position.getPathEnvironment().getUuid(), uuid -> new WorldDomain());
    worldDomain.addSnapshot(generateChunkKey(chunkX, chunkZ), chunkSnapshot);
    return chunkSnapshot;
  }

  /**
   * Fetches the ChunkSnapshot from the world.
   *
   * @param world the world
   * @param chunkX the X coordinate of the chunk
   * @param chunkZ the Z coordinate of the chunk
   * @return the ChunkSnapshot of the given chunk
   */
  private static ChunkSnapshot fetchChunkSnapshot(World world, int chunkX, int chunkZ) {
    return CHUNK_DATA_PROVIDER_RESOLVER.getChunkDataProvider().getSnapshot(world, chunkX, chunkZ);
  }

  /**
   * Inner class that extends FailingSnapshotManager to ensure block data snapshots are always
   * available.
   */
  public static class RequestingSnapshotManager extends FailingSnapshotManager {

    @Override
    public PathBlock getBlock(@NonNull PathPosition position) {
      PathBlock block = super.getBlock(position);
      return block == null ? ensureBlock(position) : block;
    }

    /**
     * Ensures that a PathBlock is created if it's not already present.
     *
     * @param pathPosition the PathPosition of the block
     * @return the created PathBlock
     */
    private static PathBlock ensureBlock(PathPosition pathPosition) {
      ChunkSnapshot chunkSnapshot = retrieveChunkSnapshot(pathPosition);
      int chunkX = calculateChunkCoordinate(pathPosition.getBlockX());
      int chunkZ = calculateChunkCoordinate(pathPosition.getBlockZ());
      int x = calculateLocalCoordinate(pathPosition.getBlockX(), chunkX);
      int z = calculateLocalCoordinate(pathPosition.getBlockZ(), chunkZ);

      Material material = fetchMaterial(chunkSnapshot, x, pathPosition.getBlockY(), z);
      BlockState blockState = fetchBlockState(chunkSnapshot, x, pathPosition.getBlockY(), z);
      return new PathBlock(pathPosition, new BlockInformation(material, blockState));
    }

    /**
     * Retrieves the ChunkSnapshot for a given PathPosition.
     *
     * @param position the PathPosition to retrieve the chunk snapshot for
     * @return the ChunkSnapshot
     */
    private static ChunkSnapshot retrieveChunkSnapshot(PathPosition position) {
      return getChunkSnapshot(position)
          .orElseGet(
              () -> {
                ChunkSnapshot chunkSnapshot = fetchChunkSnapshotFromWorld(position);
                ensureChunkSnapshot(chunkSnapshot);
                processChunkSnapshot(
                    position,
                    calculateChunkCoordinate(position.getBlockX()),
                    calculateChunkCoordinate(position.getBlockZ()),
                    chunkSnapshot);
                return chunkSnapshot;
              });
    }

    private static void ensureChunkSnapshot(ChunkSnapshot chunkSnapshot) {
      if (chunkSnapshot == null) {
        throw ErrorLogger.logFatalError("Failed to retrieve ChunkSnapshot");
      }
    }

    /**
     * Fetches the ChunkSnapshot from the world.
     *
     * @param position the PathPosition
     * @return the ChunkSnapshot
     */
    private static ChunkSnapshot fetchChunkSnapshotFromWorld(PathPosition position) {
      return fetchChunkSnapshot(
          Bukkit.getWorld(position.getPathEnvironment().getUuid()),
          calculateChunkCoordinate(position.getBlockX()),
          calculateChunkCoordinate(position.getBlockZ()));
    }

    /**
     * Calculates the chunk coordinate based on the block coordinate.
     *
     * @param blockCoordinate the global block coordinate
     * @return the chunk coordinate
     */
    private static int calculateChunkCoordinate(int blockCoordinate) {
      return blockCoordinate >> 4;
    }

    /**
     * Calculates the local coordinate inside the chunk.
     *
     * @param blockCoordinate the global block coordinate
     * @param chunkCoordinate the chunk coordinate
     * @return the local coordinate within the chunk
     */
    private static int calculateLocalCoordinate(int blockCoordinate, int chunkCoordinate) {
      return blockCoordinate - chunkCoordinate * 16;
    }

    /**
     * Retrieves the Material of the block from the ChunkSnapshot.
     *
     * @param chunkSnapshot the ChunkSnapshot containing the block data
     * @param x the local X coordinate within the chunk
     * @param y the Y coordinate (height)
     * @param z the local Z coordinate within the chunk
     * @return the Material of the block
     */
    private static Material fetchMaterial(ChunkSnapshot chunkSnapshot, int x, int y, int z) {
      return ChunkUtils.getMaterial(chunkSnapshot, x, y, z);
    }

    /**
     * Retrieves the BlockState of the block from the ChunkSnapshot.
     *
     * @param chunkSnapshot the ChunkSnapshot containing the block data
     * @param x the local X coordinate within the chunk
     * @param y the Y coordinate (height)
     * @param z the local Z coordinate within the chunk
     * @return the BlockState of the block
     */
    private static BlockState fetchBlockState(ChunkSnapshot chunkSnapshot, int x, int y, int z) {
      return CHUNK_DATA_PROVIDER_RESOLVER
          .getChunkDataProvider()
          .getBlockState(chunkSnapshot, x, y, z);
    }
  }
}
