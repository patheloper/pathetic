package xyz.oli.api.wrapper;

import lombok.Getter;

import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

@Getter
public class PathVector implements Cloneable{
    
    private double x;
    private double y;
    private double z;
    
    public PathVector() {
    }
    
    public PathVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public PathVector setX(double x) {
        this.x = x;
        return this;
    }
    
    public PathVector setY(double y) {
        this.y = y;
        return this;
    }
    
    public PathVector setZ(double z) {
        this.z = z;
        return this;
    }

    public PathVector subtract(PathVector otherVector) {
        this.x -= otherVector.x;
        this.y -= otherVector.y;
        this.z -= otherVector.z;
        return this;
    }

    public PathVector multiply(double value) {
        this.x *= value;
        this.y *= value;
        this.z *= value;
        return this;
    }

    public PathVector clone() {
        return new PathVector(this.x, this.y, this.z);
    }

    public PathVector normalize() {
        double length = this.length();
        this.x /= length;
        this.y /= length;
        this.z /= length;
        return this;
    }

    public double length() {
        return Math.sqrt(NumberConversions.square(this.x) + NumberConversions.square(this.y) + NumberConversions.square(this.z));
    }

    public Vector toBukkit() {
        return new Vector(this.x, this.y, this.z);
    }
}
