package net.timme7893.gangs.commands.user;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.GangCommand;
import net.timme7893.gangs.gangs.GangRole;
import net.timme7893.gangs.gangs.members.GMember;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GangPromoteCommand implements GangCommand {
    @Override
    public String getId() {
        return "promote";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, Player player, String[] args) {

        if (gangsPlugin.getPlayerManager().getMember(player) == null) {
            gangsPlugin.getMessageManager().sendMessage("player-not-in-gang",player);
            return;
        }
        GMember member = gangsPlugin.getPlayerManager().getMember(player);

        // format: promote <name>
        if (args.length != 2) {
            gangsPlugin.getMessageManager().sendMessage("promote-form",player);
            return;
        }

        String targetName = args[1];
        if (Bukkit.getPlayer(targetName) == null) {
            gangsPlugin.getMessageManager().sendMessage("player-not-exists",player,targetName);
            return;
        }
        Player target = Bukkit.getPlayer(targetName);

        if (gangsPlugin.getPlayerManager().getMember(target) == null) {
            gangsPlugin.getMessageManager().sendMessage("target-not-in-gang",player, target.getName());
            return;
        }
        GMember targetMember = gangsPlugin.getPlayerManager().getMember(target);

        if (!member.getGang().equals(targetMember.getGang())) {
            gangsPlugin.getMessageManager().sendMessage("not-same-gang",player, target.getName());
            return;
        }

        GangRole nextRole = targetMember.getNextRole(targetMember.getGangRole());

        if (nextRole == GangRole.LEADER) {
            gangsPlugin.getMessageManager().sendMessage("cant-promote",player);
            return;
        }

        targetMember.promote();
        gangsPlugin.getMessageManager().sendMessage("player-promoted",target, nextRole.toString().toLowerCase());
        gangsPlugin.getMessageManager().sendMessage("member-promoted",player, target.getName(), nextRole.toString().toLowerCase());

    }
}
