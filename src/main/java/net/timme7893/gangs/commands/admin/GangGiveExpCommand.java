package net.timme7893.gangs.commands.admin;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.AGangCommand;
import net.timme7893.gangs.gangs.Gang;
import org.bukkit.command.CommandSender;

public class GangGiveExpCommand implements AGangCommand {

    @Override
    public String getId() {
        return "giveexp";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, CommandSender sender, String[] args) {

        // agang giveexp gang amount

        String gangName = args[1];
        String amountString = args[2];
        if (getInteger(amountString) == -1) {
            gangsPlugin.getMessageManager().sendMessage("invalid-integer",sender);
            return;
        }
        int amount = getInteger(amountString);

        if (gangsPlugin.getGangsManager().getGangWithName(gangName) == null) {
            gangsPlugin.getMessageManager().sendMessage("gang-unknown",sender);
            return;
        }
        Gang gang = gangsPlugin.getGangsManager().getGangWithName(gangName);

        gang.setExp(gang.getExp() + amount);
        gangsPlugin.getMessageManager().sendMessage("added-exp",sender,amount,gang.getGangName());
    }

    public int getInteger(String text) {
        try {
            return Integer.parseInt(text);
        } catch (Exception ex) {
            return -1;
        }
    }
}
