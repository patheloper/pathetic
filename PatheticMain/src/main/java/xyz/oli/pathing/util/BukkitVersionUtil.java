package xyz.oli.pathing.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class BukkitVersionUtil {

    private final double current;

    static {
        String version = Bukkit.getBukkitVersion().split("-")[0];
        current = Double.parseDouble(version.split("\\.")[1]);
    }
    
    public double get() {
        return current;
    }

    public boolean isUnder(double version) {
        return current < version;
    }

    public boolean isOver(double version) {
        return current > version;
    }

    public boolean isEquals(double version) {
        return current == version;
    }

}
