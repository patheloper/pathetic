package org.patheloper.model.snapshot;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathBlockType;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.api.wrapper.PathDomain;
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

    private static synchronized Optional<PathBlock> fetchBlock(PathPosition position) {

        Optional<ChunkSnapshot> chunkSnapshotOptional = getChunkSnapshot(position);

        int chunkX = position.getBlockX() >> 4;
        int chunkZ = position.getBlockZ() >> 4;

        if (chunkSnapshotOptional.isPresent()) {
            PathBlockType pathBlockType = PathBlockType.fromMaterial(ChunkUtils.getMaterial(chunkSnapshotOptional.get(),
                    position.getBlockX() - chunkX * 16,
                    position.getBlockY(),
                    position.getBlockZ() - chunkZ * 16));
            return Optional.of(new PathBlock(position, pathBlockType));
        }

        return Optional.empty();
    }

    private static Optional<ChunkSnapshot> getChunkSnapshot(PathPosition position) {

        int chunkX = position.getBlockX() >> 4;
        int chunkZ = position.getBlockZ() >> 4;

        if (SNAPSHOTS_MAP.containsKey(position.getPathDomain().getUuid())) {

            WorldDomain worldDomain = SNAPSHOTS_MAP.get(position.getPathDomain().getUuid());
            long chunkKey = ChunkUtils.getChunkKey(chunkX, chunkZ);

            return worldDomain.getSnapshot(chunkKey);
        }

        return Optional.empty();
    }

    /**
     * @return the block or null if the block is not loaded
     */
    @Override
    public PathBlock getBlock(@NonNull PathPosition position) {
        Optional<PathBlock> block = fetchBlock(position);
        return block.orElse(null);
    }

    public static class RequestingSnapshotManager extends FailingSnapshotManager {

        private static ChunkSnapshot retrieveChunkSnapshot(PathDomain world, int chunkX, int chunkZ) {
            World bukkitWorld = Bukkit.getWorld(world.getUuid());
            return NMS_UTILS.getNmsInterface().getSnapshot(bukkitWorld, chunkX, chunkZ);
        }

        private static ChunkSnapshot retrieveSnapshot(PathPosition position) {

            int chunkX = position.getBlockX() >> 4;
            int chunkZ = position.getBlockZ() >> 4;

            Optional<ChunkSnapshot> chunkSnapshotOptional = getChunkSnapshot(position);

            return chunkSnapshotOptional.orElseGet(() -> {

                ChunkSnapshot chunkSnapshot = retrieveChunkSnapshot(position.getPathDomain(), chunkX, chunkZ);

                if (chunkSnapshot == null)
                    throw new IllegalStateException("Could not retrieve chunk snapshot --> BOOM!");

                WorldDomain worldDomain = SNAPSHOTS_MAP.computeIfAbsent(position.getPathDomain().getUuid(), uuid -> new WorldDomain());
                worldDomain.addSnapshot(ChunkUtils.getChunkKey(chunkX, chunkZ), chunkSnapshot);

                return chunkSnapshot;
            });
        }

        private static synchronized PathBlock ensureBlock(PathPosition pathPosition) {

            int chunkX = pathPosition.getBlockX() >> 4;
            int chunkZ = pathPosition.getBlockZ() >> 4;

            ChunkSnapshot chunkSnapshot = retrieveSnapshot(pathPosition);
            PathBlockType pathBlockType = PathBlockType
                    .fromMaterial(ChunkUtils.getMaterial(chunkSnapshot,
                            pathPosition.getBlockX() - chunkX * 16,
                            pathPosition.getBlockY(),
                            pathPosition.getBlockZ() - chunkZ * 16));

            return new PathBlock(pathPosition, pathBlockType);
        }

        @Override
        public PathBlock getBlock(@NonNull PathPosition position) {
            PathBlock block = super.getBlock(position);
            return block == null ? ensureBlock(position) : block;
        }
    }

}
