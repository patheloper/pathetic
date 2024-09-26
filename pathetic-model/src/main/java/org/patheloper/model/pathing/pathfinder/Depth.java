package org.patheloper.model.pathing.pathfinder;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/** Represents the depth of a node or search operation in a pathfinding algorithm. */
@Getter
@EqualsAndHashCode
public class Depth {

  private int depth;

  /**
   * Constructs a new {@code Depth} object with the specified initial depth value.
   *
   * @param initialDepth the initial depth value
   */
  public Depth(int initialDepth) {
    this.depth = initialDepth;
  }

  /** Increments the depth value by 1. */
  public void increment() {
    depth++;
  }
}
