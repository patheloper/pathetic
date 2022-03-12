# Path-eticAPI

A Simple and intuitive pathfinding API for Spigot & Paper plugins

### Example Usage
```java
class PathExample {
    
    public void displayPath(Location startLocation, Location endLocation) {
        
        // If shaded and not used as a plugin dependency, Call initialise once
        Pathetic.initialize(this);
        
        Pathfinder pathFinder = PatheticAPI.instantiateNewPathfinder();
        pathFinder.findPathAsync(startLocation, endLocation).thenAccept(pathfinderResult -> 
                pathfinderResult.getPath().getLocations().forEach(location -> 
                        player.sendBlockChange(location, Material.YELLOW_STAINED_GLASS.createBlockData())));
    }
}
```