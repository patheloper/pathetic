package xyz.ollieee.mapping;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ollieee.Pathetic;
import xyz.ollieee.api.pathing.Pathfinder;
import xyz.ollieee.api.pathing.rules.PathingRuleSet;
import xyz.ollieee.model.pathing.PathfinderImpl;

/**
 * PatheticMapper is a utility class that maps the Pathetic API to the Pathetic Implementation.
 */
@UtilityClass
public class PatheticMapper {

    /**
     * Initializes the Lib.
     * If the lib is not initialized yet but is used anyways, this will cause many things to break.
     *
     * @param javaPlugin the JavaPlugin which initializes the lib
     * @throws IllegalStateException If an attempt is made to initialize more than 1 time
     */
    public void initialize(JavaPlugin javaPlugin) {
        Pathetic.initialize(javaPlugin);
    }

    /**
     * Instantiates a new pathfinder object.
     *
     * @param pathingRuleSet - The {@link PathingRuleSet}
     * @return The {@link Pathfinder} object
     * @throws IllegalStateException If the lib is not initialized yet
     */
    public @NonNull Pathfinder newPathfinder(PathingRuleSet pathingRuleSet) {

        if (Pathetic.isInitialized())
            return new PathfinderImpl(pathingRuleSet);

        throw new IllegalStateException("Pathetic is not initialized");
    }

    /**
     * Instantiates a new pathfinder object.
     *
     * @return The {@link Pathfinder} object
     * @throws IllegalStateException If the lib is not initialized yet
     */
    public @NonNull Pathfinder newPathfinder() {
        return newPathfinder(PathingRuleSet.createAsyncRuleSet());
    }

}
