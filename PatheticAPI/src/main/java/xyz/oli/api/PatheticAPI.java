package xyz.oli.api;

import lombok.experimental.UtilityClass;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import xyz.oli.api.material.MaterialParser;
import xyz.oli.api.pathing.Pathfinder;
import xyz.oli.api.pathing.factory.PathfinderFactory;
import xyz.oli.api.pathing.world.chunk.SnapshotManager;

@UtilityClass
public class PatheticAPI {

    private final RegisteredServiceProvider<MaterialParser> parserRegistration = Bukkit.getServicesManager().getRegistration(MaterialParser.class);
    private final RegisteredServiceProvider<SnapshotManager> snapshotManagerRegistration = Bukkit.getServicesManager().getRegistration(SnapshotManager.class);
    private final RegisteredServiceProvider<PathfinderFactory> finderFactoryRegistration = Bukkit.getServicesManager().getRegistration(PathfinderFactory.class);

    public Pathfinder instantiateNewPathfinder() {

        if(finderFactoryRegistration == null)
            throw new IllegalStateException();
        
        return finderFactoryRegistration.getProvider().newPathfinder();
    }
    
    public MaterialParser getParser() {

        if(parserRegistration == null)
            throw new IllegalStateException();

        return parserRegistration.getProvider();
    }

    public SnapshotManager getSnapshotManager() {

        if(snapshotManagerRegistration == null)
            throw new IllegalStateException();

        return snapshotManagerRegistration.getProvider();
    }
}
