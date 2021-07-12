package net.timme7893.gangs.commands;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.user.*;
import net.timme7893.gangs.menus.GangMenu;
import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.text.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class GangUserCommand implements CommandExecutor {

    private GangsPlugin gangsPlugin;
    private List<String> helpPage;
    private HashMap<String, GangCommand> childCommands = new HashMap<>();

    public GangUserCommand(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
        this.helpPage = Text.format(Archive.get("messages").asList("user-gang-command-help"));
        load();
    }

    public void load() {
        childCommands.put("create", new GangCreateCommand());
        childCommands.put("disband", new GangDisbandCommand());
        childCommands.put("demote", new GangDemoteCommand());
        childCommands.put("house", new GangHouseCommand());
        childCommands.put("home", new GangHouseCommand());
        childCommands.put("chat", new GangChatCommand());
        childCommands.put("invite", new GangInviteCommand());
        childCommands.put("join", new GangJoinCommand());
        childCommands.put("kick", new GangKickCommand());
        childCommands.put("leave", new GangLeaveCommand());
        childCommands.put("setleader", new GangSetLeaderCommand());
        childCommands.put("promote", new GangPromoteCommand());
        childCommands.put("menu", new GangMenuCommand());
        childCommands.put("storage", new GangStorageCommand());
        childCommands.put("top", new GangTopCommand());
        childCommands.put("factories", new GangFactoriesCommand());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (command.getName().equalsIgnoreCase("gang")) {
            if (args.length > 0) {
                if (!(commandSender instanceof Player)) {
                    helpPage.forEach(line -> commandSender.sendMessage(line));
                    commandSender.sendMessage("Only players are allowed to use this command!");
                    return true;
                }

                Player player = (Player) commandSender;
                executeMethod(args[0],player,args);
                return true;
            } else {
                if (!(commandSender instanceof Player)) {
                    helpPage.forEach(line -> commandSender.sendMessage(line));
                    commandSender.sendMessage("Only players are allowed to use this command!");
                    return true;
                }
                Player player = (Player) commandSender;
                if (gangsPlugin.getPlayerManager().getMember(player) != null) {
                    new GangMenu(player).show();
                } else {
                    helpPage.forEach(line -> commandSender.sendMessage(line));
                }
                return true;
            }
        }

        return true;
    }

    public void executeMethod(String childCommand, Player player, String[] args) {
        GangCommand gangCommand = childCommands.values().stream().filter(command -> command.getId().equalsIgnoreCase(childCommand)).findFirst().orElse(null);
        if (gangCommand == null) {
            helpPage.forEach(line -> player.sendMessage(line));
            return;
        }
        gangCommand.executeCommand(gangsPlugin,player,args);
    }
}
