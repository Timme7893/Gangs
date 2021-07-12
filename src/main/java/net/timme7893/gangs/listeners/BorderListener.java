package net.timme7893.gangs.listeners;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.members.GMember;
import net.timme7893.gangs.utils.Present;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class BorderListener implements Listener {

    private GangsPlugin gangsPlugin;

    public BorderListener(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void playerWorldSwitch(PlayerChangedWorldEvent event) {

        if (!Present.isPlayer(event.getPlayer())) {
            return;
        }

        if (event.getFrom().equals(gangsPlugin.getGangHouseManager().getHouseWorld())) {
            GMember gMember = gangsPlugin.getPlayerManager().getMember(event.getPlayer());
            gMember.removeBorder();
        }

    }

}
