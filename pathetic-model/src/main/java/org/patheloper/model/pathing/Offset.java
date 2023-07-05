package org.patheloper.model.pathing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.patheloper.api.wrapper.PathVector;

import java.util.stream.Stream;

@AllArgsConstructor
public enum Offset {

    VERTICAL_AND_HORIZONTAL(new OffsetEntry[] {
            new OffsetEntry(new PathVector(1, 0, 0)),
            new OffsetEntry(new PathVector(-1, 0, 0)),
            new OffsetEntry(new PathVector(0, 0, 1)),
            new OffsetEntry(new PathVector(0, 0, -1)),
            new OffsetEntry(new PathVector(0, 1, 0)),
            new OffsetEntry(new PathVector(0, -1, 0)),
    }),

    DIAGONAL(new OffsetEntry[] {
            new OffsetEntry(new PathVector(1, 0, 1), new PathVector(1, 0, 0), new PathVector(0, 0, 1)),
            new OffsetEntry(new PathVector(-1, 0, -1), new PathVector(-1, 0, 0), new PathVector(0, 0, -1)),
            new OffsetEntry(new PathVector(-1, 0, 1), new PathVector(0, 0, 1), new PathVector(-1, 0, 0)),
            new OffsetEntry(new PathVector(1, 0, -1), new PathVector(0, 0, -1), new PathVector(1, 0, 0)),

            new OffsetEntry(new PathVector(0, 1, 1), new PathVector(0, 0, 1), new PathVector(0, 1, 0)),
            new OffsetEntry(new PathVector(1, 1, 1)),
            new OffsetEntry(new PathVector(1, 1, 0), new PathVector(1, 0, 0), new PathVector(0, 1, 0)),
            new OffsetEntry(new PathVector(1, 1, -1)),
            new OffsetEntry(new PathVector(0, 1, -1), new PathVector(0, 0, -1), new PathVector(0, 1, 0)),
            new OffsetEntry(new PathVector(-1, 1, -1)),
            new OffsetEntry(new PathVector(-1, 1, 0), new PathVector(0, 1, 0), new PathVector(-1, 0, 0)),
            new OffsetEntry(new PathVector(-1, 1, 1)),
            new OffsetEntry(new PathVector(0, -1, 1), new PathVector(0, 0, 1), new PathVector(0, -1, 0)),
            new OffsetEntry(new PathVector(1, -1, 1)),
            new OffsetEntry(new PathVector(1, -1, 0), new PathVector(1, 0, 0), new PathVector(0, -1, 0)),
            new OffsetEntry(new PathVector(1, -1, -1)),
            new OffsetEntry(new PathVector(0, -1, -1), new PathVector(0, 0, -1), new PathVector(0, -1, 0)),
            new OffsetEntry(new PathVector(-1, -1, -1)),
            new OffsetEntry(new PathVector(-1, -1, 0), new PathVector(0, -1, 0), new PathVector(-1, 0, 0)),
            new OffsetEntry(new PathVector(-1, -1, 1)),
    }),

    MERGED(Stream.concat(Stream.of(VERTICAL_AND_HORIZONTAL.getOffsets()), Stream.of(DIAGONAL.getOffsets()))
            .toArray(OffsetEntry[]::new));

    @Getter
    private final OffsetEntry[] offsets;

    public final static class OffsetEntry {

        @Getter
        private final PathVector vector;
        @Getter
        private final PathVector[] cornerCuts;

        OffsetEntry(PathVector vector, PathVector... cornerCuts) {
            this.vector = vector;
            this.cornerCuts = cornerCuts;
        }

    }

}
