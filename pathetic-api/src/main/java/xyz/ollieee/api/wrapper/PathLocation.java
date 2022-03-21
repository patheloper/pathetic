package xyz.ollieee.api.wrapper;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import xyz.ollieee.api.PatheticAPI;

@ToString
@EqualsAndHashCode
public class PathLocation implements Cloneable {

    private final PathWorld pathWorld;
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
    public PathWorld getPathWorld() {
        return this.pathWorld;
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
    @Override
    public PathLocation clone() {
        return new PathLocation(this.pathWorld, this.x, this.y, this.z);
    }

    private double sqrt(double input) {
        double sqrt = Double.longBitsToDouble(((Double.doubleToLongBits(input)-(1L<<52))>>1) + (1L <<61));
        double better = (sqrt + input/sqrt)/2.0;
        return (better + input/better)/2.0;
    }

    private double square(double value){
        return value * value;
    }

    /**
     * Gets the distance between the current and another location
     * @return The distance
     */
    public double distance(PathLocation otherLocation) {
        return sqrt(this.square(this.x - otherLocation.x) + this.square(this.y - otherLocation.y) + this.square(this.z - otherLocation.z));
    }

    /**
     * Adds x,y,z values to the current values
     * @param x The value to add to the x
     * @param y The value to add to the y
     * @param z The value to add to the z
     * @return The same mutated {@link PathLocation}
     */
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
    public PathLocation subtract(final PathVector vector) {
        subtract(vector.getX(), vector.getY(), vector.getZ());
        return this;
    }

    /**
     * Converts the locations x,y,z to a {@link PathVector}
     * @return A {@link PathVector} of the x,y,z
     */
    public PathVector toVector() {
        return new PathVector(this.x, this.y, this.z);
    }

    /**
     * Gets the {@link PathBlock} of the current location
     * @return The {@link PathBlock} at this location
     */
    public PathBlock getBlock() {
        return PatheticAPI.getSnapshotManager().getBlock(this);
    }

}
