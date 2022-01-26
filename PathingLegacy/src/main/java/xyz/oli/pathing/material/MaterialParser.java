package xyz.oli.pathing.material;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;

public abstract class MaterialParser {

    public abstract Material getMaterial(ChunkSnapshot snapshot, int x, int y, int z);

    public abstract boolean isAir(Block block);

    public abstract boolean isLiquid(Block block);

    public abstract boolean isPassable(Block block);

    public abstract boolean isSolid(Block block);

    public abstract boolean isSolid(Material material);

    public abstract boolean isAir(Material material);
}
