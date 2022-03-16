package xyz.ollieee.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import xyz.ollieee.api.event.PathingEvent;
import xyz.ollieee.Pathetic;

@UtilityClass
public class EventUtil {
 
    public void callEvent(PathingEvent pathingEvent) {
        
        if(!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(Pathetic.getPluginInstance(), () -> Bukkit.getPluginManager().callEvent(pathingEvent));
            return;
        }
    
        Bukkit.getPluginManager().callEvent(pathingEvent);
    }
    
}
