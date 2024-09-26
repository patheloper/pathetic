package org.patheloper.model.snapshot.world;

import java.util.Map;
import java.util.Optional;
import org.bukkit.ChunkSnapshot;
import org.patheloper.util.ExpiringHashMap;

/**
 * WorldDomain class is responsible for managing chunk snapshots and caching them using an expiring
 * map.
 */
public class WorldDomain {

  // Map that stores ChunkSnapshots with expiration functionality
  private final Map<Long, ExpiringHashMap.Entry<ChunkSnapshot>> chunkSnapshotCache =
      new ExpiringHashMap<>();

  /**
   * Retrieves a ChunkSnapshot for the specified key.
   *
   * @param chunkKey the unique key representing the chunk
   * @return an Optional containing the ChunkSnapshot if present, otherwise empty
   */
  public Optional<ChunkSnapshot> getSnapshot(long chunkKey) {
    return Optional.ofNullable(getChunkSnapshotEntry(chunkKey))
        .map(ExpiringHashMap.Entry::getValue);
  }

  /**
   * Adds a new ChunkSnapshot to the cache for the specified key.
   *
   * @param chunkKey the unique key representing the chunk
   * @param snapshot the ChunkSnapshot to be stored
   */
  public void addSnapshot(final long chunkKey, final ChunkSnapshot snapshot) {
    chunkSnapshotCache.put(chunkKey, createChunkSnapshotEntry(snapshot));
  }

  /**
   * Removes the ChunkSnapshot from the cache for the specified key.
   *
   * @param chunkKey the unique key representing the chunk
   */
  public void removeSnapshot(final long chunkKey) {
    chunkSnapshotCache.remove(chunkKey);
  }

  /**
   * Checks if the chunk snapshot exists in the cache.
   *
   * @param chunkKey the unique key representing the chunk
   * @return true if the snapshot exists, false otherwise
   */
  public boolean containsSnapshot(final long chunkKey) {
    return chunkSnapshotCache.containsKey(chunkKey);
  }

  /**
   * Helper method to retrieve the ExpiringHashMap entry for the given chunk key.
   *
   * @param chunkKey the unique key representing the chunk
   * @return the ExpiringHashMap.Entry containing the ChunkSnapshot, or null if not found
   */
  private ExpiringHashMap.Entry<ChunkSnapshot> getChunkSnapshotEntry(long chunkKey) {
    return chunkSnapshotCache.get(chunkKey);
  }

  /**
   * Helper method to create an expiring map entry for a ChunkSnapshot.
   *
   * @param snapshot the ChunkSnapshot to be stored in the cache
   * @return an ExpiringHashMap.Entry containing the ChunkSnapshot
   */
  private ExpiringHashMap.Entry<ChunkSnapshot> createChunkSnapshotEntry(ChunkSnapshot snapshot) {
    return new ExpiringHashMap.Entry<>(snapshot);
  }
}
