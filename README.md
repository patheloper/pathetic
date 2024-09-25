# ![Pathetic](https://github.com/patheloper/pathetic/assets/50031457/2af0e918-dd57-48aa-b8e1-87356271ac1d)

# Pathetic - A Pathfinding library for Minecraft

**Pathetic** is a high-performance, backwards-compatible, and asynchronous pathfinding library designed for **Spigot**,
**Paper**, and their forks. Pathetic leverages the **A*** algorithm with customizable heuristics for real-time
pathfinding in Minecraft server environments.

Pathetic excels in handling complex terrains with features such as diagonal movement, vertical pathing, and user-defined
filters for greater flexibility.

## Key Features

- **Advanced A\* Algorithm**: Employs multiple distance metrics (Manhattan, Octile, Perpendicular) and height
  differences
  for pathfinding, optimized for 3D worlds like Minecraft.
- **Asynchronous Pathfinding**: Non-blocking operations using `CompletableFuture` to minimize server impact during
  pathfinding.
- **Fibonacci Heap for Efficient Queuing**: The open set (frontier) is managed using a **Fibonacci heap**, ensuring
  optimal node retrieval with faster `insert` and `extract min` operations.
- **Customizable Heuristics**: Fine-tune pathfinding behavior using `HeuristicWeights` for balanced navigation in any
  world configuration.
- **Regional Grid Optimization**: Uses `ExpiringHashMap` and **Bloom filters** to efficiently track explored regions,
  minimizing memory overhead.
- **Dynamic Path Filters**: Define custom filters to modify node validity or prioritize paths based on criteria such as
  passability, block type, or world boundaries.

## Installation

### Maven

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.patheloper.pathetic</groupId>
    <artifactId>pathetic-mapping</artifactId>
    <version>VERSION</version>
</dependency>
```

### Gradle

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.patheloper.pathetic:pathetic-mapping:VERSION'
}
```

## Advanced Usage: Filtering and Prioritizing Paths

Here’s how to set up and use Pathetic to find paths between random points in a Minecraft world:

```java
public class AdvancedPathExample extends JavaPlugin {

    @Override
    public void onEnable() {
        PatheticMapper.initialize(this);
        findOptimizedPath(randomLocation(), randomLocation());
    }

    @Override
    public void onDisable() {
        PatheticMapper.shutdown();
    }

    private void findOptimizedPath(PathPosition start, PathPosition end) {
        Pathfinder pathfinder = PatheticMapper.newPathfinder();
        List<PathFilter> filters = List.of(new PassablePathFilter(), new CustomHeightFilter());
        List<PathFilterStage> filterStages = List.of(new EarlyExitFilterStage());

        pathfinder
                .findPath(start, end, filters, filterStages)
                .thenAccept(
                        pathfinderResult -> {
                            if (pathfinderResult.getPathState() == PathState.FOUND) {
                                pathfinderResult
                                        .getPath()
                                        .forEach(
                                                location ->
                                                        player.sendBlockChange(
                                                                location, Material.GOLD_BLOCK.createBlockData()));
                            } else {
                                getLogger().info("Pathfinding failed or exceeded limits.");
                            }
                        });
    }

    private PathPosition randomLocation() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new PathPosition(random.nextInt(0, 100), random.nextInt(0, 100), random.nextInt(0, 100));
    }
}
```

## Technical Overview

### A* Pathfinding

Pathetic uses a robust implementation of the **A*** algorithm, tailored for Minecraft's 3D world and large-scale
terrains. Key technical highlights include:

- **Heuristic Metrics**:
    - Combines multiple metrics—**Manhattan**, **Octile**, **Perpendicular**, and **Height Differences**—for a
      comprehensive cost evaluation.
    - These metrics are dynamically weighted using `HeuristicWeights`, allowing precise tuning for the environment and
      movement constraints in Minecraft.

- **Fibonacci Heap**:
    - Pathetic employs a **Fibonacci Heap** for managing the frontier (open set), optimizing node insertion and
      retrieval with amortized O(1) insertion and O(log n) extraction times.
    - This significantly improves performance when handling large grids with numerous nodes, reducing overhead compared
      to traditional binary heaps.

- **Regional Grid Optimization**:
    - **ExpiringHashMap** and **Bloom Filters** are used to manage explored nodes efficiently.
    - This allows Pathetic to minimize memory usage by clearing out unnecessary or redundant node data while still
      preventing revisits to already-explored nodes.

- **Customizable Heuristics**:
    - Fine-tune pathfinding behavior by adjusting the weights for different distances:
        - **Manhattan Weight**: Prioritizes straight-line paths on grids.
        - **Octile Weight**: Best suited for paths allowing diagonal movement.
        - **Perpendicular Weight**: Avoids sharp turns by encouraging smoother paths.
        - **Height Weight**: Penalizes paths with large vertical movements.

### Asynchronous Pathfinding

Pathetic runs pathfinding operations asynchronously via `CompletableFuture`. This ensures that pathfinding does not
block the server's main thread, allowing smooth gameplay even in complex or large pathfinding operations.

- Asynchronous execution is particularly useful for handling real-time pathfinding in busy environments without
  impacting server performance.
- Results can be processed or displayed upon completion, providing a non-blocking experience for the user.

### Dynamic Path Filters

Pathetic allows for flexible pathfinding with **custom filters** that can be applied at various stages of the
pathfinding process. For example:

- Filters like `PassablePathFilter` ensure nodes are only considered if they meet specific conditions (e.g., passability
  of the terrain).
- **Filter Stages**: Allow multi-stage processing, adjusting node evaluation dynamically based on the current
  pathfinding state.

### Node Prioritization and Filtering

- Pathetic dynamically adjusts node prioritization based on **filter stages** and environmental context, ensuring
  optimal paths are found based on the criteria set by developers.
- Nodes that pass through filter stages can receive a priority boost, enabling more intelligent pathing decisions based
  on the environment or specific goals.

### Documentation:

- **Javadocs**: [View Javadocs](https://javadocs.pathetic.ollieee.xyz/)
- **API Documentation**: [Access our Docs](https://docs.pathetic.ollieee.xyz/)

### License:

Pathetic is released under the GPL License.

### Contributions:

We welcome contributions! Feel free to fork the repository and submit pull requests. For major changes, open an issue
first to discuss what you’d like to change.

### Support:

For help and support, join our community on
the [SpigotMC forum thread](https://www.spigotmc.org/threads/how-pathetic.578998/)
or [Discord Server](https://discord.gg/HMqCbdQjX9).

