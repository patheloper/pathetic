package org.patheloper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.patheloper.bukkit.listeners.ChunkInvalidateListener;
import org.patheloper.util.BukkitVersionUtil;
import org.patheloper.util.ErrorLogger;

@UtilityClass
@Slf4j
public class Pathetic {

  private static final String PROPERTIES_FILE = "pathetic.properties";

  private static final Set<Runnable> SHUTDOWN_LISTENERS = new HashSet<>();

  private static JavaPlugin instance;
  @Getter private static String modelVersion;

  /**
   * @throws IllegalStateException If an attempt is made to initialize more than 1 time
   */
  public static void initialize(JavaPlugin javaPlugin) {

    if (instance != null) throw ErrorLogger.logFatalError("Can't be initialized twice");

    instance = javaPlugin;
    Bukkit.getPluginManager().registerEvents(new ChunkInvalidateListener(), javaPlugin);

    loadModelVersion();

    if (BukkitVersionUtil.getVersion().isUnder(16, 0)
        || BukkitVersionUtil.getVersion().isEqual(BukkitVersionUtil.Version.of(16, 0))) {
      log.warn(
          "Pathetic is currently running in a version older than or equal to 1.16. "
              + "Some functionalities might not be accessible, such as accessing the BlockState of blocks.");
    }

    log.debug("Pathetic v{} initialized", modelVersion);
  }

  public static void shutdown() {
    SHUTDOWN_LISTENERS.forEach(Runnable::run);
    SHUTDOWN_LISTENERS.clear();

    instance = null;
    log.debug("Pathetic shutdown");
  }

  public static boolean isInitialized() {
    return instance != null;
  }

  public static JavaPlugin getPluginInstance() {
    return instance;
  }

  public static void addShutdownListener(Runnable listener) {
    SHUTDOWN_LISTENERS.add(listener);
  }

  private static void loadModelVersion() {
    try (InputStream inputStream =
        Pathetic.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
      Properties properties = new Properties();
      properties.load(inputStream);

      modelVersion = properties.getProperty("model.version");
    } catch (IOException e) {
      throw ErrorLogger.logFatalError("Error loading model version", e);
    }
  }
}
