package org.patheloper.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class BukkitVersionUtil {

    private static final double CURRENT_MAJOR;
    private static final double CURRENT_MINOR;

    static {
        String[] versionParts = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        CURRENT_MAJOR = Double.parseDouble(versionParts[1]);
        CURRENT_MINOR = versionParts.length >= 3 ? Double.parseDouble(versionParts[2]): 0;
    }

    public static Version getVersion() {
        return new Version(CURRENT_MAJOR, CURRENT_MINOR);
    }

    public record Version(double major, double minor) {

        public static Version of(double major, double minor) {
            return new Version(major, minor);
        }

        public boolean isUnder(double major, double minor) {
            if (this.major < major)
                return true;
            return this.major == major && this.minor < minor;
        }

        public boolean isUnder(Version that) {
            return this.isUnder(that.major, that.minor);
        }

        public boolean isOver(Version that) {
            if (this.major > that.major)
                return true;
            return this.major == that.major && this.minor > that.minor;
        }

        public boolean isEqual(Version that) {
            return this.major == that.major && this.minor == that.minor;
        }
    }
}
