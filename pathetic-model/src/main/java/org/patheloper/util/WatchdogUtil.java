package org.patheloper.util;

import lombok.experimental.UtilityClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@UtilityClass
public class WatchdogUtil {

    private Class<?> watchdogClazz = null;
    private Method tickMethod = null;

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
                tickMethod.invoke(watchdogClazz);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
