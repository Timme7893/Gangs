package net.timme7893.gangs.listeners;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.GangRole;
import net.timme7893.gangs.gangs.members.GMember;
import net.timme7893.gangs.utils.Present;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import net.timme7893.gangs.commands.admin.GangBypassCommand;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class GangHouseListener implements Listener {

    private GangsPlugin gangsPlugin;
    private World gangHouseWorld;

    public GangHouseListener(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
        this.gangHouseWorld = gangsPlugin.getGangHouseManager().getHouseWorld();
    }

    // Lets ignore cancelled events, just to make sure we don't uncancel stuff that
    // has been cancelled purposely by other plugins.
    @EventHandler(ignoreCancelled = true)
    public void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().getWorld().equals(gangHouseWorld)) {
            if (GangBypassCommand.gangBypassEnabled.contains(player.getUniqueId())) {
                if (!player.hasPermission("gangs.admin")) {
                    // Only reachable if player enabled it, got demoted whilst he/she was online,
                    // and didn't relog in the meantime. Just as a 'fail-safe'
                    event.setCancelled(true);
                    GangBypassCommand.gangBypassEnabled.remove(player.getUniqueId());
                    return;
                }
                event.setCancelled(false);
                return;
            }
            if (Present.isPlayer(player)) {
                GMember member = gangsPlugin.getPlayerManager().getMember(player);
                if (event.getBlock().getLocation().distance(member.getGang().getGangHouse().getSpawnLoc()) >= 150) {
                    event.setCancelled(true);
                } else if (member.getGangRole() == GangRole.MEMBER) {
                    event.setCancelled(true);
                }
            } else {
                // user is not in a gang -> so can't break blocks.
                event.setCancelled(true);
            }
        }
    }

    // Lets ignore cancelled events, just to make sure we don't uncancel stuff that
    // has been cancelled purposely by other plugins.
    @EventHandler(ignoreCancelled = true)
    public void blockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().getWorld().equals(gangHouseWorld)) {
            if (GangBypassCommand.gangBypassEnabled.contains(player.getUniqueId())) {
                if (!player.hasPermission("gangs.admin")) {
                    // Only reachable if player enabled it, got demoted whilst he/she was online,
                    // and didn't relog in the meantime. Just as a 'fail-safe'
                    event.setCancelled(true);
                    GangBypassCommand.gangBypassEnabled.remove(player.getUniqueId());
                    return;
                }
                event.setCancelled(false);
                return;
            }
            if (Present.isPlayer(player)) {
                GMember member = gangsPlugin.getPlayerManager().getMember(player);
                if (event.getBlock().getLocation().distance(member.getGang().getGangHouse().getSpawnLoc()) >= 150) {
                    event.setCancelled(true);
                } else if (member.getGangRole() == GangRole.MEMBER) {
                    event.setCancelled(true);
                }
            } else {
                // user is not in a gang -> so can't place blocks.
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void openChest(InventoryOpenEvent event) {
        if (event.getInventory().getName().equals("container.chest") && event.getPlayer().getLocation().getWorld().equals(gangHouseWorld)) {
            if (event.getPlayer() instanceof Player) {
                Player player = (Player) event.getPlayer();


                if (GangBypassCommand.gangBypassEnabled.contains(player.getUniqueId())) {
                    if (!player.hasPermission("gangs.admin")) {
                        // Only reachable if player enabled it, got demoted whilst he/she was online,
                        // and didn't relog in the meantime. Just as a 'fail-safe'
                        event.setCancelled(true);
                        GangBypassCommand.gangBypassEnabled.remove(player.getUniqueId());
                        return;
                    }
                    event.setCancelled(false);
                    return;
                }


                if (Present.isPlayer(player)) {
                    GMember gMember = gangsPlugin.getPlayerManager().getMember(player);
                    if (event.getPlayer().getLocation().distance(gMember.getGang().getGangHouse().getSpawnLoc()) >= 150) {
                        event.setCancelled(true);
                    }
                } else {
                    player.chat("/spawn");
                    event.setCancelled(true);
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if(event.getLocation().getWorld().equals(gangHouseWorld)) {
            if(event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
                event.setCancelled(true);
            }
        }
    }

}
