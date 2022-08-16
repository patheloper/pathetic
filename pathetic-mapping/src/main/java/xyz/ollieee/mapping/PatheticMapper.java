package xyz.ollieee.mapping;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ollieee.Pathetic;
import xyz.ollieee.api.pathing.Pathfinder;
import xyz.ollieee.api.snapshot.MaterialParser;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.model.pathing.PathfinderImpl;

@UtilityClass
public class PatheticMapper {

    public SnapshotManager getSnapshotManager() {
        ensureInitialized();
        return Pathetic.getSnapshotManager();
    }

    public MaterialParser getMaterialParser() {
        ensureInitialized();
        return Pathetic.getMaterialParser();
    }

    /**
     * Instantiates a new pathfinder object.
     *
     * @return The {@link Pathfinder} object
     * @throws IllegalStateException If the lib is not initialized yet
     */
    @SneakyThrows
    public @NonNull Pathfinder newPathfinder() {
        ensureInitialized();
        return new PathfinderImpl();
    }

    private void ensureInitialized() {
        if (Pathetic.isInitialized()) return;

        try {
            Pathetic.initialize(
                    JavaPlugin.getProvidingPlugin(
                            Class.forName(new Exception().getStackTrace()[2].getClassName())
                    )
            );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
