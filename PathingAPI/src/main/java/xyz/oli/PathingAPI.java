package xyz.oli;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import xyz.oli.material.MaterialParser;
import xyz.oli.pathing.Pathfinder;
import xyz.oli.pathing.SnapshotManager;

@UtilityClass
public class PathingAPI {

    private MaterialParser parser = null;
    private SnapshotManager snapshotManager = null;
    private Pathfinder pathfinder = null;

    public Pathfinder getPathfinder() {
        return pathfinder;
    }

    public MaterialParser getParser() {
        return parser;
    }

    public SnapshotManager getSnapshotManager() {
        return snapshotManager;
    }

    public static void setFields(@NonNull MaterialParser parser, @NonNull SnapshotManager snapshotManager, @NonNull Pathfinder pathfinder) {

        if (PathingAPI.parser != null || PathingAPI.snapshotManager != null || PathingAPI.pathfinder != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton MaterialParser");
        }
            PathingAPI.parser = parser;
            PathingAPI.snapshotManager = snapshotManager;
            PathingAPI.pathfinder = pathfinder;
    }
}
