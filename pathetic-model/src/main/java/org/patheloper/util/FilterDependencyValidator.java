package org.patheloper.util;

import org.patheloper.api.pathing.filter.Depending;
import org.patheloper.api.pathing.filter.PathFilter;
import org.patheloper.api.pathing.filter.PathValidationContext;

import java.util.List;
import java.util.Map;

public class FilterDependencyValidator {

  /**
   * Validates the dependencies of the given filter based on the @Depending annotation. This method
   * checks if the filters specified in the @Depending annotation of the current filter pass their
   * validation.
   *
   * @param filter The filter whose dependencies are to be validated.
   * @param context The context containing the position, parent position, and snapshot manager.
   * @param allFilters The list of all filters applied in the current pathfinding operation.
   * @param cache A map to cache filter results to avoid double execution.
   * @return true if all the dependencies pass their validation, or if there are no dependencies;
   *     false otherwise.
   * @throws IllegalStateException if a required dependency is not found in the filter chain.
   */
  public static boolean validateDependencies(
      PathFilter filter,
      PathValidationContext context,
      List<PathFilter> allFilters,
      Map<Class<? extends PathFilter>, Boolean> cache) {

    Depending depending = filter.getClass().getAnnotation(Depending.class);
    if (depending == null) {
      return true;
    }

    for (Class<? extends PathFilter> dependency : depending.value()) {
      if (!validateSingleDependency(dependency, context, allFilters, cache)) {
        return false;
      }
    }
    return true;
  }

  private static boolean validateSingleDependency(
      Class<? extends PathFilter> dependency,
      PathValidationContext context,
      List<PathFilter> allFilters,
      Map<Class<? extends PathFilter>, Boolean> cache) {

    PathFilter dependencyFilter = findFilterByClass(dependency, allFilters);
    if (dependencyFilter == null) {
      throw ErrorLogger.logFatalError(
          "Dependency " + dependency.getName() + " for filter is not present in the filter chain.");
    }
    return cache.computeIfAbsent(dependency, k -> dependencyFilter.filter(context));
  }

  private static PathFilter findFilterByClass(
      Class<? extends PathFilter> filterClass, List<PathFilter> allFilters) {

    for (PathFilter filter : allFilters) {
      if (filter.getClass().equals(filterClass)) {
        return filter;
      }
    }
    return null;
  }
}
