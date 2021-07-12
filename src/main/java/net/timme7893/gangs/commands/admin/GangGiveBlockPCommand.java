package net.timme7893.gangs.commands.admin;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.AGangCommand;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.members.GMember;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GangGiveBlockPCommand implements AGangCommand {

    @Override
    public String getId() {
        return "giveblockp";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, CommandSender sender, String[] args) {

        // agang giveblockp <player> <type> <amount>

        String playerName = args[1];
        String blockType = args[2];
        String amountString = args[3];
        if (getInteger(amountString) == -1) {
            gangsPlugin.getMessageManager().sendMessage("invalid-integer", sender);
            return;
        }
        int amount = getInteger(amountString);

        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null) {
            gangsPlugin.getMessageManager().sendMessage("player-unknown", sender);
            return;
        }
        GMember gmember = gangsPlugin.getPlayerManager().getMember(targetPlayer);
        if (gmember == null) {
            gangsPlugin.getMessageManager().sendMessage("target-not-in-gang", sender, targetPlayer.getName(), 1);
            return;
        }
        Gang gang = gmember.getGang();

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
