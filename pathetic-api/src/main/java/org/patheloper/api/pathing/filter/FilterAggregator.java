package org.patheloper.api.pathing.filter;

import java.util.List;
import org.patheloper.api.wrapper.PathPosition;

public class FilterAggregator {

  private final List<PathFilter> filters;

  public FilterAggregator(List<PathFilter> filters) {
    this.filters = filters;
  }

  /**
   * Applies all filters in the aggregator, updating the target position if necessary.
   *
   * @param context The validation context.
   * @return The aggregated result, including the final validation result and updated target.
   */
  public FilterOutcome aggregate(PathValidationContext context) {
    PathPosition currentTarget = context.getTargetPosition();
    boolean hasWarning = false;

    for (PathFilter filter : filters) {
      FilterOutcome outcome = filter.filter(context);

      if (outcome.getResult() == FilterResult.FAIL) {
        return new FilterOutcome(FilterResult.FAIL, currentTarget); // early exit
      }

      if (outcome.getResult() == FilterResult.WARNING) {
        hasWarning = true;
      }

      if (outcome.getUpdatedTarget() != null) {
        currentTarget = outcome.getUpdatedTarget();
        context.setTargetPosition(currentTarget);
      }
    }

    return new FilterOutcome(hasWarning ? FilterResult.WARNING : FilterResult.PASS, currentTarget);
  }
}
