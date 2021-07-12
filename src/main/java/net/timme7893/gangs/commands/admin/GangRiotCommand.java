package net.timme7893.gangs.commands.admin;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.AGangCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GangRiotCommand implements AGangCommand {
    @Override
    public String getId() {
        return "setriotarea";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("must be player to execute this command.");
            return;
        }
        Player player = (Player) sender;
        gangsPlugin.getRiotAreaListener().creatingRegion.add(player);
        gangsPlugin.getMessageManager().sendMessage("riots-area-loc", player);
    }
}
