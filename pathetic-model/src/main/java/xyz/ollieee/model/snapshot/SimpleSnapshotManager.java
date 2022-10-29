package xyz.ollieee.model.snapshot;

import lombok.NonNull;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathLocation;

import java.util.Optional;

public class SimpleSnapshotManager implements SnapshotManager {

    private final SnapshotHolder snapshotHolder;

    public SimpleSnapshotManager(SnapshotHolder snapshotHolder) {
        this.snapshotHolder = snapshotHolder;
    }

    @Override
    public PathBlock getBlock(@NonNull PathLocation location) {
        Optional<PathBlock> block = this.snapshotHolder.getBlock(location, false);
        return block.orElse(null);
    }
}
