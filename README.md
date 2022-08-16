# Path-eticAPI

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
	    <groupId>com.github.olijeffers0n.PatheticAPI</groupId>
	    <artifactId>pathetic-mapping</artifactId>
	    <version>1.0.2-PRE</version> <!-- 1.0.2-PRE or higher. Below wont work -->
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
	        implementation 'com.github.olijeffers0n.PatheticAPI:pathetic-mapping:Tag'
	}
```

### Example API Usage
```java
public class PathExample extends JavaPlugin {
    
    @Override
    public void onEnable() {
    
        PatheticMapper.initialize(this);
        goFindSomePath();
    }
    
    private void goFindSomePath() {

        Pathfinder pathfinder = PatheticMapper.newPathfinder();
        pathfinder.findPath(startLocation, endLocation).accept(pathfinderResult ->
                pathfinderResult.getPath().getLocations().forEach(location ->
                        player.sendBlockChange(location, Material.YELLOW_STAINED_GLASS.createBlockData())));
    }
}
```

### Javadocs:
Access the Javadocs [here](http://patheticdocs.ollieee.xyz/)
