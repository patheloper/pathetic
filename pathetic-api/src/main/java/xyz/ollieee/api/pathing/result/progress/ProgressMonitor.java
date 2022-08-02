package xyz.ollieee.api.pathing.result.progress;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import xyz.ollieee.api.wrapper.PathLocation;

public class ProgressMonitor {

    private final ProgressLocations progressLocations;

    public ProgressMonitor(PathLocation start, PathLocation target) {
        this.progressLocations = new ProgressLocations(start, target, start);
    }

    public void update(PathLocation location) {
        progressLocations.setCurrent(location);
    }

    public double estimateProgress() {
        double length = this.progressLocations.getStart().distance(this.progressLocations.getTarget());
        double current = this.progressLocations.getCurrent().distance(this.progressLocations.getTarget());
        return (current / length) * 100;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class ProgressLocations {
        private PathLocation start;
        private PathLocation target;
        private PathLocation current;
    }
}

