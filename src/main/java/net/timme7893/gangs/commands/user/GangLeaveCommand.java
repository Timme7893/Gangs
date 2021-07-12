package net.timme7893.gangs.commands.user;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.GangCommand;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.GangRole;
import net.timme7893.gangs.gangs.members.GMember;
import org.bukkit.entity.Player;

public class GangLeaveCommand implements GangCommand {
    @Override
    public String getId() {
        return "leave";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, Player player, String[] args) {

        if (gangsPlugin.getPlayerManager().getMember(player) == null) {
            gangsPlugin.getMessageManager().sendMessage("player-not-in-gang",player);
            return;
        }

        GMember member = gangsPlugin.getPlayerManager().getMember(player);

        if (member.getGangRole() == GangRole.LEADER) {
            gangsPlugin.getMessageManager().sendMessage("player-leave-leader",player);
            return;
        }

        Gang gang = member.getGang();
        gang.removeMember(member);
        member.setGang(null);
        gangsPlugin.getPlayerManager().deleteMember(member);
        gangsPlugin.getMessageManager().sendMessage("player-gang-left",player);
    }
}
