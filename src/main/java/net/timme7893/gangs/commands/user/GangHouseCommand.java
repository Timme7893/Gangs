package net.timme7893.gangs.commands.user;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.GangCommand;
import net.timme7893.gangs.gangs.members.GMember;
import org.bukkit.entity.Player;

public class GangHouseCommand implements GangCommand {
    @Override
    public String getId() {
        return "house";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, Player player, String[] args) {

        if (gangsPlugin.getPlayerManager().getMember(player) == null) {
            gangsPlugin.getMessageManager().sendMessage("player-not-in-gang",player);
            return;
        }

        GMember member = gangsPlugin.getPlayerManager().getMember(player);
        if (member.getGang().getGangHouse() != null) {
            player.teleport(member.getGang().getGangHouse().getSpawnLoc());
            member.initeBorder();
        } else {
            player.sendMessage("error gang house");
        }
    }
}
