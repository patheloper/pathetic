package xyz.ollieee.model.snapshot;

import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import xyz.ollieee.Pathetic;
import xyz.ollieee.api.snapshot.NMSInterface;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathBlockType;
import xyz.ollieee.api.wrapper.PathLocation;
import xyz.ollieee.api.wrapper.PathWorld;
import xyz.ollieee.model.snapshot.world.WorldDomain;
import xyz.ollieee.util.ChunkUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SnapshotHolder {

    private final Map<UUID, WorldDomain> snapshots = new HashMap<>();
    private final NMSInterface nmsInterface;

    public SnapshotHolder() {
        this.nmsInterface = Pathetic.getNMSUtils().getNmsInterface();
    }

    public synchronized boolean removeSnapshot(UUID world, int chunkX, int chunkZ) {
        if (this.snapshots.containsKey(world)) {
            WorldDomain worldDomain = this.snapshots.get(world);
            long chunkKey = ChunkUtils.getChunkKey(chunkX, chunkZ);
            if (worldDomain.containsSnapshot(chunkKey)) {
                worldDomain.removeSnapshot(chunkKey);
                return true;
            }
        }
        return false;
    }

    public synchronized Optional<PathBlock> getBlock(PathLocation location, Boolean loadChunks) {
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;

        if (this.snapshots.containsKey(location.getPathWorld().getUuid())) {
            WorldDomain worldDomain = this.snapshots.get(location.getPathWorld().getUuid());
            long chunkKey = ChunkUtils.getChunkKey(chunkX, chunkZ);
            Optional<ChunkSnapshot> snapshot = worldDomain.getSnapshot(chunkKey);
            if (snapshot.isPresent()) {
                PathBlockType pathBlockType = PathBlockType.fromMaterial(ChunkUtils.getMaterial(snapshot.get(), location.getBlockX() - chunkX * 16, location.getBlockY(), location.getBlockZ() - chunkZ * 16));
                return Optional.of(new PathBlock(location, pathBlockType));
            }

        } else {
            // Means the snapshot is not loaded
            if (loadChunks) {
                ChunkSnapshot chunkSnapshot = this.retrieveSnapshot(location);
                PathBlockType pathBlockType = PathBlockType.fromMaterial(ChunkUtils.getMaterial(chunkSnapshot, location.getBlockX() - chunkX * 16, location.getBlockY(), location.getBlockZ() - chunkZ * 16));
                return Optional.of(new PathBlock(location, pathBlockType));
            }
        }
        return Optional.empty();
    }

    private ChunkSnapshot retrieveSnapshot(PathLocation location) {

        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        long key = ChunkUtils.getChunkKey(chunkX, chunkZ);

        WorldDomain worldDomain = this.snapshots.computeIfAbsent(location.getPathWorld().getUuid(), uuid -> new WorldDomain());
        Optional<ChunkSnapshot> snapshot = worldDomain.getSnapshot(key);
        if (snapshot.isPresent()) return snapshot.get();

        ChunkSnapshot chunkSnapshot = retrieveChunkSnapshot(location.getPathWorld(), chunkX, chunkZ);
        if (chunkSnapshot == null) throw new RuntimeException("Could not retrieve chunk snapshot");

        worldDomain.addSnapshot(key, chunkSnapshot);
        return chunkSnapshot;
    }

    private ChunkSnapshot retrieveChunkSnapshot(PathWorld world, int chunkX, int chunkZ) {
        World bukkitWorld = Bukkit.getWorld(world.getUuid());
        return this.nmsInterface.getSnapshot(bukkitWorld, chunkX, chunkZ);
    }

}
