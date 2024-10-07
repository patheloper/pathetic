package org.patheloper.api.pathing.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Value;

/** A stage for multiple PathFilters. */
@Getter
@Value
public class PathFilterStage {

  /**
   * -- GETTER -- Get all filters in this stage.
   *
   * @return the set of filters in this stage
   */
  List<PathFilter> filters = new ArrayList<>();

  public PathFilterStage(PathFilter... pathFilter) {
    filters.addAll(Arrays.asList(pathFilter));
  }

  /** Cleans up all filters in the stage. */
  public void cleanup() {
    filters.forEach(PathFilter::cleanup);
  }
}
