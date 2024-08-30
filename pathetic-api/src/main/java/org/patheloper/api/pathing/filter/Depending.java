package org.patheloper.api.pathing.filter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to specify dependencies between {@link PathFilter} implementations.
 *
 * <p>This annotation should be applied to a {@link PathFilter} to indicate that it depends on one
 * or more other filters. When a filter is annotated with {@code @Depending}, all specified
 * dependencies are dynamically created and validated for the annotated filter to be considered
 * valid.
 *
 * <p>Usage Example:
 *
 * <pre>{@code
 * @Depending({PassablePathFilter.class})
 * public class SolidGroundPathFilter implements PathFilter {
 *     @Override
 *     public boolean filter(PathValidationContext context) {
 *         // Filtering logic here
 *         return true;
 *     }
 * }
 * }</pre>
 *
 * <p>In the example above, the {@code SolidGroundPathFilter} depends on the {@code
 * PassablePathFilter}. This means that {@code PassablePathFilter} will be dynamically created and
 * validated whenever {@code SolidGroundPathFilter} is used.
 *
 * <p>Dependencies are generated and validated at runtime, regardless of their presence in the
 * filter chain. If a dependent filter cannot be instantiated, an {@link IllegalStateException} will
 * be thrown.
 *
 * @see PathFilter
 * @see PathValidationContext
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Depending {

  /**
   * Specifies the dependent filters required for validation.
   *
   * @return An array of {@link PathFilter} classes that the annotated filter depends on.
   */
  Class<? extends PathFilter>[] value();
}
