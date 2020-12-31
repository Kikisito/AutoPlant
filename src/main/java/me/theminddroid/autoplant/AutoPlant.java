package me.theminddroid.autoplant;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import me.theminddroid.autoplant.events.ConfigReload;
import me.theminddroid.autoplant.events.Crops;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class AutoPlant extends JavaPlugin {

    public static StateFlag AUTO_PLANT;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Crops(), this);
        Objects.requireNonNull(getCommand("autoplant")).setExecutor(new ConfigReload());

        int pluginID = 8534;
        Metrics metrics = new Metrics(this,pluginID);

        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }

    @Override
    public void onLoad() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("auto-plant", true);
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
}
