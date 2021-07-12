package net.timme7893.gangs.listeners;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.members.GMember;
import net.timme7893.gangs.utils.Present;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class GangExpListener implements Listener {

    private GangsPlugin gangsPlugin;

    public GangExpListener(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {

        if (event.isCancelled()) {
            return;
        }

        if (Present.isPlayer(event.getPlayer())) {
            GMember member = gangsPlugin.getPlayerManager().getMember(event.getPlayer());
            member.addBlocksBroken(1);
            if (gangsPlugin.getRiotsManager().applies(event.getBlock().getLocation())) {
                if (gangsPlugin.getRiotsManager().getRiot().hasGangLab(member.getGang())) {
                    member.getGang().addExp(20);
                    member.addExp(20);
                    gangsPlugin.getMessageManager().sendActionBar("exp-gained",event.getPlayer(),20);
                } else {
                    member.getGang().addExp(10);
                    member.addExp(10);
                    gangsPlugin.getMessageManager().sendActionBar("exp-gained",event.getPlayer(),10);
                }
            } else {
                member.getGang().addExp(1);
                member.addExp(1);
                gangsPlugin.getMessageManager().sendActionBar("exp-gained",event.getPlayer(),1);
            }
        }

    }
}
