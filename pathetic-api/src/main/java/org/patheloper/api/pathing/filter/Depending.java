package org.patheloper.api.pathing.filter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to specify dependencies between PathFilters.
 *
 * <p>This annotation should be applied to {@link PathFilter} implementations to indicate that the
 * annotated filter depends on one or more other filters. When a filter is annotated
 * with @Depending, the specified dependent filters must also pass their validation for the
 * annotated filter to be considered valid.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * @Depending(PassablePathFilter.class)
 * public class SolidGroundPathFilter implements PathFilter {
 *     @Override
 *     public boolean filter(PathValidationContext context) {
 *         // Filtering logic here
 *         return false;
 *     }
 * }
 * }</pre>
 *
 * <p>In the above example, the {@code SolidGroundPathFilter} depends on the {@code
 * PassablePathFilter}. This means that for {@code SolidGroundPathFilter} to pass, {@code
 * PassablePathFilter} must also pass.
 *
 * <p>If a dependent filter is not included in the filter chain, an {@link IllegalStateException}
 * will be thrown during validation.
 *
 * @see PathFilter
 * @see PathValidationContext
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Depending {

  /**
   * Specifies the dependent filters that must pass for the annotated filter to be considered valid.
   *
   * @return An array of {@link PathFilter} classes that the annotated filter depends on.
   */
  Class<? extends PathFilter>[] value();
}
