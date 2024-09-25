package org.patheloper.api.wrapper;

import java.util.UUID;
import lombok.Value;

/** Represents a pathing environment which attributes */
@Value
public class PathEnvironment {

  UUID uuid;
  String name;
  Integer minHeight;
  Integer maxHeight;

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (!(o instanceof PathEnvironment)) return false;

    PathEnvironment other = (PathEnvironment) o;
    if (this.name.length() != other.name.length()) return false; // early exit

    return this.name.equals(other.name);
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + this.name.hashCode();
    return result;
  }
}
