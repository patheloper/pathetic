package org.patheloper.util;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import org.patheloper.api.wrapper.PathPosition;

/**
 * The GridRegionData class represents the data associated with a grid region. This data includes a
 * Bloom filter used to quickly check if a position is within the region and a set of positions that
 * have been examined by the pathfinder.
 */
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
  private final BloomFilter<String> bloomFilter;

  /**
   * The set of positions that have been examined by the pathfinder. This set is used to track the
   * positions that have been examined by the pathfinder to avoid examining the same position
   * multiple times.
   */
  private final Set<PathPosition> regionalExaminedPositions;

  public GridRegionData() {
    bloomFilter =
        BloomFilter.create(
            Funnels.stringFunnel(Charset.defaultCharset()), DEFAULT_BLOOM_FILTER_SIZE, DEFAULT_FPP);
    regionalExaminedPositions = new HashSet<>();
  }

  public BloomFilter<String> getBloomFilter() {
    return bloomFilter;
  }

  public Set<PathPosition> getRegionalExaminedPositions() {
    return regionalExaminedPositions;
  }
}
