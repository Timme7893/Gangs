package net.timme7893.gangs.commands.admin;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.AGangCommand;
import net.timme7893.gangs.gangs.Gang;
import org.bukkit.command.CommandSender;

public class GangGiveBlockCommand implements AGangCommand {

    @Override
    public String getId() {
        return "giveblock";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, CommandSender sender, String[] args) {

        // agang giveblock <gang> <type> <amount>

        String gangName = args[1];
        String blockType = args[2];
        String amountString = args[3];
        if (getInteger(amountString) == -1) {
            gangsPlugin.getMessageManager().sendMessage("invalid-integer", sender);
            return;
        }
        int amount = getInteger(amountString);

        if (gangsPlugin.getGangsManager().getGangWithName(gangName) == null) {
            gangsPlugin.getMessageManager().sendMessage("gang-unknown", sender, gangName, 1);
            return;
        }
        Gang gang = gangsPlugin.getGangsManager().getGangWithName(gangName);

        if (blockType.equalsIgnoreCase("slime")) {
            gang.setSlimeBlocks(gang.getSlimeBlocks() + amount);
        } else if (blockType.equalsIgnoreCase("lantern")) {
            gang.setLanternBlocks(gang.getLanternBlocks() + amount);
        } else if (blockType.equalsIgnoreCase("prismarine")) {
            gang.setPrismarineBlocks(gang.getPrismarineBlocks() + amount);
        } else {
            gangsPlugin.getMessageManager().sendMessage("unknown-block", sender);
            return;
        }

        gangsPlugin.getMessageManager().sendMessage("added-block", sender, amount, blockType, gang.getGangName());
    }

    public int getInteger(String text) {
        try {
            return Integer.parseInt(text);
        } catch (Exception ex) {
            return -1;
        }
    }
}
