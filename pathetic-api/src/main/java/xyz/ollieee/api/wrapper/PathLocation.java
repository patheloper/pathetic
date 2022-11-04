package xyz.ollieee.api.wrapper;

import lombok.NonNull;
import xyz.ollieee.api.util.NumberUtils;

import java.util.Objects;

public class PathLocation implements Cloneable {

    @NonNull
    private PathWorld pathWorld;

    private double x;
    private double y;
    private double z;

    public PathLocation(@NonNull PathWorld pathWorld, double x, double y, double z) {

        this.pathWorld = pathWorld;

        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Checks to see if the two locations are in the same block
     *
     * @param otherLocation The other location to check against
     * @return True if the locations are in the same block
     */
    public boolean isInSameBlock(PathLocation otherLocation) {
        return this.getBlockX() == otherLocation.getBlockX() && this.getBlockY() == otherLocation.getBlockY() && this.getBlockZ() == otherLocation.getBlockZ();
    }

    /**
     * Gets the manhattan distance between the current and another location
     * @param otherLocation the other {@link PathLocation} to get the distance to
     * @return the distance
     */
    public int manhattanDistance(PathLocation otherLocation) {
        return Math.abs(this.getBlockX() - otherLocation.getBlockX()) + Math.abs(this.getBlockY() - otherLocation.getBlockY()) + Math.abs(this.getBlockZ() - otherLocation.getBlockZ());
    }

    /**
     * Gets the octile distance between the current and another location
     * @param otherLocation the other {@link PathLocation} to get the distance to
     * @return the distance
     */
    public double octileDistance(PathLocation otherLocation) {

        double dx = Math.abs(this.x - otherLocation.x);
        double dy = Math.abs(this.y - otherLocation.y);
        double dz = Math.abs(this.z - otherLocation.z);

        double smallest = Math.min(Math.min(dx, dz), dy);
        double highest = Math.max(Math.max(dx, dz), dy);
        double mid = Math.max(Math.min(dx,dz), Math.min(Math.max(dx,dz), dy));

        double D1 = 1;
        double D2 = 1.4142135623730951;
        double D3 = 1.7320508075688772;

        return (D3 - D2) * smallest + (D2 - D1) * mid + D1 * highest;
    }

    /**
     * Gets the distance squared between the current and another location
     * @return The distance squared
     */
    public double distanceSquared(PathLocation otherLocation) {
        return NumberUtils.square(this.x - otherLocation.x) + NumberUtils.square(this.y - otherLocation.y) + NumberUtils.square(this.z - otherLocation.z);
    }

    /**
     * Gets the distance between the current and another location
     * @return The distance
     */
    public double distance(PathLocation otherLocation) {
        return NumberUtils.sqrt(this.distanceSquared(otherLocation));
    }

    /**
     * Sets the X coordinate of the {@link PathLocation}
     *
     * @param x The new X coordinate
     * @return A new {@link PathLocation}
     */
    public PathLocation setX(double x) {
        return new PathLocation(this.pathWorld, x, this.y, this.z);
    }

    /**
     * Sets the Y coordinate of the {@link PathLocation}
     *
     * @param y The new Y coordinate
     * @return A new {@link PathLocation}
     */
    public PathLocation setY(double y) {
        return new PathLocation(this.pathWorld, this.x, y, this.z);
    }

    /**
     * Sets the Z coordinate of the {@link PathLocation}
     *
     * @param z The new Z coordinate
     * @return A new {@link PathLocation}
     */
    public PathLocation setZ(double z) {
        return new PathLocation(this.pathWorld, this.x, this.y, z);
    }

    /**
     * Gets the X coordinate of the block the location is in
     *
     * @return The X coordinate of the block
     */
    public int getBlockX() {
        return (int) Math.floor(this.x);
    }

    /**
     * Gets the Y coordinate of the block the location is in
     *
     * @return The Y coordinate of the block
     */
    public int getBlockY() {
        return (int) Math.floor(this.y);
    }

    /**
     * Gets the Z coordinate of the block the location is in
     *
     * @return The Z coordinate of the block
     */
    public int getBlockZ() {
        return (int) Math.floor(this.z);
    }

    /**
     * Adds x,y,z values to the current values
     * @param x The value to add to the x
     * @param y The value to add to the y
     * @param z The value to add to the z
     * @return A new {@link PathLocation}
     */
    @NonNull
    public PathLocation add(final double x, final double y, final double z) {
        return new PathLocation(this.pathWorld, this.x + x, this.y + y, this.z + z);
    }

    /**
     * Adds the values of a vector to the location
     * @param vector The {@link PathVector} who's values will be added
     * @return A new {@link PathLocation}
     */
    @NonNull
    public PathLocation add(final PathVector vector) {
        return add(vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Subtracts x,y,z values from the current values
     * @param x The value to subtract from the x
     * @param y The value to subtract from the y
     * @param z The value to subtract from the z
     * @return A new {@link PathLocation}
     */
    @NonNull
    public PathLocation subtract(final double x, final double y, final double z) {
        return new PathLocation(this.pathWorld, this.x - x, this.y - y, this.z - z);
    }

    /**
     * Subtracts the values of a vector from the location
     * @param vector The {@link PathVector} who's values will be subtracted
     * @return A new {@link PathLocation}
     */
    @NonNull
    public PathLocation subtract(final PathVector vector) {
        return subtract(vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Converts the locations x,y,z to a {@link PathVector}
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
     * @return A new {@link PathLocation}
     */
    public PathLocation floor() {
        return new PathLocation(this.pathWorld, this.getBlockX(), this.getBlockY(), this.getBlockZ());
    }
    
    /**
     * Sets the coordinates to the middle of the block
     * @return A new {@link PathLocation}
     */
    public PathLocation mid() {
        return new PathLocation(this.pathWorld, this.getBlockX() + 0.5, this.getBlockY() + 0.5, this.getBlockZ() + 0.5);
    }

    /**
     * Gets the {@link PathWorld} the location is in
     *
     * @return The {@link PathWorld} the location is in
     */
    public @NonNull PathWorld getPathWorld() {
        return this.pathWorld;
    }

    /**
     * Gets the x value of the location
     *
     * @return The x value of the location
     */
    public double getX() {
        return this.x;
    }

    /**
     * Gets the y value of the location
     *
     * @return The y value of the location
     */
    public double getY() {
        return this.y;
    }

    /**
     * Gets the z value of the location
     *
     * @return The z value of the location
     */
    public double getZ() {
        return this.z;
    }

    @Override
    public PathLocation clone() {

        final PathLocation clone;
        try {
            clone = (PathLocation) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Superclass messed up", ex);
        }

        clone.pathWorld = this.pathWorld;

        clone.x = this.x;
        clone.y = this.y;
        clone.z = this.z;

        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathLocation that = (PathLocation) o;
        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0 && Double.compare(that.z, z) == 0 && pathWorld.equals(that.pathWorld);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathWorld, x, y, z);
    }

    public String toString() {
        return "PathLocation(pathWorld=" + this.getPathWorld() + ", x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ")";
    }
}
