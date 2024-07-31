package org.patheloper.util;

import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;

public class ExpiringHashMap<K, V> extends ConcurrentHashMap<K, ExpiringHashMap.Entry<V>> {

  private static final long EXPIRATION_TIME = 5 * 60 * 1000;

  @Override
  public Entry<V> put(K key, Entry<V> value) {










    return super.put(key, value);
  }

  @Override
  public Entry<V> get(Object key) {










    Entry<V> entry = super.get(key);
    if (entry != null) {
      if (entry.isExpired()) {
        super.remove(key);
        return null;
      } else {
        return entry;
      }
    }
    return null;
  }

  @Override
  public boolean containsKey(Object key) {










    Entry<V> entry = super.get(key);
    if (entry != null) {
      if (entry.isExpired()) {
        super.remove(key);
        return false;
      } else {
        return true;
      }
    }
    return false;
  }

  public static class Entry<V> {

    @Getter
    private final V value;
    private final long expirationTime;

    public Entry(V value) {










      this.value = value;
      this.expirationTime = System.currentTimeMillis() + EXPIRATION_TIME;
    }

    public boolean isExpired() {










      return System.currentTimeMillis() > expirationTime;
    }
  }
}
