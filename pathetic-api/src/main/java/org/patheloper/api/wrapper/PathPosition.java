package org.patheloper.api.wrapper;

import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.patheloper.api.util.NumberUtils;

@Value
public class PathPosition {

  /** The cost of moving straight in a grid. */
  private static final double STRAIGHT_MOVEMENT_COST = 1.0;

  /** The cost of moving diagonally in a grid. */
  private static final double DIAGONAL_MOVEMENT_COST = Math.sqrt(2);

  /** The cost of moving in a tri-diagonal direction in a grid. */
  private static final double TRI_DIAGONAL_MOVEMENT_COST = Math.sqrt(3);

  /** The environment in which the position exists */
  @NonNull PathEnvironment pathEnvironment;

  /** The X coordinate of the position. -- GETTER -- Returns the X coordinate of the position. */
  double x;

  /** The Y coordinate of the position. -- GETTER -- Returns the Y coordinate of the position. */
  double y;

  /** The Z coordinate of the position. -- GETTER -- Returns the Z coordinate of the position. */
  double z;

  /**
   * Interpolates between the current position and another position based on a given progress
   * factor.
   *
   * <p>This method performs linear interpolation (lerp) on each of the X, Y, and Z coordinates of
   * the current position and the provided {@code other} position. The interpolation factor {@code
   * progress} should be between 0 and 1, where:
   *
   * <ul>
   *   <li>{@code 0.0} results in the current position
   *   <li>{@code 1.0} results in the {@code other} position
   *   <li>Any value in between results in a position proportionally between the two
   * </ul>
   *
   * @param other the {@link PathPosition} to interpolate towards
   * @param progress the interpolation factor (range: 0.0 to 1.0)
   * @return a new {@link PathPosition} representing the interpolated position
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
   * Calculates the octile distance between this position and another.
   *
   * <p>Explanation: We’re in a 3D grid, and movement isn’t just straight lines. You can move along
   * one axis, two axes at once (diagonal), or even all three axes (tri-diagonal). This method takes
   * the differences between the current and target positions on the X, Y, and Z axes, then
   * calculates the distance based on the type of movement involved.
   *
   * <p>Movement types and their costs: - Straight (D1): Moving along one axis. Cost = 1.0 -
   * Diagonal (D2): Moving along two axes at once. Cost = √2 (1.414...) - Tri-Diagonal (D3): Moving
   * along all three axes at once. Cost = √3 (1.732...)
   *
   * <p>The method calculates three key values: - Smallest: The smallest difference, which we assume
   * is for tri-diagonal movement (D3). - Mid: The middle difference, typically for diagonal
   * movement (D2). - Highest: The largest difference, for straight movement (D1).
   *
   * <p>Finally, we throw these into a formula that blends them with the movement costs. The result
   * is a distance estimate that takes the multi-axis movement into account.
   *
   * @param pathPosition the other position we’re calculating the distance to
   * @return the octile distance based on straight, diagonal, and tri-diagonal movement
   */
  public double octileDistance(PathPosition pathPosition) {
    TripletDouble diff = calculateAxisDifferences(pathPosition);

    double smallest = findSmallest(diff);
    double highest = findHighest(diff);
    double mid = findMid(diff);

    return calculateOctileDistance(smallest, mid, highest);
  }

  /**
   * Calculates the absolute differences between the current position and the other position.
   *
   * @param pathPosition the other position
   * @return a {@link TripletDouble} representing the absolute differences on the X, Y, and Z axes
   */
  private TripletDouble calculateAxisDifferences(PathPosition pathPosition) {
    return new TripletDouble(
        Math.abs(this.x - pathPosition.x),
        Math.abs(this.y - pathPosition.y),
        Math.abs(this.z - pathPosition.z));
  }

  /**
   * Finds the smallest axis difference from the given {@link TripletDouble}.
   *
   * @param diff the triplet containing axis differences
   * @return the smallest difference
   */
  private double findSmallest(TripletDouble diff) {
    return Math.min(Math.min(diff.getX(), diff.getZ()), diff.getY());
  }

  /**
   * Finds the highest axis difference from the given {@link TripletDouble}.
   *
   * @param diff the triplet containing axis differences
   * @return the highest difference
   */
  private double findHighest(TripletDouble diff) {
    return Math.max(Math.max(diff.getX(), diff.getZ()), diff.getY());
  }

  /**
   * Finds the mid-axis difference from the given {@link TripletDouble}.
   *
   * @param diff the triplet containing axis differences
   * @return the mid-range axis difference
   */
  private double findMid(TripletDouble diff) {
    return diff.getX() + diff.getY() + diff.getZ() - findSmallest(diff) - findHighest(diff);
  }

  /**
   * Calculates the octile distance based on the smallest, mid, and highest axis differences. The
   * octile distance is a heuristic used in pathfinding to estimate the distance between two points
   * in a grid where diagonal and straight movements have different costs.
   *
   * @param smallest the smallest of the axis differences (x, y, or z)
   * @param mid the mid-range axis difference (x, y, or z)
   * @param highest the largest of the axis differences (x, y, or z)
   * @return the calculated octile distance
   */
  private static double calculateOctileDistance(double smallest, double mid, double highest) {
    return (TRI_DIAGONAL_MOVEMENT_COST - DIAGONAL_MOVEMENT_COST) * smallest
        + (DIAGONAL_MOVEMENT_COST - STRAIGHT_MOVEMENT_COST) * mid
        + STRAIGHT_MOVEMENT_COST * highest;
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
   * Creates a new PathPosition with the modified X coordinate.
   *
   * @param x the new X coordinate
   * @return a new {@link PathPosition} with the updated X value
   */
  public PathPosition withX(double x) {
    return new PathPosition(this.pathEnvironment, x, this.y, this.z);
  }

  /**
   * Creates a new PathPosition with the modified Y coordinate.
   *
   * @param y the new Y coordinate
   * @return a new {@link PathPosition} with the updated Y value
   */
  public PathPosition withY(double y) {
    return new PathPosition(this.pathEnvironment, this.x, y, this.z);
  }

  /**
   * Creates a new PathPosition with the modified Z coordinate.
   *
   * @param z the new Z coordinate
   * @return a new {@link PathPosition} with the updated Z value
   */
  public PathPosition withZ(double z) {
    return new PathPosition(this.pathEnvironment, this.x, this.y, z);
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

  /**
   * Gets the middle point between the current position and another position
   *
   * @param end The other position
   * @return The middle point
   */
  public PathPosition midPoint(PathPosition end) {
    return new PathPosition(
        this.pathEnvironment, (this.x + end.x) / 2, (this.y + end.y) / 2, (this.z + end.z) / 2);
  }

  /**
   * A class representing a triplet of double values. This can be used to store three related double
   * values, such as coordinates or vector components.
   */
  @Getter
  private static class TripletDouble {
    /**
     * The x component of the triplet. -- GETTER -- Returns the value of the x component.
     *
     * @return the x component value
     */
    private final double x;

    /**
     * The y component of the triplet. -- GETTER -- Returns the value of the y component.
     *
     * @return the y component value
     */
    private final double y;

    /**
     * The z component of the triplet. -- GETTER -- Returns the value of the z component.
     *
     * @return the z component value
     */
    private final double z;

    /**
     * Constructs a new {@code TripletDouble} with the specified values for x, y, and z.
     *
     * @param x the value for the x component
     * @param y the value for the y component
     * @param z the value for the z component
     */
    TripletDouble(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }
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
