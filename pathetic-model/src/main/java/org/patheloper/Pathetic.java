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
  private static final Set<Runnable> SHUTDOWN_HOOKS = new HashSet<>();

  @Getter private static JavaPlugin pluginInstance;

  @Getter private static String modelVersion;

  /**
   * Initializes the plugin. This method must be called only once.
   *
   * @param javaPlugin The JavaPlugin instance of this plugin.
   * @throws IllegalStateException If an attempt is made to initialize more than once.
   */
  public static void initialize(JavaPlugin javaPlugin) {
    ensureNotAlreadyInitialized();
    initializePluginComponents(javaPlugin);
    loadModelVersionFromProperties();
    checkForUnsupportedMinecraftVersion();
    log.debug("Pathetic v{} successfully initialized", modelVersion);
  }

  /**
   * Initializes the core components of the plugin, including registering events and setting up
   * other initial behaviors.
   *
   * @param javaPlugin The JavaPlugin instance of this plugin.
   */
  private static void initializePluginComponents(JavaPlugin javaPlugin) {
    pluginInstance = javaPlugin;
    registerEventListeners(javaPlugin);
    BStatsHandler.init(javaPlugin);
  }

  /**
   * Ensures the plugin has not already been initialized. Throws an exception if it has.
   *
   * @throws IllegalStateException If the plugin is already initialized.
   */
  private static void ensureNotAlreadyInitialized() {
    if (isInitialized()) {
      throw ErrorLogger.logFatalError("Pathetic plugin cannot be initialized multiple times.");
    }
  }

  /** Safely shuts down the plugin, clears shutdown listeners, and logs the shutdown. */
  public static void shutdown() {
    SHUTDOWN_HOOKS.forEach(Runnable::run);
    SHUTDOWN_HOOKS.clear();
    pluginInstance = null;

    log.debug("Pathetic plugin has been shut down.");
  }

  /**
   * Checks if the plugin has already been initialized.
   *
   * @return true if initialized, otherwise false.
   */
  public static boolean isInitialized() {
    return pluginInstance != null;
  }

  /**
   * Registers a shutdown hook to be executed when the plugin shuts down.
   *
   * @param listener A Runnable that will be executed during shutdown.
   */
  public static void addShutdownListener(Runnable listener) {
    SHUTDOWN_HOOKS.add(listener);
  }

  /** Loads the model version from the properties file. */
  private static void loadModelVersionFromProperties() {
    try (InputStream inputStream =
        Pathetic.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
      validatePropertiesFileExists(inputStream);
      setModelVersionFromProperties(inputStream);
    } catch (IOException e) {
      throw ErrorLogger.logFatalError("Failed to load model version from properties file.", e);
    }
  }

  /**
   * Validates that the properties file input stream is not null, throwing an exception if it is not
   * found.
   *
   * @param inputStream The InputStream for the properties file.
   * @throws IOException If the properties file is not found.
   */
  private static void validatePropertiesFileExists(InputStream inputStream) throws IOException {
    if (inputStream == null) {
      throw new IOException("Properties file not found: " + PROPERTIES_FILE);
    }
  }

  /**
   * Loads and sets the model version from the properties file input stream.
   *
   * @param inputStream The InputStream for the properties file.
   * @throws IOException If an error occurs while loading the properties file.
   */
  private static void setModelVersionFromProperties(InputStream inputStream) throws IOException {
    Properties properties = new Properties();
    properties.load(inputStream);
    modelVersion = properties.getProperty("model.version", "unknown");
  }

  /**
   * Registers all necessary event listeners for the plugin.
   *
   * @param javaPlugin The JavaPlugin instance of this plugin.
   */
  private static void registerEventListeners(JavaPlugin javaPlugin) {
    Bukkit.getPluginManager().registerEvents(new ChunkInvalidateListener(), javaPlugin);
  }

  /**
   * Checks the Minecraft version the server is running on and logs a warning if the version is
   * outdated or incompatible with certain features of the plugin.
   */
  private static void checkForUnsupportedMinecraftVersion() {
    BukkitVersionUtil.Version currentVersion = BukkitVersionUtil.getVersion();

    if (currentVersion.isUnder(16, 0)
        || currentVersion.isEqual(BukkitVersionUtil.Version.of(16, 0))) {
      log.warn(
          "Pathetic is running on Minecraft version {}. "
              + "Some functionalities may be limited, such as accessing BlockState.",
          currentVersion);
    }
  }
}
