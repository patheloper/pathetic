package org.patheloper.api.pathing.result;

public enum PathState {

  /** The Path was successfully found for a given strategy */
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
