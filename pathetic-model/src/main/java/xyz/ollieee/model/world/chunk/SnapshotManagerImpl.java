package xyz.ollieee.model.world.chunk;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;

import xyz.ollieee.Pathetic;
import xyz.ollieee.model.world.WorldDomain;
import xyz.ollieee.api.pathing.world.chunk.SnapshotManager;
import xyz.ollieee.util.ChunkUtils;
import xyz.ollieee.utils.BukkitConverter;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathBlockType;
import xyz.ollieee.api.wrapper.PathLocation;

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
            
            if (snapshot.isPresent())
                return new PathBlock(location, BukkitConverter.toPathBlockType(ChunkUtils.getMaterial(snapshot.get(), location.getBlockX() - chunkX * 16, location.getBlockY(), location.getBlockZ() - chunkZ * 16)));
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
    
        Bukkit.getScheduler().runTaskLater(Pathetic.getPluginInstance(), () -> worldDomain.removeSnapshot(key), 1200L);
    }
}
