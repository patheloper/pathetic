package org.patheloper.mapping.bukkit;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathBlockType;
import org.patheloper.api.wrapper.PathLocation;
import org.patheloper.api.wrapper.PathVector;
import org.patheloper.api.wrapper.PathWorld;

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
        return new PathBlock(new PathLocation(toPathWorld(block.getWorld()), block.getX(), block.getY(), block.getZ()), PathBlockType.fromMaterial(block.getType()));
    }

    public World toWorld(@NonNull PathWorld pathWorld) {
        return Bukkit.getWorld(pathWorld.getUuid());
    }

    @NonNull
    public PathWorld toPathWorld(@NonNull World world) {
        return new PathWorld(world.getUID(), world.getName(), getMinHeight(world), getMaxHeight(world));
    }

    private final boolean IS_NEWER_WORLD;
    static {
        IS_NEWER_WORLD = Arrays.stream(World.class.getMethods()).anyMatch(method -> "getMinHeight".equalsIgnoreCase(method.getName()));
    }

    private int getMinHeight(World world) {
        return IS_NEWER_WORLD ? world.getMinHeight() : 0;
    }

    private int getMaxHeight(World world) {
        return IS_NEWER_WORLD ? world.getMaxHeight() : 256;
    }
}
