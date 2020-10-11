package me.theminddroid.autoplant;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import me.theminddroid.autoplant.events.Crops;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutoPlant extends JavaPlugin {

    public static StateFlag AUTO_PLANT;

    @Override
    public void onEnable() {
        System.out.println("The plugin has started...");
        getServer().getPluginManager().registerEvents(new Crops(), this);

        int pluginID = 8534;
        Metrics metrics = new Metrics(this,pluginID);

        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }

    @Override
    public void onLoad() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("auto-plant", false);
            registry.register(flag);
            AUTO_PLANT = flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("auto-plant");
            if (existing instanceof StateFlag) {
                AUTO_PLANT = (StateFlag) existing;
            } else {
                getLogger().severe("Unable to load flag.");
                throw e;
            }
        }
    }

    public static AutoPlant getInstance() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("AutoPlant");
        if (!(plugin instanceof AutoPlant)) {
            throw new RuntimeException("'AutoPlant' not found. 'AutoPlant' plugin disabled?");
        }
        return ((AutoPlant) plugin);
    }

    @Override
    public void onDisable() {
        System.out.println("The plugin is shutting down...");
    }
}
