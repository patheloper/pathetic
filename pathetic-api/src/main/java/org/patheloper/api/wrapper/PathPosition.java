package org.patheloper.api.wrapper;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.patheloper.api.util.NumberUtils;

@AllArgsConstructor
@Getter
@ToString
public class PathPosition implements Cloneable {

  @NonNull private PathEnvironment pathEnvironment;

  private double x;
  private double y;
  private double z;

  /**
   * Interpolates between two positions based on the given progress.
   *
   * @param other The other position to interpolate with
   * @param progress The interpolation progress (0.0 to 1.0)
   * @return The interpolated position
   */
  public PathPosition interpolate(PathPosition other, double progress) {
    double x = NumberUtils.interpolate(this.x, other.x, progress);
    double y = NumberUtils.interpolate(this.y, other.y, progress);
    double z = NumberUtils.interpolate(this.z, other.z, progress);
    return new PathPosition(pathEnvironment, x, y, z);
  }

  /**
   * Checks to see if the two positions are in the same block
   *
   * @param otherPosition The other position to check against
   * @return True if the positions are in the same block
   */
  public boolean isInSameBlock(PathPosition otherPosition) {
    return this.getBlockX() == otherPosition.getBlockX()
        && this.getBlockY() == otherPosition.getBlockY()
        && this.getBlockZ() == otherPosition.getBlockZ();
  }

  /**
   * Gets the manhattan distance between the current and another position
   *
   * @param otherPosition the other {@link PathPosition} to get the distance to
   * @return the distance
   */
  public int manhattanDistance(PathPosition otherPosition) {
    return Math.abs(this.getBlockX() - otherPosition.getBlockX())
        + Math.abs(this.getBlockY() - otherPosition.getBlockY())
        + Math.abs(this.getBlockZ() - otherPosition.getBlockZ());
  }

  /**
   * Gets the octile distance between the current and another position
   *
   * @param otherPosition the other {@link PathPosition} to get the distance to
   * @return the distance
   */
  public double octileDistance(PathPosition otherPosition) {

    double dx = Math.abs(this.x - otherPosition.x);
    double dy = Math.abs(this.y - otherPosition.y);
    double dz = Math.abs(this.z - otherPosition.z);

    double smallest = Math.min(Math.min(dx, dz), dy);
    double highest = Math.max(Math.max(dx, dz), dy);
    double mid = Math.max(Math.min(dx, dz), Math.min(Math.max(dx, dz), dy));

    double D1 = 1;
    double D2 = 1.4142135623730951;
    double D3 = 1.7320508075688772;

    return (D3 - D2) * smallest + (D2 - D1) * mid + D1 * highest;
  }

  /**
   * Gets the distance squared between the current and another position
   *
   * @return The distance squared
   */
  public double distanceSquared(PathPosition otherPosition) {
    return NumberUtils.square(this.x - otherPosition.x)
        + NumberUtils.square(this.y - otherPosition.y)
        + NumberUtils.square(this.z - otherPosition.z);
  }

  /**
   * Gets the distance between the current and another position
   *
   * @return The distance
   */
  public double distance(PathPosition otherPosition) {
    return NumberUtils.sqrt(this.distanceSquared(otherPosition));
  }

  /**
   * Sets the X coordinate of the {@link PathPosition}
   *
   * @param x The new X coordinate
   * @return A new {@link PathPosition}
   */
  public PathPosition setX(double x) {
    return new PathPosition(this.pathEnvironment, x, this.y, this.z);
  }

  /**
   * Sets the Y coordinate of the {@link PathPosition}
   *
   * @param y The new Y coordinate
   * @return A new {@link PathPosition}
   */
  public PathPosition setY(double y) {
    return new PathPosition(this.pathEnvironment, this.x, y, this.z);
  }

  /**
   * Sets the Z coordinate of the {@link PathPosition}
   *
   * @param z The new Z coordinate
   * @return A new {@link PathPosition}
   */
  public PathPosition setZ(double z) {
    return new PathPosition(this.pathEnvironment, this.x, this.y, z);
  }

  /**
   * Gets the X coordinate of the block the position is in
   *
   * @return The X coordinate of the block
   */
  public int getBlockX() {
    return (int) Math.floor(this.x);
  }

  /**
   * Gets the Y coordinate of the block the position is in
   *
   * @return The Y coordinate of the block
   */
  public int getBlockY() {
    return (int) Math.floor(this.y);
  }

  /**
   * Gets the Z coordinate of the block the position is in
   *
   * @return The Z coordinate of the block
   */
  public int getBlockZ() {
    return (int) Math.floor(this.z);
  }

  /**
   * Adds x,y,z values to the current values
   *
   * @param x The value to add to the x
   * @param y The value to add to the y
   * @param z The value to add to the z
   * @return A new {@link PathPosition}
   */
  @NonNull
  public PathPosition add(final double x, final double y, final double z) {
    return new PathPosition(this.pathEnvironment, this.x + x, this.y + y, this.z + z);
  }

  /**
   * Adds the values of a vector to the position
   *
   * @param vector The {@link PathVector} who's values will be added
   * @return A new {@link PathPosition}
   */
  @NonNull
  public PathPosition add(final PathVector vector) {
    return add(vector.getX(), vector.getY(), vector.getZ());
  }

  /**
   * Subtracts x,y,z values from the current values
   *
   * @param x The value to subtract from the x
   * @param y The value to subtract from the y
   * @param z The value to subtract from the z
   * @return A new {@link PathPosition}
   */
  @NonNull
  public PathPosition subtract(final double x, final double y, final double z) {
    return new PathPosition(this.pathEnvironment, this.x - x, this.y - y, this.z - z);
  }

  /**
   * Subtracts the values of a vector from the position
   *
   * @param vector The {@link PathVector} who's values will be subtracted
   * @return A new {@link PathPosition}
   */
  @NonNull
  public PathPosition subtract(final PathVector vector) {
    return subtract(vector.getX(), vector.getY(), vector.getZ());
  }

  /**
   * Converts the positions x,y,z to a {@link PathVector}
   *
   * @return A {@link PathVector} of the x,y,z
   */
  @NonNull
  public PathVector toVector() {
    return new PathVector(this.x, this.y, this.z);
  }

  /**
   * Rounds the x,y,z values to the floor of the values
   *
   * @return A new {@link PathPosition}
   */
  public PathPosition floor() {
    return new PathPosition(
        this.pathEnvironment, this.getBlockX(), this.getBlockY(), this.getBlockZ());
  }

  /**
   * Sets the coordinates to the middle of the block
   *
   * @return A new {@link PathPosition}
   */
  public PathPosition mid() {
    return new PathPosition(
        this.pathEnvironment,
        this.getBlockX() + 0.5,
        this.getBlockY() + 0.5,
        this.getBlockZ() + 0.5);
  }

  public PathPosition midPoint(PathPosition end) {
    return new PathPosition(
        this.pathEnvironment, (this.x + end.x) / 2, (this.y + end.y) / 2, (this.z + end.z) / 2);
  }

  @Override
  public PathPosition clone() {

    final PathPosition clone;
    try {
      clone = (PathPosition) super.clone();
    } catch (CloneNotSupportedException ex) {
      throw new IllegalStateException("Superclass messed up", ex);
    }

    clone.pathEnvironment = this.pathEnvironment;
    clone.x = this.x;
    clone.y = this.y;
    clone.z = this.z;
    return clone;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PathPosition that = (PathPosition) o;
    return x == that.x
        && y == that.y
        && z == that.z
        && Objects.equals(pathEnvironment, that.pathEnvironment);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pathEnvironment, x, y, z);
  }
}
