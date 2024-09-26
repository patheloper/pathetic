package org.patheloper.util;

import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

/**
 * A {@link ConcurrentHashMap} that automatically removes entries after they have expired. The
 * expiration time is set to 5 minutes by default. Cleanup of expired entries occurs when entries
 * are accessed or added, and it runs every 5 minutes.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class ExpiringHashMap<K, V> extends ConcurrentHashMap<K, ExpiringHashMap.Entry<V>> {

  private static final long EXPIRATION_TIME = 5 * 60 * 1000; // 5 minutes in milliseconds
  private long lastCleanupTime = System.currentTimeMillis();

  /**
   * Inserts a key-value pair into the map. Expired entries are removed upon insertion if the
   * cleanup period has elapsed.
   *
   * @param key the key to insert
   * @param value the entry containing the value and its expiration time
   * @return the previous value associated with the key, or {@code null} if there was no mapping for
   *     the key
   */
  @Override
  public Entry<V> put(K key, Entry<V> value) {
    cleanupExpiredEntries();
    return super.put(key, value);
  }

  /**
   * Retrieves the value corresponding to the specified key. Expired entries are removed if the
   * cleanup period has elapsed.
   *
   * @param key the key whose associated value is to be returned
   * @return the entry corresponding to the key, or {@code null} if no mapping exists
   */
  @Override
  public Entry<V> get(Object key) {
    cleanupExpiredEntries();
    return super.get(key);
  }

  /**
   * Checks if the map contains a mapping for the specified key. Expired entries are removed if the
   * cleanup period has elapsed.
   *
   * @param key the key whose presence is to be tested
   * @return {@code true} if this map contains a mapping for the specified key
   */
  @Override
  public boolean containsKey(Object key) {
    cleanupExpiredEntries();
    return super.containsKey(key);
  }

  /** Checks if the cleanup interval has passed and triggers the cleanup of expired entries. */
  private void cleanupExpiredEntries() {
    if (isCleanupRequired()) {
      removeExpiredEntries();
      updateLastCleanupTime();
    }
  }

  /**
   * Determines if the configured cleanup interval has passed since the last cleanup.
   *
   * @return true if cleanup is needed, false otherwise
   */
  private boolean isCleanupRequired() {
    long currentTime = System.currentTimeMillis();
    return (currentTime - lastCleanupTime >= EXPIRATION_TIME);
  }

  /** Iterates through the map and removes entries that have expired. */
  private void removeExpiredEntries() {
    this.forEach(
        (key, value) -> {
          if (value.isExpired()) {
            this.remove(key);
          }
        });
  }

  /** Updates the timestamp of the last cleanup to the current system time. */
  private void updateLastCleanupTime() {
    lastCleanupTime = System.currentTimeMillis();
  }

  /**
   * Represents an entry in the {@link ExpiringHashMap}, which includes a value and its expiration
   * time.
   *
   * @param <V> the type of the value held in this entry
   */
  public static class Entry<V> {

    @Getter private final V value;
    private final long expirationTime;

    /**
     * Constructs a new entry with the given value and calculates the expiration time based on the
     * default expiration duration.
     *
     * @param value the value to be stored in this entry
     */
    public Entry(V value) {
      this.value = value;
      this.expirationTime = System.currentTimeMillis() + EXPIRATION_TIME;
    }

    /**
     * Checks if the entry has expired based on the current system time.
     *
     * @return {@code true} if the entry is expired, otherwise {@code false}
     */
    public boolean isExpired() {
      return System.currentTimeMillis() > expirationTime;
    }
  }
}
