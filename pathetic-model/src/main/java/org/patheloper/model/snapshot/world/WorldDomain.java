package org.patheloper.model.snapshot.world;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.ChunkSnapshot;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class WorldDomain {

    private static final Cache<Long, ChunkSnapshot> CHUNK_SNAPSHOT_CACHE = CacheBuilder.newBuilder()
            .maximumSize(100000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    public Optional<ChunkSnapshot> getSnapshot(long key) {
        return Optional.ofNullable(CHUNK_SNAPSHOT_CACHE.getIfPresent(key));
    }

    public void addSnapshot(final long key, final ChunkSnapshot snapshot) {
        CHUNK_SNAPSHOT_CACHE.put(key, snapshot);
    }

    public void removeSnapshot(final long key) {
        CHUNK_SNAPSHOT_CACHE.invalidate(key);
    }

    public boolean containsSnapshot(final long key) {
        return CHUNK_SNAPSHOT_CACHE.getIfPresent(key) != null;
    }
}
