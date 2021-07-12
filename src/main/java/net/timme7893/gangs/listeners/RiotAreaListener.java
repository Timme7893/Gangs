package net.timme7893.gangs.listeners;

import net.timme7893.gangs.GangsPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class RiotAreaListener implements Listener {

    private GangsPlugin gangsPlugin;
    public HashMap<Player, Location> regionMake = new HashMap<Player, Location>();
    public ArrayList<Player> creatingRegion = new ArrayList<>();

    public RiotAreaListener(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        if (creatingRegion.contains(event.getPlayer())) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (regionMake.containsKey(event.getPlayer())) {
                    // set location 2
                    Location loc2 = event.getClickedBlock().getLocation();
                    Location loc1 = regionMake.get(event.getPlayer());
                    gangsPlugin.getRiotsManager().saveArea(loc1,loc2);
                    gangsPlugin.getMessageManager().sendMessage("riots-area-loc2", event.getPlayer());
                    regionMake.remove(event.getPlayer());
                    creatingRegion.remove(event.getPlayer());
                    event.setCancelled(true);
                } else {
                    // set location 1
                    Location loc1 = event.getClickedBlock().getLocation();
                    regionMake.put(event.getPlayer(),loc1);
                    gangsPlugin.getMessageManager().sendMessage("riots-area-loc1",event.getPlayer());
                    event.setCancelled(true);
                }
            }

        }
    }
}
