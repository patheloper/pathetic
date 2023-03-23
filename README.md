# Pathetic

A simple and intuitive 1.8-1.19 A* pathfinding API for Spigot & Paper plugins 

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
        pathfinder.findPath(start, end).thenAccept(pathfinderResult ->
                pathfinderResult.getPath().getPositions().forEach(location ->
                        player.sendBlockChange(location, Material.YELLOW_STAINED_GLASS.createBlockData())));
    }
    
    private PathPosition randomLocation() {
        ThreadLocalRandom instance = ThreadLocalRandom.current();
        return new PathPosition(instance.nextInt(0, 100), instance.nextInt(0, 100), instance.nextInt(0, 100));
    }
}
```

#### See the `pathetic-example` module for a more in-depth example plugin.

### Javadocs:
Access the Javadocs [here](https://patheticdocs.ollieee.xyz/)
