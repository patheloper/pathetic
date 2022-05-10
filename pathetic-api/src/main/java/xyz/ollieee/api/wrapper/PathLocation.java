package xyz.ollieee.api.wrapper;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import xyz.ollieee.api.PatheticAPI;

@ToString
@EqualsAndHashCode
public class PathLocation implements Cloneable {

    private PathWorld pathWorld;
    private double x, y, z;
    
    public PathLocation(@NonNull PathWorld pathWorld, double x, double y, double z) {
        
        this.pathWorld = pathWorld;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Gets the world that this location resides in
     * @return {@link PathWorld} that contains this location
     */
    @NonNull
    public PathWorld getPathWorld() {
        return this.pathWorld;
    }

    /**
     * Sets the world that this location resides in
     */
    public void setPathWorld(PathWorld world) {
        this.pathWorld = world;
    }

    /**
     * Gets the x-coordinate of this location
     * @return The x-coordinate
     */
    public double getX() {
        return this.x;
    }

    /**
     * Gets the x-coordinate of the block this location resides in
     * @return The x-coordinate
     */
    public int getBlockX() {
        return (int) Math.floor(this.x);
    }

    /**
     * Gets the y-coordinate of this location
     * @return The y-coordinate
     */
    public double getY() {
        return this.y;
    }

    /**
     * Gets the y-coordinate of the block this location resides in
     * @return The y-coordinate
     */
    public int getBlockY() {
        return (int) Math.floor(this.y);
    }

    /**
     * Gets the z-coordinate of this location
     * @return The z-coordinate
     */
    public double getZ() {
        return this.z;
    }

    /**
     * Gets the z-coordinate of the block this location resides in
     * @return The z-coordinate
     */
    public int getBlockZ() {
        return (int) Math.floor(this.z);
    }


    /**
     * Clones the {@link PathLocation}
     * @return A new {@link PathLocation} with the same values
     */
    @NonNull
    @Override
    public PathLocation clone() {

        final PathLocation clone;
        try {
            clone = (PathLocation) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Superclass messed up", ex);
        }

        clone.pathWorld = this.pathWorld;
        clone.x = this.x;
        clone.y = this.y;
        clone.z = this.z;

        return clone;
    }

    /**
     * Gets the distance squared between the current and another location
     * @return The distance squared
     */
    public double distanceSquared(PathLocation otherLocation) {
        return this.square(this.x - otherLocation.x) + this.square(this.y - otherLocation.y) + this.square(this.z - otherLocation.z);
    }

    /**
     * Gets the distance between the current and another location
     * @return The distance
     */
    public double distance(PathLocation otherLocation) {
        return sqrt(this.distanceSquared(otherLocation));
    }

    /**
     * Adds x,y,z values to the current values
     * @param x The value to add to the x
     * @param y The value to add to the y
     * @param z The value to add to the z
     * @return The same mutated {@link PathLocation}
     */
    @NonNull
    public PathLocation add(final double x, final double y, final double z) {

        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    /**
     * Adds the values of a vector to the location
     * @param vector The {@link PathVector} who's values will be added
     * @return The same mutated {@link PathLocation}
     */
    @NonNull
    public PathLocation add(final PathVector vector) {
        add(vector.getX(), vector.getY(), vector.getZ());
        return this;
    }

    /**
     * Subtracts x,y,z values from the current values
     * @param x The value to subtract from the x
     * @param y The value to subtract from the y
     * @param z The value to subtract from the z
     * @return The same mutated {@link PathLocation}
     */
    @NonNull
    public PathLocation subtract(final double x, final double y, final double z) {

        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    /**
     * Subtracts the values of a vector from the location
     * @param vector The {@link PathVector} who's values will be subtracted
     * @return The same mutated {@link PathLocation}
     */
    @NonNull
    public PathLocation subtract(final PathVector vector) {
        subtract(vector.getX(), vector.getY(), vector.getZ());
        return this;
    }

    /**
     * Converts the locations x,y,z to a {@link PathVector}
     * @return A {@link PathVector} of the x,y,z
     */
    @NonNull
    public PathVector toVector() {
        return new PathVector(this.x, this.y, this.z);
    }

    /**
     * Gets the {@link PathBlock} of the current location
     * @return The {@link PathBlock} at this location
     */
    @NonNull
    public PathBlock getBlock() {
        return PatheticAPI.getSnapshotManager().getBlock(this);
    }

    /**
     * Rounds the x,y,z values to the floor of the values
     * @return The mutated {@link PathLocation}
     */
    public PathLocation toIntegers() {

        this.x = this.getBlockX();
        this.y = this.getBlockY();
        this.z = this.getBlockZ();

        return this;
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

    private double sqrt(double input) {
        double sqrt = Double.longBitsToDouble(((Double.doubleToLongBits(input)-(1L<<52))>>1) + (1L <<61));
        double better = (sqrt + input/sqrt)/2.0;
        return (better + input/better)/2.0;
    }

    private double square(double value){
        return value * value;
    }

}
