package org.patheloper.mapping.bukkit;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.patheloper.api.wrapper.BlockInformation;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathEnvironment;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.api.wrapper.PathVector;
import org.patheloper.util.ErrorLogger;

import java.util.Arrays;

@UtilityClass
public class BukkitMapper {

    @NonNull
    public Location toLocation(@NonNull PathPosition pathPosition) {
        return new Location(toWorld(pathPosition.getPathEnvironment()),
                pathPosition.getX(),
                pathPosition.getY(),
                pathPosition.getZ());
    }

    @NonNull
    public PathPosition toPathPosition(@NonNull Location location) {

        if (location.getWorld() == null)
            throw ErrorLogger.logFatalError("World is null");

        return new PathPosition(toPathWorld(location.getWorld()),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ());
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
        return toLocation(pathBlock.getPathPosition()).getBlock();
    }

    @NonNull
    public PathBlock toPathBlock(@NonNull Block block) {
        return new PathBlock(new PathPosition(
                toPathWorld(block.getWorld()),
                block.getX(),
                block.getY(),
                block.getZ()),
                new BlockInformation(block.getType(), block.getState()));
    }

    public World toWorld(@NonNull PathEnvironment pathEnvironment) {
        return Bukkit.getWorld(pathEnvironment.getUuid());
    }

    @NonNull
    public PathEnvironment toPathWorld(@NonNull World world) {
        return new PathEnvironment(world.getUID(), world.getName(), getMinHeight(world), getMaxHeight(world));
    }

    private final boolean IS_NEWER_WORLD;
    static {
        IS_NEWER_WORLD = Arrays.stream(World.class.getMethods())
                .anyMatch(method -> "getMinHeight".equalsIgnoreCase(method.getName()));
    }

    private int getMinHeight(World world) {
        return IS_NEWER_WORLD ? world.getMinHeight() : 0;
    }

    private int getMaxHeight(World world) {
        return IS_NEWER_WORLD ? world.getMaxHeight() : 256;
    }
}
