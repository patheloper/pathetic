# Pathetic

Pathetic is a simple and intuitive backwards-compatible up-to-date pathfinding API for Spigot and forks.
See more info here: https://www.spigotmc.org/threads/how-pathetic.578998/#post-4644823

### How to import

#### Maven
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

#### Gradle
```xml
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
    
	dependencies {
	        implementation 'com.github.patheloper.pathetic:pathetic-mapping:VERSION'
	}
```

### Example API Usage
```java
public class PathExample extends JavaPlugin {

  @Override
  public void onEnable() {

    PatheticMapper.initialize(this);
    goFindSomePath(randomLocation(), randomLocation());
  }

  private void goFindSomePath(PathPosition start, PathPosition end) {

    Pathfinder pathfinder = PatheticMapper.newPathfinder();
    pathfinder
        .findPath(start, end, new DirectPathfinderStrategy())
        .thenAccept(
            pathfinderResult ->
                pathfinderResult
                    .getPath()
                    .getPositions()
                    .forEach(
                        location ->
                            player.sendBlockChange(
                                location, Material.YELLOW_STAINED_GLASS.createBlockData())));
  }

  private PathPosition randomLocation() {
    ThreadLocalRandom instance = ThreadLocalRandom.current();
    return new PathPosition(
        instance.nextInt(0, 100), instance.nextInt(0, 100), instance.nextInt(0, 100));
  }
}

```

#### See the `pathetic-example` module for a more in-depth example plugin.

### Javadocs:
Access the Javadocs [here](https://patheticdocs.ollieee.xyz/)
