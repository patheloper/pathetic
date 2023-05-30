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
    }

    public NMSInterface getNmsInterface() {
        return this.nmsInterface;
    }

    private boolean supportsAsyncLoading() {
        try {
            World.class.getDeclaredMethod("getChunkAtAsyncUrgently", int.class, int.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
