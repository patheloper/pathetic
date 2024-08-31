package org.patheloper.api.pathing.result;

public enum PathState {

  /** The pathfinding process was aborted */
  ABORTED,
  /** Pathing failed to start, typically due to an invalid start or end position. */
  INITIALLY_FAILED,
  /** The Path was successfully found */
  FOUND,
  /**
   * The Path wasn't found, either it reached its max search depth or it couldn't find more
   * positions
   */
  FAILED,
  /** Signifies that the pathfinder fell back during the pathfinding attempt */
  FALLBACK,
  /** Signifies that the pathfinder reached its length limit */
  LENGTH_LIMITED,
  /** Signifies that the pathfinder reached its iteration limit */
  MAX_ITERATIONS_REACHED
}
