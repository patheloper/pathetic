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
public class PathingAPI {

    private MaterialParser parser = null;
    private SnapshotManager snapshotManager = null;

    public Pathfinder getPathfinder() {
        RegisteredServiceProvider<PathfinderFactory> registration = Bukkit.getServicesManager().getRegistration(PathfinderFactory.class);
        return registration != null ? registration.getProvider().newPathfinder() : null;
    }

    public MaterialParser getParser() {
        return parser;
    }

    public SnapshotManager getSnapshotManager() {
        return snapshotManager;
    }

    public static void setFields(@NonNull MaterialParser parser, @NonNull SnapshotManager snapshotManager) {

        if (PathingAPI.parser != null || PathingAPI.snapshotManager != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton MaterialParser");
        }
            PathingAPI.parser = parser;
            PathingAPI.snapshotManager = snapshotManager;
    }
}
