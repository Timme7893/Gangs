package net.timme7893.gangs.commands.admin;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.AGangCommand;
import net.timme7893.gangs.gangs.GangRole;
import net.timme7893.gangs.gangs.members.GMember;
import net.timme7893.gangs.utils.Present;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GangUpgradeCommand implements AGangCommand {
    @Override
    public String getId() {
        return "upgradeplayer";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, CommandSender sender, String[] args) {

        // agang upgradeplayer <player>

        String targetName = args[1];

        if (Bukkit.getPlayer(targetName) == null) {
            gangsPlugin.getMessageManager().sendMessage("player-not-exists",sender,targetName);
            return;
        }

        Player target = Bukkit.getPlayer(targetName);

        if (!Present.isPlayer(target)) {
            gangsPlugin.getMessageManager().sendMessage("target-not-in-gang",sender,targetName);
            return;
        }

        GMember member = gangsPlugin.getPlayerManager().getMember(target);
        GangRole nextRole = member.getNextRole(member.getGangRole());
        member.setGangRole(nextRole);

        gangsPlugin.getMessageManager().sendMessage("member-promoted",sender,targetName,nextRole.toString());
        gangsPlugin.getMessageManager().sendMessage("player-promoted",target,nextRole.toString());
    }
}
