package xyz.ollieee.api.pathing.strategy;

import lombok.Getter;
import lombok.Value;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathLocation;

/**
 * A Dao to access the needed essentials for a {@link PathfinderStrategy}
 */
@Value
@Getter
// TODO: 28/07/2022 Not a good name for this class. It's not a Dao. It's a wrapper for the essentials of a strategy.
public class StrategyEssentialsDao {

    SnapshotManager snapshotManager;
    PathLocation pathLocation;

}
