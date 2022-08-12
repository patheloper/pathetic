package xyz.ollieee.nms;

import xyz.ollieee.api.snapshot.NMSInterface;
import xyz.ollieee.nms.v1_12.OneTwelveNMSInterface;
import xyz.ollieee.nms.v1_15.OneFifteenNMSInterface;
import xyz.ollieee.nms.v1_16.OneSixteenNMSInterface;
import xyz.ollieee.nms.v1_17.OneSeventeenNMSInterface;
import xyz.ollieee.nms.v1_18.OneEighteenNMSInterface;
import xyz.ollieee.nms.v1_19.OneNineteenNMSInterface;
import xyz.ollieee.nms.v1_8.OneEightNMSInterface;

public class NMSUtils {

    private final NMSInterface nmsInterface;

    public NMSUtils(int version) {
        switch (version) {
            case 19:
                nmsInterface = new OneNineteenNMSInterface();
                break;
            case 18:
                nmsInterface = new OneEighteenNMSInterface();
                break;
            case 17:
                nmsInterface = new OneSeventeenNMSInterface();
                break;
            case 16:
                nmsInterface = new OneSixteenNMSInterface();
                break;
            case 15:
                nmsInterface = new OneFifteenNMSInterface();
                break;
            case 12:
                nmsInterface = new OneTwelveNMSInterface();
                break;
            case 8:
                nmsInterface = new OneEightNMSInterface();
                break;
            default:
                throw new IllegalArgumentException("Unsupported version: " + version);
        }
    }

    public NMSInterface getNmsInterface() {
        return this.nmsInterface;
    }


}
