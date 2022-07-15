# Path-eticAPI

A Simple and intuitive pathfinding API for Spigot & Paper plugins

### Shade it in
## Maven
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
 
 	<dependency>
	    <groupId>com.github.olijeffers0n.PatheticAPI</groupId>
	    <artifactId>pathetic-mapping</artifactId>
	    <version>Tag</version>
	</dependency>
```

## Gradle
```xml
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
    
    	dependencies {
	        implementation 'com.github.olijeffers0n.PatheticAPI:pathetic-mapping:Tag'
	}
```

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
