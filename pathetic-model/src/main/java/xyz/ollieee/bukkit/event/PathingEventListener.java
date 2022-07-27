package xyz.ollieee.bukkit.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import xyz.ollieee.api.event.PathingFinishedEvent;
import xyz.ollieee.api.event.PathingStartFindEvent;
import xyz.ollieee.api.pathing.result.PathfinderResult;
import xyz.ollieee.bstats.BStatsHandler;

public class PathingEventListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPathingStart(PathingStartFindEvent event) {
        BStatsHandler.increasePathCount();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPathingFinished(PathingFinishedEvent event) {

        PathfinderResult pathfinderResult = event.getPathfinderResult();
        if(!pathfinderResult.successful())
            BStatsHandler.increaseFailedPathCount();
        else
            BStatsHandler.addLength(pathfinderResult.getPath().length());
    }

}
