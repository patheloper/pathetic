package org.patheloper.mapping;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;
import org.patheloper.Pathetic;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.configuration.PathfinderConfiguration;
import org.patheloper.model.pathing.pathfinder.AStarPathfinder;
import org.patheloper.util.ErrorLogger;

/** PatheticMapper is a utility class that maps the Pathetic API to the Pathetic Implementation. */
@UtilityClass
public class PatheticMapper {

  /**
   * @apiNote If Pathetic is not initialized yet but is used anyways, this will cause many things to
   *     break.
   * @param javaPlugin the JavaPlugin which initializes the lib
   * @throws IllegalStateException If an attempt is made to initialize more than once
   */
  public void initialize(JavaPlugin javaPlugin) {
    Pathetic.initialize(javaPlugin);
  }

  /**
   * Signals Pathetic to initiate its shutdown process, releasing resources and finalizing
   * operations. This method should be called when Pathetic is no longer needed or the plugin is
   * being disabled.
   */
  public void shutdown() {
    Pathetic.shutdown();
  }

  /**
   * Instantiates a new pathfinder object.
   *
   * @return The {@link Pathfinder} object
   * @throws IllegalStateException If the lib is not initialized yet
   */
  public @NonNull Pathfinder newPathfinder() {
    return newPathfinder(PathfinderConfiguration.createAsyncConfiguration());
  }

  /**
   * Instantiates a new A*-pathfinder.
   *
   * @param pathfinderConfiguration - The {@link PathfinderConfiguration}
   * @return The {@link Pathfinder}
   * @throws IllegalStateException If the lib is not initialized yet
   */
  public @NonNull Pathfinder newPathfinder(PathfinderConfiguration pathfinderConfiguration) {
    if (Pathetic.isInitialized()) return new AStarPathfinder(pathfinderConfiguration);

    throw ErrorLogger.logFatalError("Pathetic is not initialized yet.");
  }
}
