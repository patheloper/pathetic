package xyz.ollieee.nms;

import xyz.ollieee.api.snapshot.ChunkSnapshotGrabber;

import xyz.ollieee.nms.v1_15.OneFifteenSnapshotGrabber;
import xyz.ollieee.nms.v1_16.OneSixteenSnapshotGrabber;
import xyz.ollieee.nms.v1_17.OneSeventeenSnapshotGrabber;
import xyz.ollieee.nms.v1_18.OneEighteenSnapshotGrabber;
import xyz.ollieee.nms.v1_19.OneNineteenSnapshotGrabber;

public class NMSUtils {

    private final ChunkSnapshotGrabber chunkSnapshotGrabber;

    public NMSUtils(final String version) {
        switch (version) {
            case "19":
                chunkSnapshotGrabber = new OneNineteenSnapshotGrabber();
                break;
            case "18":
                chunkSnapshotGrabber = new OneEighteenSnapshotGrabber();
                break;
            case "17":
                chunkSnapshotGrabber = new OneSeventeenSnapshotGrabber();
                break;
            case "16":
                chunkSnapshotGrabber = new OneSixteenSnapshotGrabber();
                break;
            case "15":
                chunkSnapshotGrabber = new OneFifteenSnapshotGrabber();
                break;
            default:
                throw new IllegalArgumentException("Unsupported version: " + version);
        }
    }

    public ChunkSnapshotGrabber getChunkSnapshotGrabber() {
        return chunkSnapshotGrabber;
    }
}
