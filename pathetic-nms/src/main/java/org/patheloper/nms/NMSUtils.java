package org.patheloper.nms;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.patheloper.api.snapshot.NMSInterface;

public class NMSUtils {

    private final NMSInterface nmsInterface;

    public NMSUtils() {
        String nmsVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        if (supportsAsyncLoading()) {
            nmsInterface = new NMSInterface_Paper();
        } else {
            try {
                nmsInterface =
                        (NMSInterface) Class.forName(getClass().getPackage().getName() + ".NMSInterface_" + nmsVersion)
                                .getConstructor()
                                .newInstance();
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Unsupported version: " + nmsVersion, e);
            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException("Could not create NMSInterface for version " + nmsVersion, e);
            }
        }
        System.out.println("Used NMS interface: " + nmsInterface);
    }

    public NMSInterface getNmsInterface() {
        return this.nmsInterface;
    }

    private boolean supportsAsyncLoading() {
        try {
            World.class.getDeclaredMethod("getChunkAtAsync", int.class, int.class, boolean.class, boolean.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
