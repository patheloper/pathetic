package xyz.oli;

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

    public static void setParser(MaterialParser parser) {
        if (PathingAPI.parser != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton MaterialParser");
        }
        PathingAPI.parser = parser;
    }

    public static void setSnapshotManager(SnapshotManager snapshotManager) {
        if (PathingAPI.snapshotManager != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton SnapshotManager");
        }
        PathingAPI.snapshotManager = snapshotManager;
    }

    public static void setPathfinder(Pathfinder pathfinder) {
        if (PathingAPI.pathfinder != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton Pathfinder");
        }
        PathingAPI.pathfinder = pathfinder;
    }
}
