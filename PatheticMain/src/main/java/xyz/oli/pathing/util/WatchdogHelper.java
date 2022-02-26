package xyz.oli.pathing.util;

import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WatchdogHelper {

    private static Class<?> watchdogClazz = null;
    private static Method tickMethod = null;

    static {
        try {
            watchdogClazz = Class.forName("org.spigotmc.WatchdogThread");
            tickMethod = watchdogClazz.getDeclaredMethod("tick");
        }catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void tickWatchdog() {
        if (Bukkit.isPrimaryThread() && tickMethod != null && watchdogClazz != null) {
            try {
                tickMethod.invoke(watchdogClazz, null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
