package xyz.ollieee.model.snapshot;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import xyz.ollieee.Pathetic;
import xyz.ollieee.api.snapshot.ChunkSnapshotGrabber;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathBlockType;
import xyz.ollieee.api.wrapper.PathLocation;
import xyz.ollieee.model.world.WorldDomain;
import xyz.ollieee.nms.NMSUtils;
import xyz.ollieee.util.ChunkUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SnapshotManagerImpl implements SnapshotManager {

    private final Map<UUID, WorldDomain> snapshots = new HashMap<>();
    private final ChunkSnapshotGrabber chunkSnapshotGrabber;

    public SnapshotManagerImpl() {
        this.chunkSnapshotGrabber = new NMSUtils(Bukkit.getBukkitVersion().split("\\-")[0].split("\\.")[1]).getChunkSnapshotGrabber();
    }

    @NonNull
    @Override
    public PathBlock getBlock(@NonNull PathLocation location) {
        
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        long key = ChunkUtils.getChunkKey(chunkX, chunkZ);

        if (snapshots.containsKey(location.getPathWorld().getUuid())) {
            
            WorldDomain worldDomain = snapshots.get(location.getPathWorld().getUuid());
            Optional<ChunkSnapshot> snapshot = worldDomain.getSnapshot(key);
            
            if (snapshot.isPresent())
                return new PathBlock(location, Pathetic.getMaterialParser().getPathBlockType(ChunkUtils.getMaterial(snapshot.get(), location.getBlockX() - chunkX * 16, location.getBlockY(), location.getBlockZ() - chunkZ * 16)));
        }
        
        return fetchAndGetBlock(location, chunkX, chunkZ, key);
    }

    private PathBlock fetchAndGetBlock(@NonNull PathLocation location, int chunkX, int chunkZ, long key) {
        
        try {
            // TODO: 27/04/2022 Make this thread safe
            ChunkSnapshot chunkSnapshot = this.retrieveChunkSnapshot(Bukkit.getWorld(location.getPathWorld().getUuid()), chunkX, chunkZ);
            addSnapshot(location, key, chunkSnapshot);

            PathBlockType pathBlockType = Pathetic.getMaterialParser().getPathBlockType(ChunkUtils.getMaterial(chunkSnapshot, location.getBlockX() - chunkX * 16, location.getBlockY(), location.getBlockZ() - chunkZ * 16));
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

    private ChunkSnapshot retrieveChunkSnapshot(World world, int chunkX, int chunkZ) {
        return this.chunkSnapshotGrabber.getSnapshot(world, chunkX, chunkZ);
    }
}
