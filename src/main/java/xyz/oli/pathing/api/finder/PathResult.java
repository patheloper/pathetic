package xyz.oli.pathing.api.finder;

import xyz.oli.pathing.api.Path;

public record PathResult(PathSuccess success, Path path){}

enum PathSuccess {
    FOUND,
    FAILED
}
