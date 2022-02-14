package xyz.oli.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import xyz.oli.api.PatheticAPI;
import xyz.oli.api.material.MaterialParser;
import xyz.oli.api.wrapper.*;

@UtilityClass
public class BukkitConverter {

    @NonNull
    public Location toLocation(@NonNull PathLocation pathLocation) {
        return new Location(toWorld(pathLocation.getPathWorld()), pathLocation.getX(), pathLocation.getY(), pathLocation.getZ());
    }

    @NonNull
    public PathLocation toPathLocation(@NonNull Location location) {
        if (location.getWorld() == null) {
            throw new NullPointerException("World is null");
        }
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
    public PathWorld toPathWorld(@NonNull World world) {
        return new PathWorld(world.getUID(), world.getName());
    }

    @NonNull
    public PathBlockType toPathBlockType(@NonNull Block block) {

        MaterialParser parser = PatheticAPI.getParser();

        if (parser.isLiquid(block)) return PathBlockType.LIQUID;
        else if (parser.isAir(block)) return PathBlockType.AIR;
        else if (parser.isPassable(block)) return PathBlockType.OTHER;
        else if (parser.isSolid(block)) return PathBlockType.SOLID;
        else return PathBlockType.SOLID;
    }

    @NonNull
    public PathBlockType toPathBlockType(@NonNull Material material) {

        MaterialParser parser = PatheticAPI.getParser();

        if (parser.isAir(material))
            return PathBlockType.AIR;

        switch (material) {
            case WATER, LAVA: return PathBlockType.LIQUID;
            case GRASS, TALL_GRASS: return PathBlockType.OTHER;
            default: return PathBlockType.SOLID;
        }
    }
}
