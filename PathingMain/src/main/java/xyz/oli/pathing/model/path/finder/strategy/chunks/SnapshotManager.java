package xyz.oli.pathing.model.path.finder.strategy.chunks;

import org.bukkit.ChunkSnapshot;

import xyz.oli.pathing.PathfindingPlugin;
import xyz.oli.pathing.util.PathingScheduler;
import xyz.oli.wrapper.BukkitConverter;
import xyz.oli.wrapper.PathBlock;
import xyz.oli.wrapper.PathBlockType;
import xyz.oli.wrapper.PathLocation;

import java.util.HashMap;
import java.util.Map;

public class SnapshotManager implements xyz.oli.pathing.SnapshotManager {

    private final Map<Long, ChunkSnapshot> snapshots = new HashMap<>();

    @Override
    public PathBlock getBlock(PathLocation location) {
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        long key = ChunkUtils.getChunkKey(location.getPathWorld().getUuid(), chunkX, chunkZ);

        if (snapshots.containsKey(key)) {
            ChunkSnapshot snapshot = snapshots.get(key);
            return new PathBlock(location, BukkitConverter.toPathBlockType(ChunkUtils.getMaterial(snapshot, location.getBlockX() - chunkX * 16, location.getBlockY(), location.getBlockZ() - chunkZ * 16)));
        }
        return fetchAndGetBlock(location, chunkX, chunkZ, key);
    }

    private PathBlock fetchAndGetBlock(PathLocation location, int chunkX, int chunkZ, long key) {
        try {
            ChunkSnapshot chunkSnapshot = BukkitConverter.toWorld(location.getPathWorld()).getChunkAt(chunkX, chunkZ).getChunkSnapshot();
            addSnapshot(key, chunkSnapshot);
            PathBlockType pathBlockType = BukkitConverter.toPathBlockType(ChunkUtils.getMaterial(chunkSnapshot, location.getBlockX() - chunkX * 16, location.getBlockY(), location.getBlockZ() - chunkZ * 16));
            return new PathBlock(location, pathBlockType);
        }catch (Exception e) {
            PathfindingPlugin.getPluginLogger().warning("Error fetching Block: " + e.getMessage());
            return new PathBlock(location, PathBlockType.SOLID);
        }
    }

    private void addSnapshot(long key, ChunkSnapshot snapshot) {
        snapshots.put(key, snapshot);
        PathingScheduler.runLaterAsync(() -> snapshots.remove(key, snapshot), 1200L);
    }
}
