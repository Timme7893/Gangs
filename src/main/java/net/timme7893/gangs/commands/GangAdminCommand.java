package net.timme7893.gangs.commands;

import java.util.HashMap;
import java.util.List;

import net.timme7893.gangs.commands.admin.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.text.Text;

public class GangAdminCommand implements CommandExecutor {

    private GangsPlugin gangsPlugin;
    private List<String> helpPage;
    private HashMap<String, AGangCommand> childCommands = new HashMap<>();

    public GangAdminCommand(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
        this.helpPage = Text.format(Archive.get("messages").asList("admin-gang-command-help"));
        load();
    }

    public void load() {
        childCommands.put("setriotarea", new GangRiotCommand());
        childCommands.put("demoteplayer", new GangDemoteCommand());
        childCommands.put("upgradeplayer", new GangUpgradeCommand());
        childCommands.put("giveblock", new GangGiveBlockCommand());
        childCommands.put("giveblockp", new GangGiveBlockPCommand());
        childCommands.put("removeblock", new GangRemoveBlockCommand());
        childCommands.put("giveexp", new GangGiveExpCommand());
        childCommands.put("removeexp", new GangRemoveExpCommand());
        childCommands.put("giveganglab", new GangLabCommand());
        childCommands.put("givebooster", new GangGiveBoosterCommand());
        childCommands.put("clearboosters", new GangClearBoostersCommand());
        childCommands.put("bypass", new GangBypassCommand());
        childCommands.put("riot", new GangRiotStatusCommand());
        childCommands.put("delete", new GangDeleteCommand());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (command.getName().equalsIgnoreCase("agang")) {

            if (!commandSender.hasPermission("gangs.admin")) {
                gangsPlugin.getMessageManager().sendMessage("not-admin", commandSender);
                return true;
            }

            if (args.length > 0) {

                executeMethod(args[0], commandSender, args);
                return true;
            } else {
                helpPage.forEach(line -> commandSender.sendMessage(line));
                return true;
            }
        }

        return true;
    }

    public void executeMethod(String childCommand, CommandSender sender, String[] args) {
        AGangCommand gangCommand = childCommands.values().stream()
                .filter(command -> command.getId().equalsIgnoreCase(childCommand)).findFirst().orElse(null);
        if (gangCommand == null) {
            helpPage.forEach(line -> sender.sendMessage(line));
            return;
        }
        gangCommand.executeCommand(gangsPlugin, sender, args);
    }
}
