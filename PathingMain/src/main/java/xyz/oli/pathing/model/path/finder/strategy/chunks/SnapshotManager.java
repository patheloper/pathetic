package xyz.oli.pathing.model.path.finder.strategy.chunks;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;

import xyz.oli.pathing.PathfindingPlugin;
import xyz.oli.pathing.model.wrapper.BukkitConverter;
import xyz.oli.pathing.model.wrapper.PathBlock;
import xyz.oli.pathing.model.wrapper.PathBlockType;
import xyz.oli.pathing.model.wrapper.PathLocation;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class SnapshotManager {

    private final Map<Long, ChunkSnapshot> snapshots = new HashMap<>();

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
            ChunkSnapshot chunkSnapshot = location.getPathWorld().getWorld().getChunkAt(chunkX, chunkZ).getChunkSnapshot();
            addSnapshot(key, chunkSnapshot);
            PathBlockType pathBlockType = BukkitConverter.toPathBlockType(ChunkUtils.getMaterial(chunkSnapshot, location.getBlockX() - chunkX * 16, location.getBlockY(), location.getBlockZ() - chunkZ * 16));
            return new PathBlock(location, pathBlockType);
        }catch (Exception e) {
            PathfindingPlugin.getInstance().getLogger().warning("Error fetching Block: " + e.getMessage());
            return new PathBlock(location, PathBlockType.SOLID);
        }
    }

    private void addSnapshot(long key, ChunkSnapshot snapshot) {
        snapshots.put(key, snapshot);
        Bukkit.getScheduler().runTaskLaterAsynchronously(PathfindingPlugin.getInstance(), () -> {
            snapshots.remove(key, snapshot);
        }, 1200);
    }
}
