package net.timme7893.gangs.listeners;

import net.timme7893.gangs.gangs.members.GMember;
import net.timme7893.gangs.utils.Present;
import net.timme7893.gangs.GangsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.timme7893.gangs.commands.admin.GangBypassCommand;

public class PlayerHandlerListener implements Listener {

    private GangsPlugin gangsPlugin;

    public PlayerHandlerListener(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void playerQuit(PlayerQuitEvent event) {
        if (gangsPlugin.getGangChatListener().hasPlayer(event.getPlayer())) {
            gangsPlugin.getGangChatListener().removePlayer(event.getPlayer());
        }
        GangBypassCommand.gangBypassEnabled.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void playerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getWorld().equals(gangsPlugin.getGangHouseManager().getHouseWorld())) {
            if (Present.isPlayer(event.getPlayer())) {
                GMember gMember = gangsPlugin.getPlayerManager().getMember(event.getPlayer());
                event.getPlayer().teleport(gMember.getGang().getGangHouse().getSpawnLoc());
                Bukkit.getScheduler().runTaskLater(gangsPlugin, () -> {
                    gMember.forceBorder();
                }, 20l);
            } else {
                event.getPlayer().chat("/spawn");
            }
        }
    }
}
