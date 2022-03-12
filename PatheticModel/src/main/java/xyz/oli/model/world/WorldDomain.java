package xyz.oli.model.world;

import org.bukkit.ChunkSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WorldDomain {

    private final Map<Long, ChunkSnapshot> snapshotMap;

    public WorldDomain() {
        this.snapshotMap = new HashMap<>();
    }

    public Optional<ChunkSnapshot> getSnapshot(final long key) {
        return Optional.ofNullable(this.snapshotMap.getOrDefault(key, null));
    }

    public void addSnapshot(final long key, final ChunkSnapshot snapshot) {
        this.snapshotMap.put(key, snapshot);
    }

    public void removeSnapshot(final long key) {
        this.snapshotMap.remove(key);
    }
}
