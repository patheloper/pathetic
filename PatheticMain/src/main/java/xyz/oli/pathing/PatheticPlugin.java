package xyz.oli.pathing;

import org.bukkit.plugin.java.JavaPlugin;

public class PatheticPlugin extends JavaPlugin {
    
    @Override
    public void onLoad() {
        Pathetic.initialize(this);
    }
}
