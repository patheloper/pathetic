package xyz.oli.pathing.model.wrapper;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import xyz.oli.pathing.PathfindingPlugin;
import xyz.oli.pathing.material.MaterialParser;

import org.jetbrains.annotations.NotNull;

public class BukkitConverter {

    @NotNull
    public static Location toLocation(@NotNull PathLocation pathLocation) {
        return new Location(pathLocation.getPathWorld().getWorld(), pathLocation.getX(), pathLocation.getY(), pathLocation.getZ());
    }

    @NotNull
    public static PathLocation toPathLocation(@NotNull Location location) {
        return new PathLocation(new PathWorld(location.getWorld().getUID()), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @NotNull
    public static Block toBlock(@NotNull PathBlock pathBlock) {
        return toLocation(pathBlock.getPathLocation()).getBlock();
    }

    @NotNull
    public static PathBlock toPathBlock(@NotNull Block block) {
        return new PathBlock(new PathLocation(new PathWorld(block.getWorld().getUID()), block.getX(), block.getY(), block.getZ()), toPathBlockType(block));
    }

    @NotNull
    public static PathBlockType toPathBlockType(@NotNull Block block) {

        MaterialParser parser = PathfindingPlugin.getInstance().getParser();

        if (parser.isLiquid(block)) return PathBlockType.LIQUID;
        else if (parser.isAir(block)) return PathBlockType.AIR;
        else if (parser.isPassable(block)) return PathBlockType.OTHER;
        else if (parser.isSolid(block)) return PathBlockType.SOLID;
        else return PathBlockType.SOLID;
    }

    @NotNull
    public static PathBlockType toPathBlockType(@NotNull Material material) {

        MaterialParser parser = PathfindingPlugin.getInstance().getParser();

        if (parser.isAir(material))
            return PathBlockType.AIR;

        else if (parser.isSolid(material))
            return PathBlockType.SOLID;

        switch (material) {
            case WATER, LAVA -> {
                return PathBlockType.LIQUID;
            }
            case GRASS, TALL_GRASS -> {
                return PathBlockType.OTHER;
            }
        }

        return PathBlockType.SOLID;
    }
}
