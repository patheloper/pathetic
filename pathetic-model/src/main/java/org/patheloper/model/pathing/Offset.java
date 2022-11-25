package org.patheloper.model.pathing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.patheloper.api.wrapper.PathVector;

import java.util.stream.Stream;

@AllArgsConstructor
public enum Offset {

    VERTICAL_AND_HORIZONTAL(new PathVector[] {
            new PathVector(1, 0, 0),
            new PathVector(-1, 0, 0),
            new PathVector(0, 0, 1),
            new PathVector(0, 0, -1),
            new PathVector(0, 1, 0),
            new PathVector(0, -1, 0),
    }),
    DIAGONAL(new PathVector[] {
            new PathVector(1, 0, 1), new PathVector(-1, 0, -1), new PathVector(-1, 0, 1), new PathVector(1, 0, -1),
            new PathVector(0, 1, 1), new PathVector(1, 1, 1), new PathVector(1, 1, 0), new PathVector(1, 1, -1),
            new PathVector(0, 1, -1), new PathVector(-1, 1, -1), new PathVector(-1, 1, 0), new PathVector(-1, 1, 1),
            new PathVector(0, -1, 1), new PathVector(1, -1, 1), new PathVector(1, -1, 0), new PathVector(1, -1, -1),
            new PathVector(0, -1, -1), new PathVector(-1, -1, -1), new PathVector(-1, -1, 0), new PathVector(-1, -1, 1),
    }),
    MERGED(Stream.concat(Stream.of(VERTICAL_AND_HORIZONTAL.getVectors()), Stream.of(DIAGONAL.getVectors()))
            .toArray(PathVector[]::new));

    @Getter
    private final PathVector[] vectors;

}
