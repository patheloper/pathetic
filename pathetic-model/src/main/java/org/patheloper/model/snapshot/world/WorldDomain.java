package org.patheloper.model.snapshot.world;

import java.util.Map;
import java.util.Optional;
import org.bukkit.ChunkSnapshot;
import org.patheloper.util.ExpiringHashMap;

public class WorldDomain {

  private final Map<Long, ChunkSnapshot> chunkSnapshotMap = new ExpiringHashMap<>();

  public Optional<ChunkSnapshot> getSnapshot(long key) {
    return Optional.ofNullable(chunkSnapshotMap.get(key));
  }

  public void addSnapshot(final long key, final ChunkSnapshot snapshot) {
    chunkSnapshotMap.put(key, snapshot);
  }

  public void removeSnapshot(final long key) {
    chunkSnapshotMap.remove(key);
  }

  public boolean containsSnapshot(final long key) {
    return chunkSnapshotMap.get(key) != null;
  }
}
