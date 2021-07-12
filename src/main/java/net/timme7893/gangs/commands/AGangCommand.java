package net.timme7893.gangs.commands;

import net.timme7893.gangs.GangsPlugin;
import org.bukkit.command.CommandSender;

public interface AGangCommand {

    String getId();

    void executeCommand(GangsPlugin gangsPlugin, CommandSender sender, String[] args);

}

