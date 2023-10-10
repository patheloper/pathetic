package org.patheloper.model.pathing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.patheloper.api.wrapper.PathVector;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Offset {

    VERTICAL_AND_HORIZONTAL(new OffsetEntry[] {
            new OffsetEntry(vec3(1, 0, 0)),
            new OffsetEntry(vec3(-1, 0, 0)),
            new OffsetEntry(vec3(0, 0, 1)),
            new OffsetEntry(vec3(0, 0, -1)),
            new OffsetEntry(vec3(0, 1, 0)),
            new OffsetEntry(vec3(0, -1, 0)),
    }),
    
    DIAGONAL(new OffsetEntry[] {
            new OffsetEntry(vec3(-1, 0, -1), cut(vec3(-1, 0, 0)), cut(vec3(0, 0, -1))),
            new OffsetEntry(vec3(-1, 0, 0), cut(vec3(-1, 0, 0)), cut(vec3(0, 0, 0))),
            new OffsetEntry(vec3(-1, 0, 1), cut(vec3(0, 0, 1)), cut(vec3(-1, 0, 0))),
            new OffsetEntry(vec3(0, 0, -1), cut(vec3(0, 0, -1)), cut(vec3(0, 0, 0))),
            new OffsetEntry(vec3(0, 0, 0), cut(vec3(0, 0, 0)), cut(vec3(0, 0, 0))),
            new OffsetEntry(vec3(0, 0, 1), cut(vec3(0, 0, 1)), cut(vec3(0, 0, 0))),
            new OffsetEntry(vec3(1, 0, -1), cut(vec3(0, 0, -1)), cut(vec3(1, 0, 0))),
            new OffsetEntry(vec3(1, 0, 0), cut(vec3(1, 0, 0)), cut(vec3(0, 0, 0))),
            new OffsetEntry(vec3(1, 0, 1), cut(vec3(1, 0, 0)), cut(vec3(0, 0, 1))),

            new OffsetEntry(vec3(-1, 1, -1), cut(vec3(-1, 0, 0), vec3(-1, 0, 1)), cut(vec3(0, 1, 0), vec3(0, 1, 1))), // Corner
            new OffsetEntry(vec3(-1, 1, 0), cut(vec3(0, 1, 0)), cut(vec3(-1, 0, 0))),
            new OffsetEntry(vec3(-1, 1, 1), cut(vec3(0, 1, 0)), cut(vec3(-1, 0, 0))), // Corner
            new OffsetEntry(vec3(0, 1, -1), cut(vec3(0, 0, -1)), cut(vec3(0, 1, 0))),
            new OffsetEntry(vec3(0, 1, 0), cut(vec3(0, 1, 0)), cut(vec3(0, 0, 0))),
            new OffsetEntry(vec3(0, 1, 1), cut(vec3(0, 1, 0)), cut(vec3(0, 0, 0))),
            new OffsetEntry(vec3(1, 1, -1), cut(vec3(1, 0, 0)), cut(vec3(0, 1, 0))), // Corner
            new OffsetEntry(vec3(1, 1, 0), cut(vec3(1, 0, 0)), cut(vec3(0, 1, 0))),
            new OffsetEntry(vec3(1, 1, 1), cut(vec3(1, 0, 0)), cut(vec3(0, 1, 0))), // Corner

            new OffsetEntry(vec3(-1, -1, -1), cut(vec3(-1, 0, 0)), cut(vec3(0, -1, 0))), // Corner
            new OffsetEntry(vec3(-1, -1, 0), cut(vec3(0, -1, 0)), cut(vec3(-1, 0, 0))),
            new OffsetEntry(vec3(-1, -1, 1), cut(vec3(0, -1, 0)), cut(vec3(-1, 0, 0))), // Corner
            new OffsetEntry(vec3(0, -1, -1), cut(vec3(0, 0, -1)), cut(vec3(0, -1, 0))),
            new OffsetEntry(vec3(0, -1, 0), cut(vec3(0, -1, 0)), cut(vec3(0, 0, 0))),
            new OffsetEntry(vec3(0, -1, 1), cut(vec3(0, -1, 0)), cut(vec3(0, 0, 0))),
            new OffsetEntry(vec3(1, -1, -1), cut(vec3(1, 0, 0)), cut(vec3(0, -1, 0))), // Corner
            new OffsetEntry(vec3(1, -1, 0), cut(vec3(1, 0, 0)), cut(vec3(0, -1, 0))),
            new OffsetEntry(vec3(1, -1, 1), cut(vec3(1, 0, 0)), cut(vec3(0, -1, 0))) // Corner
    }),

    MERGED(Stream.concat(Stream.of(DIAGONAL.getEntries()), Stream.of(VERTICAL_AND_HORIZONTAL.getEntries()))
            .toArray(OffsetEntry[]::new));

    private final OffsetEntry[] entries;

    @Getter
    public final static class OffsetEntry {

        private final PathVector vector;
        private final CornerCut[] cornerCuts;
        
        OffsetEntry(PathVector vector) {
            this(vector, (CornerCut) null);
        }

        OffsetEntry(PathVector vector, CornerCut... cornerCuts) {
            this.vector = vector;
            this.cornerCuts = cornerCuts;
        }
    }
    
    @Getter
    public final static class CornerCut {
        
        private final PathVector[] vectors;
        
        CornerCut(PathVector... cornerCuts) {
            this.vectors = cornerCuts;
        }
    }

    private static CornerCut cut(PathVector... vecs) {
        return new CornerCut(vecs);
    }
    
    private static PathVector vec3(final int x, final int y, final int z) {
        return new PathVector(x, y, z);
    }
}
