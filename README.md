# Path-eticAPI

A Simple and intuitive pathfinding API for Spigot & Paper plugins

### Example Usage
```java
PathfinderOptions options = PathfinderOptions.builder()
        .start(startLocation)
        .target(endLocation)
        .asyncMode(true)
        .strategy(new DirectPathfinderStrategy())
        .build();

PatheticAPI.instantiateNewPathfinder().findPathAsync(options, pathfinderResult -> {
    pathfinderResult.getPath().getLocations().forEach(location -> player.sendBlockChange(location, 
    Material.YELLOW_STAINED_GLASS.createBlockData()));
});