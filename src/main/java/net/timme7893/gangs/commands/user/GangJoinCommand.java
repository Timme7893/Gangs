package net.timme7893.gangs.commands.user;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.GangCommand;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.invites.GangInvite;
import org.bukkit.entity.Player;

public class GangJoinCommand implements GangCommand {
    @Override
    public String getId() {
        return "join";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, Player player, String[] args) {

        if (gangsPlugin.getPlayerManager().getMember(player) != null) {
            gangsPlugin.getMessageManager().sendMessage("member-in-gang", player);
            return;
        }

        // format: join <name>
        if (args.length != 2) {
            gangsPlugin.getMessageManager().sendMessage("join-form", player);
            return;
        }

        String gangName = args[1];
        if (gangsPlugin.getGangsManager().getGangWithName(gangName) == null) {
            gangsPlugin.getMessageManager().sendMessage("gang-not-exists", player, gangName);
            return;
        }

        Gang gang = gangsPlugin.getGangsManager().getGangWithName(gangName);
        GangInvite gangInvite = gangsPlugin.getGangInviteManager().hasInvite(player, gang);
        if (gangInvite == null) {
            gangsPlugin.getMessageManager().sendMessage("player-not-invited", player, gangName);
            return;
        }

        if (gang.getGangMemberList().size() - 1 >= gang.getMaxPlayersLevel()) {
            gangsPlugin.getMessageManager().sendMessage("gang-is-full", player, gangName);
            return;
        }

        gangInvite.accept();
    }
}
