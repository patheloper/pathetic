package xyz.ollieee.api.wrapper;

import lombok.*;

import java.util.UUID;

@Value
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class PathWorld {

    UUID uuid;
    String name;
    Integer minHeight;
    Integer maxHeight;

}
