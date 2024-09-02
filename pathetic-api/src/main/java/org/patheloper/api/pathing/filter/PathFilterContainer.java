package org.patheloper.api.pathing.filter;

import lombok.Value;

import java.util.Set;

/**
 * A container for multiple path filters.
 */
@Value
public class PathFilterContainer {

  Set<PathFilter> filters;

  /**
   * Filters the given context with all filters in the container.
   * @param context The context to filter.
   * @return true if the context passes all filters, false otherwise.
   */
  public boolean filter(PathValidationContext context) {
    return filters.stream().allMatch(filter -> filter.filter(context));
  }
}
