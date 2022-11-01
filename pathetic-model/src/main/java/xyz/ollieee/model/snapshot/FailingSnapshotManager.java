package xyz.ollieee.model.snapshot;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathBlockType;
import xyz.ollieee.api.wrapper.PathLocation;
import xyz.ollieee.api.wrapper.PathWorld;
import xyz.ollieee.model.snapshot.world.WorldDomain;
import xyz.ollieee.nms.NMSUtils;
import xyz.ollieee.util.BukkitVersionUtil;
import xyz.ollieee.util.ChunkUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FailingSnapshotManager implements SnapshotManager {

    private static final Map<UUID, WorldDomain> snapshots = new HashMap<>();

    private static final NMSUtils nmsUtils;

    static {
        nmsUtils = new NMSUtils((int) BukkitVersionUtil.get());
    }

    public static void invalidateChunk(UUID worldUUID, int chunkX, int chunkZ) {

        if (snapshots.containsKey(worldUUID)) {

            WorldDomain worldDomain = snapshots.get(worldUUID);
            long chunkKey = ChunkUtils.getChunkKey(chunkX, chunkZ);

            if (worldDomain.containsSnapshot(chunkKey))
                worldDomain.removeSnapshot(chunkKey);
        }
    }

    private static synchronized Optional<PathBlock> fetchBlock(PathLocation location) {

        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;

        if (snapshots.containsKey(location.getPathWorld().getUuid())) {

            WorldDomain worldDomain = snapshots.get(location.getPathWorld().getUuid());

            long chunkKey = ChunkUtils.getChunkKey(chunkX, chunkZ);
            Optional<ChunkSnapshot> snapshot = worldDomain.getSnapshot(chunkKey);

            if (snapshot.isPresent()) {
                PathBlockType pathBlockType = PathBlockType.fromMaterial(ChunkUtils.getMaterial(snapshot.get(), location.getBlockX() - chunkX * 16, location.getBlockY(), location.getBlockZ() - chunkZ * 16));
                return Optional.of(new PathBlock(location, pathBlockType));
            }

        }

        return Optional.empty();
    }

    @Override
    public PathBlock getBlock(@NonNull PathLocation location) {
        Optional<PathBlock> block = FailingSnapshotManager.fetchBlock(location);
        return block.orElse(null);
    }

    public static class RequestingSnapshotManager extends FailingSnapshotManager {

        private static ChunkSnapshot retrieveChunkSnapshot(PathWorld world, int chunkX, int chunkZ) {
            World bukkitWorld = Bukkit.getWorld(world.getUuid());
            return nmsUtils.getNmsInterface().getSnapshot(bukkitWorld, chunkX, chunkZ);
        }

        private static ChunkSnapshot retrieveSnapshot(PathLocation location) {

            int chunkX = location.getBlockX() >> 4;
            int chunkZ = location.getBlockZ() >> 4;
            long key = ChunkUtils.getChunkKey(chunkX, chunkZ);

            WorldDomain worldDomain = snapshots.computeIfAbsent(location.getPathWorld().getUuid(), uuid -> new WorldDomain());
            Optional<ChunkSnapshot> snapshot = worldDomain.getSnapshot(key);

            if (snapshot.isPresent())
                return snapshot.get();

            ChunkSnapshot chunkSnapshot = retrieveChunkSnapshot(location.getPathWorld(), chunkX, chunkZ);
            if (chunkSnapshot == null)
                throw new IllegalStateException("Could not retrieve chunk snapshot --> BOOM!");

            worldDomain.addSnapshot(key, chunkSnapshot);

            return chunkSnapshot;
        }

        private static synchronized PathBlock ensureBlock(PathLocation pathLocation) {

            int chunkX = pathLocation.getBlockX() >> 4;
            int chunkZ = pathLocation.getBlockZ() >> 4;

            ChunkSnapshot chunkSnapshot = retrieveSnapshot(pathLocation);
            PathBlockType pathBlockType = PathBlockType
                    .fromMaterial(ChunkUtils.getMaterial(chunkSnapshot,
                            pathLocation.getBlockX() - chunkX * 16,
                            pathLocation.getBlockY(),
                            pathLocation.getBlockZ() - chunkZ * 16));

            return new PathBlock(pathLocation, pathBlockType);
        }

        @Override
        public PathBlock getBlock(@NonNull PathLocation location) {
            PathBlock block = super.getBlock(location);
            return block == null ? ensureBlock(location) : block;
        }
    }

}
