package xyz.ollieee.model.snapshot;

import lombok.NonNull;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathLocation;

public class LoadingSnapshotManager implements SnapshotManager {

    private final SnapshotHolder snapshotHolder;

    public LoadingSnapshotManager(SnapshotHolder snapshotHolder) {
        this.snapshotHolder = snapshotHolder;
    }

    @Override
    public PathBlock getBlock(@NonNull PathLocation location) {
        return this.snapshotHolder.getBlock(location, true).orElse(null);
    }
}
