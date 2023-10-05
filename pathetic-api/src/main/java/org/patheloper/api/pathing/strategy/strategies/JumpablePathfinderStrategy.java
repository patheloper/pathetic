package org.patheloper.api.pathing.strategy.strategies;

import lombok.NonNull;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathPosition;

public class JumpablePathfinderStrategy extends WalkablePathfinderStrategy {
    
    private final int jumpHeight;
    private final int jumpDistance;
    
    private PathPosition lastValidPosition = null;
    
    public JumpablePathfinderStrategy() {
        this(2, 1, 4);
    }
    
    public JumpablePathfinderStrategy(int height, int jumpHeight, int jumpDistance) {
        super(height);
        this.jumpHeight = jumpHeight;
        this.jumpDistance = jumpDistance;
    }
    
    @Override
    public boolean isValid(@NonNull PathPosition position, @NonNull SnapshotManager snapshotManager) {
        PathBlock startBlock = snapshotManager.getBlock(position);
        if(canStandOn(startBlock, snapshotManager)) {
            lastValidPosition = position;
            return true;
        }
        
        if(position.getBlockY() - lastValidPosition.getBlockY() > jumpHeight) {
            return false;
        }
        
        return !(position.distance(lastValidPosition) > jumpDistance);
    }
}
