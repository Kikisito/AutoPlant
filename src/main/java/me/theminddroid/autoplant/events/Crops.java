package me.theminddroid.autoplant.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.theminddroid.autoplant.AutoPlant;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.EnumSet;
import java.util.Set;

public class Crops implements Listener {

    private final Set<Material> cropList = EnumSet.of(Material.WHEAT, Material.POTATOES, Material.CARROTS, Material.COCOA, Material.BEETROOTS, Material.NETHER_WART);

    @EventHandler
    public void cropBroken(BlockBreakEvent event) {

        Player player = event.getPlayer();
        Block block = event.getBlock();

        final com.sk89q.worldedit.util.Location worldGuardLocation = BukkitAdapter.adapt(block.getLocation());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get((World) worldGuardLocation.getExtent());

        if (regions == null) {
            Bukkit.getLogger().finer("WorldGuard failed to return region manager for world.");
            return;
        }

        ApplicableRegionSet set = regions.getApplicableRegions(worldGuardLocation.toVector().toBlockPoint());
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        // Disabled in region
        if (!set.testState(localPlayer, AutoPlant.AUTO_PLANT)) return;
        if (player.hasPermission("autoplant.bypass")) return;

        handleCrop(event, player, block);
    }


    private void handleCrop(BlockBreakEvent event , Player player, Block block) {
        BlockData blockData = block.getBlockData();
        Material material = block.getType();

        if(!(blockData instanceof Ageable)) return;
        if(!cropList.contains(material)) return;
        if(!player.getInventory().getItemInMainHand().getType().name().endsWith("_HOE")) return;

        Ageable age = (Ageable) blockData;
        if(age.getAge() != age.getMaximumAge()) {
            event.setCancelled(true);
            return;
        }

        Bukkit.getScheduler().runTaskLater(AutoPlant.getInstance(), () -> {
            block.setType(material);

            BlockData newBlockData = block.getBlockData();
            if (blockData instanceof Directional && newBlockData instanceof Directional) {
                Directional newDirectional = (Directional) newBlockData;
                Directional oldDirectional = (Directional) blockData;

                newDirectional.setFacing(oldDirectional.getFacing());
            }
            block.setBlockData(newBlockData);
        }, 1);
    }
}