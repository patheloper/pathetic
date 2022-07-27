package xyz.ollieee.mapping;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ollieee.Pathetic;
import xyz.ollieee.api.pathing.Pathfinder;
import xyz.ollieee.api.snapshot.MaterialParser;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.model.pathing.PathfinderImpl;

public final class PatheticMapper {

    /**
     * Initializes the Lib. If the lib is not initialized yet but is used anyways, this will lead to many boooooms.
     *
     * @throws IllegalStateException If an attempt is made to initialize more than 1 time
     * @param javaPlugin the JavaPlugin which initializes the lib
     */
    public static void initialize(JavaPlugin javaPlugin) {
        Pathetic.initialize(javaPlugin);
    }

    public static @NonNull SnapshotManager getSnapshotManager() {
        return Pathetic.getSnapshotManager();
    }

    public static @NonNull MaterialParser getMaterialParser() {
        return Pathetic.getMaterialParser();
    }

    public static @NonNull Pathfinder newPathfinder() {
        return new PathfinderImpl();
    }

    private PatheticMapper() {
    }

}
