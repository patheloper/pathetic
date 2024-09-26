package org.patheloper.util;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import org.patheloper.api.wrapper.PathPosition;

/**
 * Represents the data associated with a grid region. This data includes a Bloom filter used to
 * quickly check if a position is within the region and a set of positions that have been examined
 * by the pathfinder to avoid duplicate examinations.
 */
@Getter
public class GridRegionData {

  /**
   * The default size of the Bloom filter. A larger size will reduce the false positive probability
   * but increase memory usage.
   */
  private static final int DEFAULT_BLOOM_FILTER_SIZE = 1000;

  /**
   * The default false positive probability (FPP) of the Bloom filter. A lower FPP reduces the
   * chance of false positives but requires a larger Bloom filter.
   */
  private static final double DEFAULT_FPP = 0.01; // 1% false positive probability

  /**
   * The Bloom filter used to track positions within the region. This allows efficient checks for
   * position existence without iterating over all positions.
   */
  private final BloomFilter<PathPosition> bloomFilter;

  /** The set of positions that have been examined by the pathfinder to prevent re-examination. */
  private final Set<PathPosition> regionalExaminedPositions;

  /**
   * Constructs a new GridRegionData instance, initializing the Bloom filter and the examined
   * positions set.
   */
  public GridRegionData() {
    // Defines how the PathPosition is serialized into the Bloom filter for storage.
    Funnel<PathPosition> pathPositionFunnel =
        (pathPosition, into) ->
            into.putInt(pathPosition.getBlockX())
                .putInt(pathPosition.getBlockY())
                .putInt(pathPosition.getBlockZ());

    // Initializes the Bloom filter with the given funnel, size, and false positive probability.
    this.bloomFilter =
        BloomFilter.create(pathPositionFunnel, DEFAULT_BLOOM_FILTER_SIZE, DEFAULT_FPP);
    this.regionalExaminedPositions = new HashSet<>();
  }
}
