package org.patheloper.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;

/**
 * Utility class for handling chunk-related operations in Bukkit
 */
@UtilityClass
public class ChunkUtils {

  /** Reflection method for fetching materials in versions under 1.13. */
  private static Method materialMethod;

  /** Reflection method for fetching block type IDs in versions under 1.13. */
  private static Method blockTypeMethod;

  static {
    initializeReflectionMethods();
  }

  /**
   * Initializes reflection methods for Minecraft versions below 1.13. In these versions, blocks are
   * represented by numeric block IDs.
   */
  private void initializeReflectionMethods() {
    if (BukkitVersionUtil.getVersion().isUnder(13, 0)) {
      try {
        materialMethod = Material.class.getDeclaredMethod("getMaterial", int.class);
        blockTypeMethod =
            ChunkSnapshot.class.getDeclaredMethod(
                "getBlockTypeId", int.class, int.class, int.class);
      } catch (NoSuchMethodException e) {
        throw ErrorLogger.logFatalError(
            "Failed to initialize reflection methods: " + e.getMessage(), e);
      }
    }
  }

  /**
   * Generates a chunk key based on the given chunk x and z coordinates.
   *
   * @param x the chunk's x coordinate
   * @param z the chunk's z coordinate
   * @return a long value representing the chunk key
   */
  public long getChunkKey(final int x, final int z) {
    return x & 0xFFFFFFFFL | (z & 0xFFFFFFFFL) << 32;
  }

  /**
   * Retrieves the block type (Material) from a chunk snapshot at the given coordinates.
   *
   * @param snapshot the {@link ChunkSnapshot} containing the chunk data
   * @param x the x-coordinate within the chunk
   * @param y the y-coordinate (height) within the chunk
   * @param z the z-coordinate within the chunk
   * @return the {@link Material} type of the block
   * @throws IllegalStateException if reflection methods fail for legacy versions
   */
  @SneakyThrows
  public Material getMaterial(ChunkSnapshot snapshot, int x, int y, int z) {
    if (BukkitVersionUtil.getVersion().isUnder(13, 0)) {
      return getMaterialForLegacyVersion(snapshot, x, y, z);
    }
    return snapshot.getBlockType(x, y, z); // For versions 1.13 and above
  }

  /**
   * Retrieves the legacy block type for Minecraft versions under 1.13.
   *
   * @param snapshot the {@link ChunkSnapshot} containing the chunk data
   * @param x the x-coordinate within the chunk
   * @param y the y-coordinate (height) within the chunk
   * @param z the z-coordinate within the chunk
   * @return the {@link Material} corresponding to the block's ID
   * @throws IllegalStateException if reflection methods are not initialized or invocation fails
   */
  private Material getMaterialForLegacyVersion(ChunkSnapshot snapshot, int x, int y, int z)
      throws InvocationTargetException, IllegalAccessException {
    ensureReflectionMethodsInitialized();
    return getLegacyMaterial(snapshot, x, y, z);
  }

  /**
   * Invokes reflection to retrieve the {@link Material} for a block in a chunk snapshot using block
   * IDs for Minecraft versions below 1.13.
   *
   * @param snapshot the {@link ChunkSnapshot} containing the chunk data
   * @param x the x-coordinate within the chunk
   * @param y the y-coordinate (height) within the chunk
   * @param z the z-coordinate within the chunk
   * @return the {@link Material} corresponding to the block's ID
   * @throws IllegalAccessException if access to the method is illegal
   * @throws InvocationTargetException if the underlying method throws an exception
   */
  private Material getLegacyMaterial(ChunkSnapshot snapshot, int x, int y, int z)
      throws IllegalAccessException, InvocationTargetException {
    Object blockTypeId = blockTypeMethod.invoke(snapshot, x, y, z);
    return (Material) materialMethod.invoke(null, blockTypeId);
  }

  /**
   * Ensures that the reflection methods for fetching materials and block types are properly
   * initialized. Throws a fatal error if they are not initialized.
   *
   * @throws IllegalStateException if the reflection methods are not initialized
   */
  private void ensureReflectionMethodsInitialized() {
    if (materialMethod == null || blockTypeMethod == null) {
      throw ErrorLogger.logFatalError("Reflection methods not initialized");
    }
  }
}
