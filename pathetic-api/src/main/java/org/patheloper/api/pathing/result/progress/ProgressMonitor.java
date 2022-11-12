package org.patheloper.api.pathing.result.progress;

import org.patheloper.api.wrapper.PathLocation;

/**
 * A Class used to estimate how long a pathing task will take to complete and how far it is.
 */
public class ProgressMonitor {

    private final Progress progress;

    public ProgressMonitor(PathLocation start, PathLocation target) {
        this.progress = new Progress(start, target, start);
    }

    /**
     * Sets the current block the pathing task is at.
     *
     * @param current The current {@link PathLocation} the pathing task is at.
     */
    public void update(PathLocation current) {
        progress.update(current);
    }

    /**
     * Gets the estimated percentage of completion
     *
     * @return The estimated percentage of completion
     */
    public double estimateProgress() {

        double length = this.progress.getStart().distance(this.progress.getTarget());
        double current = this.progress.getCurrent().distance(this.progress.getTarget());

        return current / length * 100;
    }

    static class Progress {

        private final PathLocation start;
        private final PathLocation target;
        private PathLocation current;

        public Progress(PathLocation start, PathLocation target, PathLocation current) {

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

        public void update(PathLocation current) {
            this.current = current;
        }
    }
}

