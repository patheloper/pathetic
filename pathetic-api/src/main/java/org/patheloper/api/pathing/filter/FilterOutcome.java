package org.patheloper.api.pathing.filter;

import org.patheloper.api.wrapper.PathPosition;

public class FilterOutcome {
  private final FilterResult result;
  private final PathPosition updatedTarget;

  public FilterOutcome(FilterResult result, PathPosition updatedTarget) {
    this.result = result;
    this.updatedTarget = updatedTarget;
  }

  public FilterResult getResult() {
    return result;
  }

  public PathPosition getUpdatedTarget() {
    return updatedTarget;
  }
}
