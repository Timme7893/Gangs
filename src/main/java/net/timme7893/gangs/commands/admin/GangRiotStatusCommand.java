package net.timme7893.gangs.commands.admin;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.AGangCommand;
import net.timme7893.gangs.gangs.riots.RiotsManager;
import org.bukkit.command.CommandSender;

public class GangRiotStatusCommand implements AGangCommand {

    @Override
    public String getId() {
        return "riot";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, CommandSender sender, String[] args) {

        if (args.length < 2) {
            gangsPlugin.getMessageManager().sendMessage("use-riot",sender);
            return;
        }

        RiotsManager riotsManager = gangsPlugin.getRiotsManager();

        if (args[1].equalsIgnoreCase("start")) {

            if (riotsManager.isActive()) {
                gangsPlugin.getMessageManager().sendMessage("riot-active",sender);
                return;
            }
            riotsManager.start();

        } else if (args[1].equalsIgnoreCase("stop")) {

            if (!riotsManager.isActive()) {
                gangsPlugin.getMessageManager().sendMessage("riot-empty",sender);
                return;
            }

            riotsManager.stop();

        } else {
            gangsPlugin.getMessageManager().sendMessage("use-riot",sender);
            return;
        }
    }
}
