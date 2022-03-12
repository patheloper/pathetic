package xyz.ollieee.api;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import xyz.ollieee.api.material.MaterialParser;
import xyz.ollieee.api.pathing.Pathfinder;
import xyz.ollieee.api.pathing.factory.PathfinderFactory;
import xyz.ollieee.api.pathing.world.chunk.SnapshotManager;

@UtilityClass
public class PatheticAPI {

    private final RegisteredServiceProvider<MaterialParser> parserRegistration = Bukkit.getServicesManager().getRegistration(MaterialParser.class);
    private final RegisteredServiceProvider<SnapshotManager> snapshotManagerRegistration = Bukkit.getServicesManager().getRegistration(SnapshotManager.class);
    private final RegisteredServiceProvider<PathfinderFactory> finderFactoryRegistration = Bukkit.getServicesManager().getRegistration(PathfinderFactory.class);

    public @NonNull Pathfinder instantiateNewPathfinder() {

        if(finderFactoryRegistration == null)
            throw new IllegalStateException();
        
        return finderFactoryRegistration.getProvider().newPathfinder();
    }
    
    public @NonNull MaterialParser getParser() {

        if(parserRegistration == null)
            throw new IllegalStateException();

        return parserRegistration.getProvider();
    }

    public @NonNull SnapshotManager getSnapshotManager() {

        if(snapshotManagerRegistration == null)
            throw new IllegalStateException();

        return snapshotManagerRegistration.getProvider();
    }
}
