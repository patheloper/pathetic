package org.patheloper.bukkit.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.patheloper.api.event.PathingFinishedEvent;
import org.patheloper.api.event.PathingStartFindEvent;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.bstats.BStatsHandler;

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
