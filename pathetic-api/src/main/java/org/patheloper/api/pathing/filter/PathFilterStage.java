package org.patheloper.api.pathing.filter;

import lombok.Value;

import java.util.Set;

/**
 * A stage for multiple PathFilters.
 */
@Value
public class PathFilterStage {

  Set<PathFilter> filters;

  /**
   * Filters the given context with all filters in the stage.
   * @param context The context to filter.
   * @return true if the context passes all filters, false otherwise.
   */
  public boolean filter(PathValidationContext context) {
    return filters.stream().allMatch(filter -> filter.filter(context));
  }
}
