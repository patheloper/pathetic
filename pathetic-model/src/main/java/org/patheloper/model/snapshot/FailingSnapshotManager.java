package org.patheloper.model.snapshot;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathBlockType;
import org.patheloper.api.wrapper.PathLocation;
import org.patheloper.api.wrapper.PathWorld;
import org.patheloper.model.snapshot.world.WorldDomain;
import org.patheloper.nms.NMSUtils;
import org.patheloper.util.BukkitVersionUtil;
import org.patheloper.util.ChunkUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FailingSnapshotManager implements SnapshotManager {

    private static final Map<UUID, WorldDomain> SNAPSHOTS_MAP = new HashMap<>();

    private static final NMSUtils NMS_UTILS;

    static {
        NMS_UTILS = new NMSUtils((int) BukkitVersionUtil.get());
    }

    public static void invalidateChunk(UUID worldUUID, int chunkX, int chunkZ) {

        if (SNAPSHOTS_MAP.containsKey(worldUUID)) {

            WorldDomain worldDomain = SNAPSHOTS_MAP.get(worldUUID);
            long chunkKey = ChunkUtils.getChunkKey(chunkX, chunkZ);

            if (worldDomain.containsSnapshot(chunkKey))
                worldDomain.removeSnapshot(chunkKey);
        }
    }

    private static synchronized Optional<PathBlock> fetchBlock(PathLocation location) {

        Optional<ChunkSnapshot> chunkSnapshotOptional = getChunkSnapshot(location);

        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;

        if (chunkSnapshotOptional.isPresent()) {
            PathBlockType pathBlockType = PathBlockType.fromMaterial(ChunkUtils.getMaterial(chunkSnapshotOptional.get(),
                    location.getBlockX() - chunkX * 16,
                    location.getBlockY(),
                    location.getBlockZ() - chunkZ * 16));
            return Optional.of(new PathBlock(location, pathBlockType));
        }

        return Optional.empty();
    }

    private static Optional<ChunkSnapshot> getChunkSnapshot(PathLocation location) {

        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;

        if (SNAPSHOTS_MAP.containsKey(location.getPathWorld().getUuid())) {

            WorldDomain worldDomain = SNAPSHOTS_MAP.get(location.getPathWorld().getUuid());
            long chunkKey = ChunkUtils.getChunkKey(chunkX, chunkZ);

            return worldDomain.getSnapshot(chunkKey);
        }

        return Optional.empty();
    }

    /**
     * @return the block or null if the block is not loaded
     */
    @Override
    public PathBlock getBlock(@NonNull PathLocation location) {
        Optional<PathBlock> block = fetchBlock(location);
        return block.orElse(null);
    }

    public static class RequestingSnapshotManager extends FailingSnapshotManager {

        private static ChunkSnapshot retrieveChunkSnapshot(PathWorld world, int chunkX, int chunkZ) {
            World bukkitWorld = Bukkit.getWorld(world.getUuid());
            return NMS_UTILS.getNmsInterface().getSnapshot(bukkitWorld, chunkX, chunkZ);
        }

        private static ChunkSnapshot retrieveSnapshot(PathLocation location) {

            int chunkX = location.getBlockX() >> 4;
            int chunkZ = location.getBlockZ() >> 4;

            Optional<ChunkSnapshot> chunkSnapshotOptional = getChunkSnapshot(location);

            return chunkSnapshotOptional.orElseGet(() -> {

                ChunkSnapshot chunkSnapshot = retrieveChunkSnapshot(location.getPathWorld(), chunkX, chunkZ);

                if (chunkSnapshot == null)
                    throw new IllegalStateException("Could not retrieve chunk snapshot --> BOOM!");

                WorldDomain worldDomain = SNAPSHOTS_MAP.computeIfAbsent(location.getPathWorld().getUuid(), uuid -> new WorldDomain());
                worldDomain.addSnapshot(ChunkUtils.getChunkKey(chunkX, chunkZ), chunkSnapshot);

                return chunkSnapshot;
            });
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
