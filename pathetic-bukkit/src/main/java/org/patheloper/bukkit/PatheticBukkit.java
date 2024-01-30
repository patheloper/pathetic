package org.patheloper.bukkit;

import java.util.Arrays;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.patheloper.Pathetic;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.api.wrapper.BlockInformation;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathEnvironment;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.api.wrapper.PathVector;
import org.patheloper.model.pathing.pathfinder.AStarPathfinder;
import org.patheloper.util.ErrorLogger;

public class PatheticBukkit {

  private static final PatheticBukkit INSTANCE = new PatheticBukkit();

  public static PatheticBukkit getInstance() {
    return INSTANCE;
  }

  protected PatheticBukkit() {}

  /**
   * Initializes the Lib. If the lib is not initialized yet but is used anyways, this will cause
   * many things to break.
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
   * @return The {@link Pathfinder} object
   * @throws IllegalStateException If the lib is not initialized yet
   */
  public @NonNull Pathfinder newPathfinder() {
    return newPathfinder(PathingRuleSet.createAsyncRuleSet());
  }

  /**
   * Instantiates a new A*-pathfinder.
   *
   * @param pathingRuleSet - The {@link PathingRuleSet}
   * @return The {@link Pathfinder}
   * @throws IllegalStateException If the lib is not initialized yet
   */
  public @NonNull Pathfinder newPathfinder(PathingRuleSet pathingRuleSet) {
    if (Pathetic.isInitialized())
      return new AStarPathfinder(
          pathingRuleSet,
          pathingRuleSet.isLoadingChunks()
              ? new BukkitFailingTerrainProvider.BukkitRequestingTerrainProvider()
              : new BukkitFailingTerrainProvider());

    throw ErrorLogger.logFatalError("Pathetic is not initialized");
  }

  private static final boolean IS_NEWER_WORLD;

  static {
    IS_NEWER_WORLD =
        Arrays.stream(World.class.getMethods())
            .anyMatch(method -> "getMinHeight".equalsIgnoreCase(method.getName()));
  }

  @NonNull
  public Location toLocation(@NonNull PathPosition pathPosition) {
    return new Location(
        toWorld(pathPosition.getPathEnvironment()),
        pathPosition.getX(),
        pathPosition.getY(),
        pathPosition.getZ());
  }

  @NonNull
  public PathPosition toPathPosition(@NonNull Location location) {

    if (location.getWorld() == null) throw ErrorLogger.logFatalError("World is null");

    return new PathPosition(
        toPathWorld(location.getWorld()),
        location.getBlockX(),
        location.getBlockY(),
        location.getBlockZ());
  }

  @NonNull
  public Vector toVector(PathVector pathVector) {
    return new Vector(pathVector.getX(), pathVector.getY(), pathVector.getZ());
  }

  @NonNull
  public PathVector toPathVector(Vector vector) {
    return new PathVector(vector.getX(), vector.getY(), vector.getZ());
  }

  @NonNull
  public Block toBlock(@NonNull PathBlock pathBlock) {
    return toLocation(pathBlock.getPathPosition()).getBlock();
  }

  @NonNull
  public PathBlock toPathBlock(@NonNull Block block) {
    return new PathBlock(
        new PathPosition(toPathWorld(block.getWorld()), block.getX(), block.getY(), block.getZ()),
        new BlockInformation(block.getType(), block.getState()));
  }

  public World toWorld(@NonNull PathEnvironment pathEnvironment) {
    return Bukkit.getWorld(pathEnvironment.getUuid());
  }

  @NonNull
  public PathEnvironment toPathWorld(@NonNull World world) {
    return new PathEnvironment(
        world.getUID(), world.getName(), getMinHeight(world), getMaxHeight(world));
  }

  private int getMinHeight(World world) {
    return IS_NEWER_WORLD ? world.getMinHeight() : 0;
  }

  private int getMaxHeight(World world) {
    return IS_NEWER_WORLD ? world.getMaxHeight() : 256;
  }
}
