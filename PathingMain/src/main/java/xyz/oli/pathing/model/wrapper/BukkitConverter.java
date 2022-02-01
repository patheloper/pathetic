package xyz.oli.pathing.model.wrapper;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import xyz.oli.pathing.PathfindingPlugin;
import xyz.oli.pathing.material.MaterialParser;

@UtilityClass
public class BukkitConverter {

    @NonNull
    public Location toLocation(@NonNull PathLocation pathLocation) {
        return new Location(pathLocation.getPathWorld().getWorld(), pathLocation.getX(), pathLocation.getY(), pathLocation.getZ());
    }

    @NonNull
    public PathLocation toPathLocation(@NonNull Location location) {
        return new PathLocation(new PathWorld(location.getWorld().getUID()), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @NonNull
    public Block toBlock(@NonNull PathBlock pathBlock) {
        return toLocation(pathBlock.getPathLocation()).getBlock();
    }

    @NonNull
    public PathBlock toPathBlock(@NonNull Block block) {
        return new PathBlock(new PathLocation(new PathWorld(block.getWorld().getUID()), block.getX(), block.getY(), block.getZ()), toPathBlockType(block));
    }

    @NonNull
    public PathBlockType toPathBlockType(@NonNull Block block) {

        MaterialParser parser = PathfindingPlugin.getInstance().getParser();

        if (parser.isLiquid(block)) return PathBlockType.LIQUID;
        else if (parser.isAir(block)) return PathBlockType.AIR;
        else if (parser.isPassable(block)) return PathBlockType.OTHER;
        else if (parser.isSolid(block)) return PathBlockType.SOLID;
        else return PathBlockType.SOLID;
    }

    @NonNull
    public PathBlockType toPathBlockType(@NonNull Material material) {

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
