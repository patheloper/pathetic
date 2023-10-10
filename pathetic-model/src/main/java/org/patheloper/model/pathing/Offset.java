package org.patheloper.model.pathing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.patheloper.api.wrapper.PathVector;

import javax.annotation.Nullable;
import java.util.stream.Stream;

@Getter
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
            new OffsetEntry(new PathVector(-1, 0, -1), new CornerCuts(new PathVector(-1, 0, 0), new PathVector(0, 0, -1))),
            new OffsetEntry(new PathVector(-1, 0, 1), new CornerCuts(new PathVector(0, 0, 1), new PathVector(-1, 0, 0))),
            new OffsetEntry(new PathVector(1, 0, -1), new CornerCuts(new PathVector(0, 0, -1), new PathVector(1, 0, 0))),
            new OffsetEntry(new PathVector(1, 0, 1), new CornerCuts(new PathVector(1, 0, 0), new PathVector(0, 0, 1))),
            
            new OffsetEntry(new PathVector(-1, 1, -1), new CornerCuts(new PathVector(-1, 0, 0), new PathVector(0, 1, 0))),
            new OffsetEntry(new PathVector(-1, 1, 0)),
            new OffsetEntry(new PathVector(-1, 1, 1), new CornerCuts(new PathVector(0, 1, 0), new PathVector(-1, 0, 0))),
            new OffsetEntry(new PathVector(0, 1, -1)),
            new OffsetEntry(new PathVector(0, 1, 1)),
            new OffsetEntry(new PathVector(1, 1, -1), new CornerCuts(new PathVector(1, 0, 0), new PathVector(0, 1, 0))),
            new OffsetEntry(new PathVector(1, 1, 0)),
            new OffsetEntry(new PathVector(1, 1, 1), new CornerCuts(new PathVector(1, 0, 0), new PathVector(0, 1, 0))),
            
            new OffsetEntry(new PathVector(-1, -1, -1), new CornerCuts(new PathVector(-1, 0, 0), new PathVector(0, -1, 0))),
            new OffsetEntry(new PathVector(-1, -1, 0)),
            new OffsetEntry(new PathVector(-1, -1, 1), new CornerCuts(new PathVector(0, -1, 0), new PathVector(-1, 0, 0))),
            new OffsetEntry(new PathVector(0, -1, -1)),
            new OffsetEntry(new PathVector(0, -1, 1)),
            new OffsetEntry(new PathVector(1, -1, -1), new CornerCuts(new PathVector(1, 0, 0), new PathVector(0, -1, 0))),
            new OffsetEntry(new PathVector(1, -1, 0)),
            new OffsetEntry(new PathVector(1, -1, 1), new CornerCuts(new PathVector(1, 0, 0), new PathVector(0, -1, 0)))
    }),

    MERGED(Stream.concat(Stream.of(DIAGONAL.getEntries()), Stream.of(VERTICAL_AND_HORIZONTAL.getEntries()))
            .toArray(OffsetEntry[]::new));

    private final OffsetEntry[] entries;

    @Getter
    public final static class OffsetEntry {

        private final PathVector vector;
        private final CornerCuts cornerCuts;
        
        OffsetEntry(PathVector vector) {
            this(vector, null);
        }

        OffsetEntry(PathVector vector, @Nullable CornerCuts cornerCuts) {
            this.vector = vector;
            this.cornerCuts = cornerCuts;
        }
    }
    
    @Getter
    public final static class CornerCuts {
        
        private final PathVector[] cornerCuts;
        
        CornerCuts(PathVector... cornerCuts) {
            this.cornerCuts = cornerCuts;
        }
    }
}
