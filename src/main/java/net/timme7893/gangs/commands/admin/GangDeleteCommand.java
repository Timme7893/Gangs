package net.timme7893.gangs.commands.admin;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.AGangCommand;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.members.GMember;
import org.bukkit.command.CommandSender;

public class GangDeleteCommand implements AGangCommand {
    @Override
    public String getId() {
        return "delete";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, CommandSender sender, String[] args) {

        if (args.length < 2) {
            gangsPlugin.getMessageManager().sendMessage("use-delete-gang",sender);
            return;
        }

        String gangName = args[1];
        if (gangsPlugin.getGangsManager().getGangWithName(gangName) == null) {
            gangsPlugin.getMessageManager().sendMessage("gang-unknown",sender,gangName);
            return;
        }

        Gang gangToDelete = gangsPlugin.getGangsManager().getGangWithName(gangName);

        gangsPlugin.getMessageManager().sendMessage("gang-deleted",sender,gangName);

        for (GMember member : gangToDelete.getGangMemberList()) {
            member.setGang(null);
            member.setDirty(true);
        }

        gangsPlugin.getGangsManager().deleteGang(gangToDelete.getId());

    }
}
