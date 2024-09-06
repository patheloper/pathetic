package org.patheloper.api.pathing.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.Value;

/** A stage for multiple PathFilters. */
@Value
public class PathFilterStage {

  Set<PathFilter> filters = new HashSet<>();

  public PathFilterStage(PathFilter... pathFilter) {
    filters.addAll(Arrays.asList(pathFilter));
  }

  /**
   * Filters the given context with all filters in the stage.
   *
   * @param context The context to filter.
   * @return true if the context passes all filters, false otherwise.
   */
  public boolean filter(PathValidationContext context) {
    return filters.stream().allMatch(filter -> filter.filter(context));
  }

  /** Cleans up all filters in the stage. */
  public void cleanup() {
    filters.forEach(PathFilter::cleanup);
  }
}
