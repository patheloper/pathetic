package org.patheloper.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

/**
 * Utility class to parse and handle Minecraft server versioning in Bukkit. This class provides
 * functionality to retrieve the server version and compare versions for compatibility purposes.
 */
@UtilityClass
public class BukkitVersionUtil {

  private static final double CURRENT_MAJOR;
  private static final double CURRENT_MINOR;

  static {
    String versionString = extractMinecraftVersion();
    int[] versionNumbers = parseVersionString(versionString);
    CURRENT_MAJOR = versionNumbers[1];
    CURRENT_MINOR = versionNumbers[2];
  }

  /**
   * Retrieves the current Minecraft version running on the server.
   *
   * @return a {@link Version} object representing the current server version
   */
  public static Version getVersion() {
    return new Version(CURRENT_MAJOR, CURRENT_MINOR);
  }

  /**
   * Extracts the Minecraft version string from the server version output.
   *
   * @return the extracted Minecraft version string
   */
  private String extractMinecraftVersion() {
    Pattern pattern = Pattern.compile(".*\\(.*MC.\\s*([a-zA-z0-9\\-.]+)\\s*\\)");
    Matcher matcher = pattern.matcher(Bukkit.getVersion());

    validateMatcher(matcher);
    return matcher.group(1);
  }

  /**
   * Parses the extracted version string into an array of integers representing major and minor
   * versions.
   *
   * @param versionString the extracted version string
   * @return an array containing the major and minor version numbers
   */
  private int[] parseVersionString(String versionString) {
    String[] elements = versionString.split("\\.");
    validateVersionElements(elements, versionString);

    int[] values = new int[3]; // Placeholder for major, minor, and patch if available
    for (int i = 0; i < Math.min(values.length, elements.length); i++) {
      values[i] = Integer.parseInt(elements[i].trim());
    }

    return values;
  }

  /**
   * Validates that the version matcher is valid and has successfully matched the version string.
   *
   * @param matcher the version matcher to validate
   */
  private void validateMatcher(Matcher matcher) {
    if (!matcher.matches() || matcher.group(1) == null) {
      throw new IllegalStateException("Cannot parse version String '" + Bukkit.getVersion() + "'");
    }
  }

  /**
   * Validates that the version elements array is properly structured.
   *
   * @param elements the array of version elements
   * @param versionString the version string used for validation in case of an error
   */
  private void validateVersionElements(String[] elements, String versionString) {
    if (elements.length < 1) {
      throw new IllegalStateException("Invalid server version '" + versionString + "'");
    }
  }

  /**
   * A class representing a specific Minecraft version, including major and minor version numbers.
   */
  @Getter
  public static class Version {

    private final double major;
    private final double minor;

    /**
     * Factory method to create a new {@link Version} object with specified major and minor
     * versions.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @return a new {@link Version} instance
     */
    public static Version of(double major, double minor) {
      return new Version(major, minor);
    }

    /**
     * Constructs a new {@link Version} object.
     *
     * @param major the major version number
     * @param minor the minor version number
     */
    public Version(double major, double minor) {
      this.major = major;
      this.minor = minor;
    }

    /**
     * Checks if the current version is under the specified major and minor version.
     *
     * @param major the major version to compare against
     * @param minor the minor version to compare against
     * @return true if the current version is under the specified version, false otherwise
     */
    public boolean isUnder(double major, double minor) {
      if (this.major < major) return true;
      return this.major == major && this.minor < minor;
    }

    /**
     * Checks if the current version is under the specified {@link Version}.
     *
     * @param that the version to compare against
     * @return true if the current version is under the specified version, false otherwise
     */
    public boolean isUnder(Version that) {
      return this.isUnder(that.major, that.minor);
    }

    /**
     * Checks if the current version is over the specified {@link Version}.
     *
     * @param that the version to compare against
     * @return true if the current version is over the specified version, false otherwise
     */
    public boolean isOver(Version that) {
      if (this.major > that.major) return true;
      return this.major == that.major && this.minor > that.minor;
    }

    /**
     * Checks if the current version is equal to the specified {@link Version}.
     *
     * @param that the version to compare against
     * @return true if the current version is equal to the specified version, false otherwise
     */
    public boolean isEqual(Version that) {
      return this.major == that.major && this.minor == that.minor;
    }
  }
}
