package org.patheloper.nms;

import org.bukkit.Bukkit;
import org.patheloper.api.snapshot.NMSInterface;

public class NMSUtils {

    private static final Class<?>[] supportedVersions = new Class[]{
            NMSInterfacev1_8_R3.class,
            NMSInterfacev1_12_R1.class,
            NMSInterfacev1_15_R1.class,
            NMSInterfacev1_16_R3.class,
            NMSInterfacev1_17_R1.class,
            NMSInterfacev1_18_R1.class,
            NMSInterfacev1_19_R2.class,
            NMSInterfacev1_19_R3.class
    };

    private final NMSInterface nmsInterface;

    public NMSUtils() {
        String nmsVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        try {
            for (Class<?> clazz : supportedVersions) {
                if (clazz.getSimpleName().endsWith(nmsVersion)) {
                    nmsInterface = (NMSInterface) clazz.getConstructor().newInstance();
                    return;
                }
            }
            throw new IllegalArgumentException("Unsupported version: " + nmsVersion);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Could not create NMSInterface for version " + nmsVersion, e);
        }
    }

    public NMSInterface getNmsInterface() {
        return this.nmsInterface;
    }
}
