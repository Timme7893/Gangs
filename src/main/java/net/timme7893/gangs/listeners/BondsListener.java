package net.timme7893.gangs.listeners;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.members.GMember;
import net.timme7893.gangs.utils.Present;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class BondsListener implements Listener {

    private GangsPlugin gangsPlugin;

    public BondsListener(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
    }

    @EventHandler
    public void hitPlayer(EntityDamageByEntityEvent event) {

        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player hitter = (Player) event.getDamager();
            Player player = (Player) event.getEntity();

            if (Present.isPlayer(hitter) && Present.isPlayer(player)) {
                GMember hitMember = gangsPlugin.getPlayerManager().getMember(hitter);
                GMember member = gangsPlugin.getPlayerManager().getMember(player);

                if (hitMember.getGang().equals(member.getGang())) {
                    event.setCancelled(true);
                }

                /*if (hitMember.getGang().isAlly(member.getGang())) {
                    event.setCancelled(true);
                }*/
            }
        }

    }
}
