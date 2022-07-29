package xyz.ollieee.api.pathing.strategy;

import lombok.Getter;
import lombok.Value;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathLocation;

/**
 * An essentials bundle for {@link PathfinderStrategy}s.
 */
@Value
@Getter
public class PathfinderStrategyEssentials {

    SnapshotManager snapshotManager;
    PathLocation pathLocation;

}
