package net.timme7893.gangs.commands.user;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.GangCommand;
import net.timme7893.gangs.menus.GangMenu;
import net.timme7893.gangs.utils.Present;
import org.bukkit.entity.Player;

public class GangMenuCommand implements GangCommand {
    @Override
    public String getId() {
        return "menu";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, Player player, String[] args) {

        if (!Present.isPlayer(player)) {
            gangsPlugin.getMessageManager().sendMessage("player-not-in-gang",player);
            return;
        }

        new GangMenu(player);
    }
}
