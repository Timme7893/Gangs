package net.timme7893.gangs.listeners;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.members.GMember;
import net.timme7893.gangs.gangs.riots.GangLab;
import net.timme7893.gangs.gangs.riots.RiotsManager;
import net.timme7893.gangs.utils.Present;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class RiotUtilListeners implements Listener {

    private GangsPlugin gangsPlugin;
    private ItemStack gangLab;
    private RiotsManager riotsManager;

    public RiotUtilListeners(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
        this.gangLab = gangsPlugin.getRiotsManager().getGangLab();
        this.riotsManager = gangsPlugin.getRiotsManager();
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void blockPlace(BlockPlaceEvent event) {
        if (riotsManager.isActive()) {
            if (riotsManager.applies(event.getBlock().getLocation())) {

                if (!Present.isPlayer(event.getPlayer())) {
                    return;
                }

                if (Present.isSimilar(event.getItemInHand(),gangLab)) {

                    // item confirmed = gang lab
                    GMember member = gangsPlugin.getPlayerManager().getMember(event.getPlayer());
                    riotsManager.getRiot().addGangLab(event.getBlock(),member.getGang());
                    riotsManager.announceGangLab(event.getPlayer());
                }

            }
        }
    }

    @EventHandler
    public void blockRemove(BlockBreakEvent event) {
        if (riotsManager.isActive()) {
            if (riotsManager.applies(event.getBlock().getLocation())) {
                if (riotsManager.getRiot().getGangLabByBlock(event.getBlock()) != null) {
                    GangLab gangLab = riotsManager.getRiot().getGangLabByBlock(event.getBlock());
                    riotsManager.getRiot().removeGangLab(gangLab.getBlock());
                }
            }
        }
    }
}
