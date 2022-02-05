# PathingAPI

### Example Usage
```java
PathfinderOptions options = PathfinderOptions.builder()
        .start(startLocation)
        .target(endLocation)
        .asyncMode(true)
        .strategy(new DirectPathfinderStrategy())
        .build();

PathingAPI.getPathfinder().findPath(options, pathfinderResult -> {
    pathfinderResult.getPath().getLocations().forEach(location -> player.sendBlockChange(location, 
    Material.YELLOW_STAINED_GLASS.createBlockData()));
});