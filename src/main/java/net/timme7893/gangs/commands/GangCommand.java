package net.timme7893.gangs.commands;

import net.timme7893.gangs.GangsPlugin;
import org.bukkit.entity.Player;

public interface GangCommand {

    String getId();

    void executeCommand(GangsPlugin gangsPlugin, Player player, String[] args);

}
