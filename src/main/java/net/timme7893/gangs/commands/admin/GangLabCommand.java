package net.timme7893.gangs.commands.admin;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.AGangCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GangLabCommand implements AGangCommand {


    @Override
    public String getId() {
        return "giveganglab";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, CommandSender sender, String[] args) {

        if (args.length < 3) {
            gangsPlugin.getMessageManager().sendMessage("use-ganglab",sender);
            return;
        }

        String playerName = args[1];
        if (Bukkit.getPlayer(playerName) == null) {
            gangsPlugin.getMessageManager().sendMessage("player-not-exists",sender);
            return;
        }
        Player target = Bukkit.getPlayer(playerName);


        if (getInteger(args[2]) == -1) {
            gangsPlugin.getMessageManager().sendMessage("invalid-integer",sender);
            return;
        }
        int amount = getInteger(args[2]);

        for (int i = 0; i < amount; i++) {
            target.getInventory().addItem(gangsPlugin.getRiotsManager().getGangLab());
        }
        gangsPlugin.getMessageManager().sendMessage("given-ganglab",sender,amount,target.getName());
    }

    public int getInteger(String text) {
        try {
            return Integer.parseInt(text);
        } catch (Exception ex) {
            return -1;
        }
    }
}
