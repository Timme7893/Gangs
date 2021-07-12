package net.timme7893.gangs.commands.admin;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.AGangCommand;
import net.timme7893.gangs.gangs.Gang;
import org.bukkit.command.CommandSender;

public class GangClearBoostersCommand implements AGangCommand {

    @Override
    public String getId() {
        return "clearboosters";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, CommandSender sender, String[] args) {

        if (args.length < 2) {
            gangsPlugin.getMessageManager().sendMessage("use-clearbooster", sender);
            return;
        }

        String gangName = args[1];
        if (gangsPlugin.getGangsManager().getGangWithName(gangName) == null) {
            gangsPlugin.getMessageManager().sendMessage("gang-not-exists", sender);
            return;
        }

        Gang gang = gangsPlugin.getGangsManager().getGangWithName(gangName);
        int boosters = gang.clearBoosters();

        gangsPlugin.getMessageManager().sendMessage("clearboosters", sender, "" + boosters, gang.getGangName());

    }
}
