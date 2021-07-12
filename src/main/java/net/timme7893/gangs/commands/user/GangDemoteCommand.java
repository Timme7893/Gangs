package net.timme7893.gangs.commands.user;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.GangCommand;
import net.timme7893.gangs.gangs.GangRole;
import net.timme7893.gangs.gangs.members.GMember;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GangDemoteCommand implements GangCommand {
    @Override
    public String getId() {
        return "demote";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, Player player, String[] args) {
        if (gangsPlugin.getPlayerManager().getMember(player) == null) {
            gangsPlugin.getMessageManager().sendMessage("player-not-in-gang", player);
            return;
        }
        GMember member = gangsPlugin.getPlayerManager().getMember(player);

        // format: demote <name>
        if (args.length != 2) {
            gangsPlugin.getMessageManager().sendMessage("demote-form", player);
            return;
        }

        String targetName = args[1];
        if (Bukkit.getPlayer(targetName) == null) {
            gangsPlugin.getMessageManager().sendMessage("player-not-exists", player, targetName);
            return;
        }
        Player target = Bukkit.getPlayer(targetName);

        if (gangsPlugin.getPlayerManager().getMember(target) == null) {
            gangsPlugin.getMessageManager().sendMessage("target-not-in-gang", player, target.getName());
            return;
        }
        GMember targetMember = gangsPlugin.getPlayerManager().getMember(target);

        if (!member.getGang().equals(targetMember.getGang())) {
            gangsPlugin.getMessageManager().sendMessage("not-same-gang", player, target.getName());
            return;
        }

        GangRole nextRole = targetMember.getLowerRole(targetMember.getGangRole());

        if (targetMember.getGangRole() == GangRole.MEMBER) {
            gangsPlugin.getMessageManager().sendMessage("cant-demote", player);
            return;
        }

        if (targetMember.getGangRole() == GangRole.LEADER) {
            gangsPlugin.getMessageManager().sendMessage("cant-demote-leader", player);
            return;
        }

        targetMember.demote();
        gangsPlugin.getMessageManager().sendMessage("player-demoted", target, nextRole.toString().toLowerCase());
        gangsPlugin.getMessageManager().sendMessage("member-demoted", player, target.getName(),
                nextRole.toString().toLowerCase());
    }
}
