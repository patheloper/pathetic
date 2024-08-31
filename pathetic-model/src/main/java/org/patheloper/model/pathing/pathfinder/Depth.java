package org.patheloper.model.pathing.pathfinder;

import lombok.Getter;

@Getter
public class Depth {

  private int depth;

  public Depth(int depth) {
    this.depth = depth;
  }

  public void increment() {
    depth++;
  }
}
