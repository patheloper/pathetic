package org.patheloper.util;

import org.patheloper.api.pathing.filter.Depending;
import org.patheloper.api.pathing.filter.PathFilter;
import org.patheloper.api.pathing.filter.PathValidationContext;

import java.util.List;

public class FilterDependencyValidator {

  /**
   * Validates the dependencies of the given filter based on the @Depending annotation. This method
   * checks if the filters specified in the @Depending annotation of the current filter pass their
   * validation.
   *
   * @param filter The filter whose dependencies are to be validated.
   * @param context The context containing the position, parent position, and snapshot manager.
   * @param allFilters The list of all filters applied in the current pathfinding operation.
   * @return true if all the dependencies pass their validation, or if there are no dependencies;
   *     false otherwise.
   * @throws IllegalStateException if a required dependency is not found in the filter chain.
   */
  public static boolean validateDependencies(
      PathFilter filter, PathValidationContext context, List<PathFilter> allFilters) {
    Depending depending = filter.getClass().getAnnotation(Depending.class);
    if (depending != null) {
      for (Class<? extends PathFilter> dependency : depending.value()) {
        boolean dependencyFound = false;
        for (PathFilter f : allFilters) {
          if (f.getClass().equals(dependency)) {
            dependencyFound = true;
            if (!f.filter(context)) {
              return false;
            }
          }
        }
        if (!dependencyFound) {
          throw ErrorLogger.logFatalError(
              "Dependency "
                  + dependency.getName()
                  + " for filter "
                  + filter.getClass().getName()
                  + " is not present in the filter chain.");
        }
      }
    }
    return true;
  }
}
