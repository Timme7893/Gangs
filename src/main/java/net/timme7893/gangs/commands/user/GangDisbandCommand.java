package net.timme7893.gangs.commands.user;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.GangCommand;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.members.GMember;
import org.bukkit.entity.Player;

public class GangDisbandCommand implements GangCommand {

    @Override
    public String getId() {
        return "disband";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, Player player, String[] args) {

        if (gangsPlugin.getPlayerManager().getMember(player) == null) {
            gangsPlugin.getMessageManager().sendMessage("player-not-in-gang",player);
            return;
        }

        if (gangsPlugin.getGangsManager().getGangWithLeader(player.getUniqueId()) == null) {
            gangsPlugin.getMessageManager().sendMessage("member-not-leader",player);
            return;
        }

        GMember gMember = gangsPlugin.getPlayerManager().getMember(player);
        gMember.setGang(null);
        gangsPlugin.getPlayerManager().deleteMember(gMember);
        Gang gang = gangsPlugin.getGangsManager().getGangWithLeader(player.getUniqueId());
        gang.removeMember(gMember);
        gangsPlugin.getGangsManager().deleteGang(gang.getId());
        gangsPlugin.getMessageManager().sendMessage("gang-disbanded",player);
        gangsPlugin.getGangHouseManager().deleteHouse(gang.getGangHouse());

        player.chat("/spawn");

    }
}
