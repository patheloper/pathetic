package xyz.oli.pathing.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@UtilityClass
public class WatchdogHelper {

    private Class<?> watchdogClazz;
    private Method tickMethod;

    static {
        try {
            watchdogClazz = Class.forName("org.spigotmc.WatchdogThread");
            tickMethod = watchdogClazz.getDeclaredMethod("tick");
        }catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void tickWatchdog() {
        if (tickMethod != null && watchdogClazz != null) {
            try {
                tickMethod.invoke(watchdogClazz, null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
