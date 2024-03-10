package org.patheloper.util;

import java.util.HashMap;

public class ExpiringHashMap<K, V> extends HashMap<K, V> {

  private static final long EXPIRATION_TIME = 5 * 60 * 1000;

  private final HashMap<K, Long> expirationMap = new HashMap<>();

  @Override
  public V put(K key, V value) {
    expirationMap.put(key, System.currentTimeMillis() + EXPIRATION_TIME);
    return super.put(key, value);
  }

  @Override
  public V get(Object key) {
    if (isExpired(key)) {
      super.remove(key);
      expirationMap.remove(key);
      return null;
    }
    return super.get(key);
  }

  @Override
  public boolean containsKey(Object key) {
    if (isExpired(key)) {
      super.remove(key);
      expirationMap.remove(key);
      return false;
    }
    return super.containsKey(key);
  }

  private boolean isExpired(Object key) {
    Long expirationTime = expirationMap.get(key);
    return expirationTime != null && expirationTime < System.currentTimeMillis();
  }
}
