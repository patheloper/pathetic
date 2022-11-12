package org.patheloper.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class BukkitVersionUtil {

    private final double CURRENT_VERSION;

    static {
        String version = Bukkit.getBukkitVersion().split("-")[0];
        CURRENT_VERSION = Double.parseDouble(version.split("\\.")[1]);
    }
    
    public double get() {
        return CURRENT_VERSION;
    }

    public boolean isUnder(double version) {
        return CURRENT_VERSION < version;
    }

    public boolean isOver(double version) {
        return CURRENT_VERSION > version;
    }

    public boolean isEquals(double version) {
        return CURRENT_VERSION == version;
    }

}
