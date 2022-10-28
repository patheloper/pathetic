package xyz.ollieee.mapping;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ollieee.Pathetic;
import xyz.ollieee.api.pathing.Pathfinder;
import xyz.ollieee.api.pathing.rules.PathingRuleSet;
import xyz.ollieee.api.snapshot.MaterialParser;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.model.pathing.PathfinderImpl;

@UtilityClass
public class PatheticMapper {

    /**
     * initializes the Lib. If the lib is not initialized yet but is used anyways, this will cause many things to break.
     *
     * @param javaPlugin the JavaPlugin which initializes the lib
     * @throws IllegalStateException If an attempt is made to initialize more than 1 time
     */
    @SneakyThrows
    public void initialize(JavaPlugin javaPlugin) {
        Pathetic.initialize(javaPlugin);
    }

    public SnapshotManager getSnapshotManager() {
        return Pathetic.getSnapshotManager();
    }

    public MaterialParser getMaterialParser() {
        return Pathetic.getMaterialParser();
    }

    /**
     * Instantiates a new pathfinder object.
     *
     * @return The {@link Pathfinder} object
     * @throws IllegalStateException If the lib is not initialized yet
     */
    @SneakyThrows
    public @NonNull Pathfinder newPathfinder(PathingRuleSet pathingRuleSet) {

        if (Pathetic.isInitialized())
            return new PathfinderImpl(pathingRuleSet);

        throw new IllegalStateException("Pathetic is not initialized");
    }

}
