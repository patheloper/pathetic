package xyz.oli.api.wrapper;

import lombok.Getter;

@Getter
public class PathVector {
    
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
}
