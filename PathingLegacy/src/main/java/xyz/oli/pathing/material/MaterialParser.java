package xyz.oli.pathing.material;

import lombok.NonNull;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;

public interface MaterialParser {

    @NonNull
    Material getMaterial(@NonNull ChunkSnapshot snapshot, int x, int y, int z);

    boolean isAir(@NonNull Block block);

    boolean isLiquid(@NonNull Block block);

    boolean isPassable(@NonNull Block block);

    boolean isSolid(@NonNull Block block);

    boolean isSolid(@NonNull Material material);

    boolean isAir(@NonNull Material material);
}
