package xyz.ollieee.model.pathing.strategy;

import lombok.NonNull;
import xyz.ollieee.Pathetic;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathBlockType;
import xyz.ollieee.api.wrapper.PathLocation;

public class WalkablePathfinderStrategy implements PathfinderStrategy {
    
    @Override
    public boolean isValid(@NonNull PathBlock current, PathBlock previous, PathBlock previouser) {
    
        PathLocation below = current.getPathLocation().clone().subtract(0, 1, 0);
        PathLocation above = current.getPathLocation().clone().add(0, 1, 0);
        PathLocation aboveAbove = above.clone().add(0, 1, 0);
    
        SnapshotManager snapshotManager = Pathetic.getSnapshotManager();
        return current.isPassable()
                && snapshotManager.getBlock(below).getPathBlockType() == PathBlockType.SOLID
                && snapshotManager.getBlock(above).isPassable()
                && snapshotManager.getBlock(aboveAbove).isPassable();
    }
}
