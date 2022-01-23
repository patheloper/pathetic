package xyz.oli.pathing.model.path.finder.strategy.chunks;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import org.bukkit.ChunkSnapshot;

import xyz.oli.pathing.PathfindingPlugin;
import xyz.oli.pathing.model.wrapper.BukkitConverter;
import xyz.oli.pathing.model.wrapper.PathBlock;
import xyz.oli.pathing.model.wrapper.PathBlockType;
import xyz.oli.pathing.model.wrapper.PathLocation;

public class SnapshotManager {

    private static final Long2ObjectOpenHashMap<ChunkSnapshot> snapshots = new Long2ObjectOpenHashMap<>();

    public static PathBlock getBlock(PathLocation location) {
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        long key = ChunkUtils.getChunkKey(location.getPathWorld().getUuid(), chunkX, chunkZ);

        if (snapshots.containsKey(key)) {
            ChunkSnapshot snapshot = snapshots.get(key);
            return new PathBlock(location, BukkitConverter.toPathBlockType(snapshot.getBlockType(location.getBlockX() - chunkX * 16, location.getBlockY(), location.getBlockZ() - chunkZ * 16)));
        }
        return fetchAndGetBlock(location, chunkX, chunkZ, key);
    }

    private static PathBlock fetchAndGetBlock(PathLocation location, int chunkX, int chunkZ, long key) {
        try {
            ChunkSnapshot chunkSnapshot = location.getPathWorld().getWorld().getChunkAt(chunkX, chunkZ).getChunkSnapshot();
            snapshots.put(key, chunkSnapshot);
            PathBlockType pathBlockType = BukkitConverter.toPathBlockType(chunkSnapshot.getBlockType(location.getBlockX() - chunkX * 16, location.getBlockY(), location.getBlockZ() - chunkZ * 16));
            return new PathBlock(location, pathBlockType);
        }catch (Exception e) {
            PathfindingPlugin.getInstance().getLogger().warning("Error fetching Block: " + e.getMessage());
            return null;
        }
    }
}
