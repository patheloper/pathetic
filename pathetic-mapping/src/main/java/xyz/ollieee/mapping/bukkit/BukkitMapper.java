package xyz.ollieee.mapping.bukkit;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.Vector;

import xyz.ollieee.api.wrapper.*;
import xyz.ollieee.model.snapshot.SnapshotManagerImpl;

import java.util.Arrays;

@UtilityClass
public class BukkitMapper {

    @NonNull
    public Location toLocation(@NonNull PathLocation pathLocation) {
        return new Location(toWorld(pathLocation.getPathWorld()), pathLocation.getX(), pathLocation.getY(), pathLocation.getZ());
    }

    @NonNull
    public PathLocation toPathLocation(@NonNull Location location) {

        if (location.getWorld() == null)
            throw new IllegalStateException("World is null");

        return new PathLocation(toPathWorld(location.getWorld()), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @NonNull
    public Vector toVector(PathVector pathVector) {
        return new Vector(pathVector.getX(), pathVector.getY(), pathVector.getZ());
    }

    @NonNull
    public PathVector toPathVector(Vector vector) {
        return new PathVector(vector.getX(), vector.getY(), vector.getZ());
    }

    @NonNull
    public Block toBlock(@NonNull PathBlock pathBlock) {
        return toLocation(pathBlock.getPathLocation()).getBlock();
    }

    @NonNull
    public PathBlock toPathBlock(@NonNull Block block) {
        return new PathBlock(new PathLocation(toPathWorld(block.getWorld()), block.getX(), block.getY(), block.getZ()), toPathBlockType(block));
    }

    public World toWorld(@NonNull PathWorld pathWorld) {
        return Bukkit.getWorld(pathWorld.getUuid());
    }

    @NonNull
    public PathWorld toPathWorld(@NonNull WorldInfo world) {
        return new PathWorld(world.getUID(), world.getName(), getMinHeight(world), getMaxHeight(world));
    }

    @NonNull
    public PathBlockType toPathBlockType(@NonNull Block block) {
        return toPathBlockType(block.getType());
    }

    @NonNull
    public PathBlockType toPathBlockType(@NonNull Material material) {
        return SnapshotManagerImpl.toPathBlockType(material);
    }

    private final boolean IS_NEWER_WORLD;
    static {
        IS_NEWER_WORLD = Arrays.stream(World.class.getMethods()).anyMatch(method -> "getMinHeight".equalsIgnoreCase(method.getName()));
    }

    private int getMinHeight(WorldInfo world) {
        return IS_NEWER_WORLD ? world.getMinHeight() : 0;
    }

    private int getMaxHeight(WorldInfo world) {
        return IS_NEWER_WORLD ? world.getMaxHeight() : 256;
    }
}
