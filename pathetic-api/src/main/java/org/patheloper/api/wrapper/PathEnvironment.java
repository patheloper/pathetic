package org.patheloper.api.wrapper;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
/** Represents the pathing environment */
public class PathEnvironment {

  UUID uuid;
  String name;
  @EqualsAndHashCode.Exclude Integer minHeight;
  @EqualsAndHashCode.Exclude Integer maxHeight;
}
