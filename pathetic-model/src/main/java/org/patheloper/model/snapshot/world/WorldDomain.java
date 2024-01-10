package org.patheloper.model.snapshot.world;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.bukkit.ChunkSnapshot;

public class WorldDomain {

  private final Cache<Long, ChunkSnapshot> chunkSnapshotCache =
      CacheBuilder.newBuilder().maximumSize(100000).expireAfterAccess(5, TimeUnit.MINUTES).build();

  public Optional<ChunkSnapshot> getSnapshot(long key) {
    return Optional.ofNullable(chunkSnapshotCache.getIfPresent(key));
  }

  public void addSnapshot(final long key, final ChunkSnapshot snapshot) {
    chunkSnapshotCache.put(key, snapshot);
  }

  public void removeSnapshot(final long key) {
    chunkSnapshotCache.invalidate(key);
  }

  public boolean containsSnapshot(final long key) {
    return chunkSnapshotCache.getIfPresent(key) != null;
  }
}
