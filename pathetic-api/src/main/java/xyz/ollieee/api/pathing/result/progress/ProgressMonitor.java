package xyz.ollieee.api.pathing.result.progress;

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

    static class ProgressLocations {
        private PathLocation start;
        private PathLocation target;
        private PathLocation current;

        public ProgressLocations(PathLocation start, PathLocation target, PathLocation current) {
            this.start = start;
            this.target = target;
            this.current = current;
        }

        public PathLocation getStart() {
            return this.start;
        }

        public PathLocation getTarget() {
            return this.target;
        }

        public PathLocation getCurrent() {
            return this.current;
        }

        public void setStart(PathLocation start) {
            this.start = start;
        }

        public void setTarget(PathLocation target) {
            this.target = target;
        }

        public void setCurrent(PathLocation current) {
            this.current = current;
        }
    }
}

