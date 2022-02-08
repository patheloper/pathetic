package xyz.oli.api.event;

import lombok.Getter;

import xyz.oli.api.pathing.result.PathfinderResult;

public class PathingFinishedEvent extends PathingEvent {

    @Getter
    PathfinderResult pathfinderResult;

    public PathingFinishedEvent(PathfinderResult result) {
        this.pathfinderResult = result;
    }
}
