package xyz.oli.pathing.model.pathing.world.chunk;

import lombok.NonNull;
import org.bukkit.ChunkSnapshot;

import xyz.oli.pathing.Pathetic;
import xyz.oli.api.pathing.world.chunk.SnapshotManager;
import xyz.oli.pathing.model.pathing.world.WorldDomain;
import xyz.oli.pathing.util.ChunkUtils;
import xyz.oli.pathing.util.PathingScheduler;
import xyz.oli.api.wrapper.BukkitConverter;
import xyz.oli.api.wrapper.PathBlock;
import xyz.oli.api.wrapper.PathBlockType;
import xyz.oli.api.wrapper.PathLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SnapshotManagerImpl implements SnapshotManager {

    private final Map<UUID, WorldDomain> snapshots = new HashMap<>();

    @Override
    public PathBlock getBlock(@NonNull PathLocation location) {
        
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        long key = ChunkUtils.getChunkKey(chunkX, chunkZ);

        if (snapshots.containsKey(location.getPathWorld().getUuid())) {
            
            WorldDomain worldDomain = snapshots.get(location.getPathWorld().getUuid());
            Optional<ChunkSnapshot> snapshot = worldDomain.getSnapshot(key);
            
            if (snapshot.isPresent()) return new PathBlock(location, BukkitConverter.toPathBlockType(ChunkUtils.getMaterial(snapshot.get(), location.getBlockX() - chunkX * 16, location.getBlockY(), location.getBlockZ() - chunkZ * 16)));
        }
        
        return fetchAndGetBlock(location, chunkX, chunkZ, key);
    }

    private PathBlock fetchAndGetBlock(@NonNull PathLocation location, int chunkX, int chunkZ, long key) {
        
        try {
            
            ChunkSnapshot chunkSnapshot = BukkitConverter.toWorld(location.getPathWorld()).getChunkAt(chunkX, chunkZ).getChunkSnapshot();
            addSnapshot(location, key, chunkSnapshot);
            
            PathBlockType pathBlockType = BukkitConverter.toPathBlockType(ChunkUtils.getMaterial(chunkSnapshot, location.getBlockX() - chunkX * 16, location.getBlockY(), location.getBlockZ() - chunkZ * 16));
            return new PathBlock(location, pathBlockType);
            
        } catch (Exception e) {
            
            Pathetic.getPluginLogger().warning("Error fetching Block: " + e.getMessage());
            return new PathBlock(location, PathBlockType.SOLID);
        }
    }

    private void addSnapshot(@NonNull PathLocation location, long key, @NonNull ChunkSnapshot snapshot) {
        
        if (!snapshots.containsKey(location.getPathWorld().getUuid())) snapshots.put(location.getPathWorld().getUuid(), new WorldDomain());
        
        WorldDomain worldDomain = snapshots.get(location.getPathWorld().getUuid());
        worldDomain.addSnapshot(key, snapshot);
        
        PathingScheduler.runLaterAsync(() -> worldDomain.removeSnapshot(key), 1200L);
    }
}
