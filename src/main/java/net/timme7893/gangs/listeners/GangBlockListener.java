package net.timme7893.gangs.listeners;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.utils.Present;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class GangBlockListener implements Listener {

    private GangsPlugin gangsPlugin;

    public GangBlockListener(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.SLIME_BLOCK || event.getBlock().getType() == Material.SEA_LANTERN
                || event.getBlock().getType() == Material.PRISMARINE || event.getBlock().getType() == Material.STONE) {
            if (Present.isPlayer(event.getPlayer())) {
                Gang gang = getGang(event.getPlayer());
                if (gang.getGangHouse().getGangMine().getMinutesSinceBreak() == -1) {
                    gang.getGangHouse().getGangMine().setMinutesSinceBreak(0);
                }
            }
        }
        if (event.getBlock().getType() == Material.SLIME_BLOCK) {
            if (Present.isPlayer(event.getPlayer())) {
                Gang gang = getGang(event.getPlayer());
                gang.setSlimeBlocks(gang.getSlimeBlocks() + 1);
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
            }
        } else if (event.getBlock().getType() == Material.SEA_LANTERN) {
            if (Present.isPlayer(event.getPlayer())) {
                Gang gang = getGang(event.getPlayer());
                gang.setLanternBlocks(gang.getLanternBlocks() + 1);
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
            }
        } else if (event.getBlock().getType() == Material.PRISMARINE) {
            if (Present.isPlayer(event.getPlayer())) {
                Gang gang = getGang(event.getPlayer());
                gang.setPrismarineBlocks(gang.getPrismarineBlocks() + 1);
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
            }
        }

    }

    public Gang getGang(Player player) {
        return gangsPlugin.getPlayerManager().getMember(player).getGang();
    }
}
