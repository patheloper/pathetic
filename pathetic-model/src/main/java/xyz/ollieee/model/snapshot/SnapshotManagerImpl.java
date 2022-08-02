package xyz.ollieee.model.snapshot;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;

import org.bukkit.Material;
import org.bukkit.World;
import xyz.ollieee.Pathetic;
import xyz.ollieee.api.snapshot.MaterialParser;
import xyz.ollieee.api.wrapper.PathWorld;
import xyz.ollieee.model.world.WorldDomain;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.util.ChunkUtils;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathBlockType;
import xyz.ollieee.api.wrapper.PathLocation;

import java.util.*;

public class SnapshotManagerImpl implements SnapshotManager {

    private final Map<UUID, WorldDomain> snapshots = new HashMap<>();

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
                return new PathBlock(location, toPathBlockType(ChunkUtils.getMaterial(snapshot.get(), location.getBlockX() - chunkX * 16, location.getBlockY(), location.getBlockZ() - chunkZ * 16)));
        }
        
        return fetchAndGetBlock(location, chunkX, chunkZ, key);
    }

    private PathBlock fetchAndGetBlock(@NonNull PathLocation location, int chunkX, int chunkZ, long key) {
        
        try {
            // TODO: 27/04/2022 Make this thread safe
            ChunkSnapshot chunkSnapshot = Bukkit.getWorld(location.getPathWorld().getUuid()).getChunkAt(chunkX, chunkZ).getChunkSnapshot();
            addSnapshot(location, key, chunkSnapshot);
            
            PathBlockType pathBlockType = toPathBlockType(ChunkUtils.getMaterial(chunkSnapshot, location.getBlockX() - chunkX * 16, location.getBlockY(), location.getBlockZ() - chunkZ * 16));
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

    public static PathBlockType toPathBlockType(@NonNull Material material) {

        MaterialParser parser = Pathetic.getMaterialParser();

        if (parser.isAir(material))
            return PathBlockType.AIR;

        switch (material) {
            case WATER:
            case LAVA: return PathBlockType.LIQUID;
            case GRASS:
            case TALL_GRASS: return PathBlockType.OTHER;
            default: return PathBlockType.SOLID;
        }
    }

    private PathWorld toPathWorld(@NonNull World world) {

        final int minHeight = hasMethod(world.getClass(), "getMinHeight") ? world.getMinHeight() : 0;
        final int maxHeight = hasMethod(world.getClass(), "getMaxHeight") ? world.getMaxHeight() : 256;
        return new PathWorld(world.getUID(), world.getName(), minHeight, maxHeight);
    }

    private boolean hasMethod(Class<? extends World> worldClass, final String name) {
        return Arrays.stream(worldClass.getMethods()).anyMatch(method -> method.getName().equalsIgnoreCase(name));
    }
}
