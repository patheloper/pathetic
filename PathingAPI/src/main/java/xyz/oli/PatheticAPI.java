package xyz.oli;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import xyz.oli.material.MaterialParser;
import xyz.oli.pathing.Pathfinder;
import xyz.oli.pathing.PathfinderFactory;
import xyz.oli.pathing.SnapshotManager;

@UtilityClass
public class PatheticAPI {

    private MaterialParser parser = null;
    private SnapshotManager snapshotManager = null;

    public Pathfinder instantiateNewPathfinder() {
        
        RegisteredServiceProvider<PathfinderFactory> registration = Bukkit.getServicesManager().getRegistration(PathfinderFactory.class);
        
        if(registration == null)
            throw new IllegalStateException();
        
        return registration.getProvider().newPathfinder();
    }
    
    public void setFields(@NonNull MaterialParser parser, @NonNull SnapshotManager snapshotManager) {
        
        if (PatheticAPI.parser != null || PatheticAPI.snapshotManager != null)
            throw new UnsupportedOperationException("Cannot redefine singleton MaterialParser");
        
        PatheticAPI.parser = parser;
        PatheticAPI.snapshotManager = snapshotManager;
    }
    
    public MaterialParser getParser() {
        return parser;
    }

    public SnapshotManager getSnapshotManager() {
        return snapshotManager;
    }
}
