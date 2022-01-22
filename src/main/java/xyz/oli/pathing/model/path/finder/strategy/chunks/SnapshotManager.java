package xyz.oli.pathing.model.path.finder.strategy.chunks;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import org.bukkit.ChunkSnapshot;

import xyz.oli.pathing.model.wrapper.BukkitConverter;
import xyz.oli.pathing.model.wrapper.PathBlock;
import xyz.oli.pathing.model.wrapper.PathLocation;

public class SnapshotManager {

    Long2ObjectOpenHashMap<ChunkSnapshot> snapshots = new Long2ObjectOpenHashMap<>();

    public static PathBlock getBlock(PathLocation location) {
        // Change for proper impl
        return new PathBlock(location, BukkitConverter.toPathBlockType(BukkitConverter.toLocation(location).getBlock()));
    }
}
