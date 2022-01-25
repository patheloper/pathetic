package xyz.oli.pathing.model.path.finder.strategy.chunks.utils;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;

import java.lang.reflect.Field;

public class LegacyVersionHelper {

    public static Material getMaterial(ChunkSnapshot snapshot, int x, int y, int z) {
        try {
            Field f = snapshot.getClass().getDeclaredField("blockids");
            f.setAccessible(true);
            short[][] iWantThis = (short[][]) f.get(snapshot);
            return Material.getMaterial(iWantThis[y >> 4][(y & 15) << 8 | z << 4 | x]);

        }catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Material.STONE;
    }
}
