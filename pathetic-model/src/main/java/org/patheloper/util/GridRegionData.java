package org.patheloper.util;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import org.patheloper.api.wrapper.PathPosition;

/**
 * The GridRegionData class represents the data associated with a grid region. This data includes a
 * Bloom filter used to quickly check if a position is within the region and a set of positions that
 * have been examined by the pathfinder.
 */
@Getter
public class GridRegionData {

  /**
   * The default size of the Bloom filter. A larger size will reduce the false positive probability
   * of the Bloom filter, but will also increase the memory usage.
   */
  private static final int DEFAULT_BLOOM_FILTER_SIZE = 1000;

  /**
   * The default false positive probability of the Bloom filter. A lower FPP means a smaller chance
   * of incorrectly identifying a position as being in the region, but it also requires a larger
   * Bloom filter.
   */
  private static final double DEFAULT_FPP = 0.01; // 1%

  /**
   * The Bloom filter used to store the positions of the region. This filter is used to quickly
   * check if a position is within the region without having to iterate over all the positions in
   * the region.
   */
  private final BloomFilter<PathPosition> bloomFilter;

  /**
   * The set of positions that have been examined by the pathfinder. This set is used to track the
   * positions that have been examined by the pathfinder to avoid examining the same position
   * multiple times.
   */
  private final Set<PathPosition> regionalExaminedPositions;

  public GridRegionData() {
    Funnel<PathPosition> pathPositionFunnel =
        (pathPosition, into) ->
            into.putInt(pathPosition.getBlockX())
                .putInt(pathPosition.getBlockY())
                .putInt(pathPosition.getBlockZ());

    bloomFilter = BloomFilter.create(pathPositionFunnel, DEFAULT_BLOOM_FILTER_SIZE, DEFAULT_FPP);
    regionalExaminedPositions = new HashSet<>();
  }
}
