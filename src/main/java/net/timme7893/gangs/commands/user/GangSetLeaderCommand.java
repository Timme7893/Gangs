package net.timme7893.gangs.commands.user;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.GangCommand;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.GangRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GangSetLeaderCommand implements GangCommand {
    @Override
    public String getId() {
        return "setleader";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, Player player, String[] args) {

        if (gangsPlugin.getPlayerManager().getMember(player) == null) {
            gangsPlugin.getMessageManager().sendMessage("player-not-in-gang", player);
            return;
        }

        if (gangsPlugin.getGangsManager().getGangWithLeader(player.getUniqueId()) == null) {
            gangsPlugin.getMessageManager().sendMessage("member-not-leader", player);
            return;
        }

        // format: setleader <name>
        if (args.length != 2) {
            gangsPlugin.getMessageManager().sendMessage("setleader-form", player);
            return;
        }

        String newLeaderName = args[1];
        if (Bukkit.getPlayer(newLeaderName) == null) {
            gangsPlugin.getMessageManager().sendMessage("player-not-exists", player, newLeaderName);
            return;
        }

        Player newLeader = Bukkit.getPlayer(newLeaderName);
        Gang gang = gangsPlugin.getGangsManager().getGangWithLeader(player.getUniqueId());
        gang.setGangLeader(newLeader.getUniqueId());
        gangsPlugin.getPlayerManager().getMember(newLeader).setGangRole(GangRole.LEADER);
        gangsPlugin.getPlayerManager().getMember(player).setGangRole(GangRole.ADMIN);
        gangsPlugin.getMessageManager().sendMessage("player-now-leader", newLeader, gang.getGangName());
        gangsPlugin.getMessageManager().sendMessage("player-now-admin", player, newLeader.getName());
    }
}
