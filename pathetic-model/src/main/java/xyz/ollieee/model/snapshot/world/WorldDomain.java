package xyz.ollieee.model.snapshot.world;

import org.bukkit.ChunkSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WorldDomain {

    private final Map<Long, ChunkSnapshot> snapshotMap = new HashMap<>();

    public Optional<ChunkSnapshot> getSnapshot(long key) {
        return Optional.ofNullable(this.snapshotMap.getOrDefault(key, null));
    }

    public void addSnapshot(final long key, final ChunkSnapshot snapshot) {
        this.snapshotMap.put(key, snapshot);
    }

    public void removeSnapshot(final long key) {
        this.snapshotMap.remove(key);
    }

    public boolean containsSnapshot(final long key) {
        return this.snapshotMap.containsKey(key);
    }
}
