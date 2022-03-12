package xyz.oli.pathing;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.oli.Pathetic;

public class PatheticPlugin extends JavaPlugin {
    
    @Override
    public void onLoad() {
        Pathetic.initialize(this);
    }
}
