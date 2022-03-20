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

    private final RegisteredServiceProvider<MaterialParser> parserRegistration;
    private final RegisteredServiceProvider<SnapshotManager> snapshotManagerRegistration;
    private final RegisteredServiceProvider<PathfinderFactory> finderFactoryRegistration;

    static {

        parserRegistration = Bukkit.getServicesManager().getRegistration(MaterialParser.class);
        snapshotManagerRegistration = Bukkit.getServicesManager().getRegistration(SnapshotManager.class);
        finderFactoryRegistration = Bukkit.getServicesManager().getRegistration(PathfinderFactory.class);

        if(parserRegistration == null || snapshotManagerRegistration == null || finderFactoryRegistration == null)
            throw new IllegalStateException("Services registration has failed");
    }

    /**
     * Returns a new Pathfinder instance
     * @return {@link Pathfinder} The new pathfinder instance
     */
    @NonNull
    public Pathfinder instantiateNewPathfinder() {
        return finderFactoryRegistration.getProvider().newPathfinder();
    }

    /**
     * Returns the material parser instance
     * @return {@link MaterialParser} The parser instance
     */
    @NonNull
    public MaterialParser getParser() {
        return parserRegistration.getProvider();
    }

    /**
     * Returns the snapshot manager instance
     * @return {@link SnapshotManager} The snapshot manager instance
     */
    @NonNull
    public SnapshotManager getSnapshotManager() {
        return snapshotManagerRegistration.getProvider();
    }
}
