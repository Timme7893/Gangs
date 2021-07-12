package net.timme7893.gangs.commands.user;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.GangCommand;
import net.timme7893.gangs.menus.OwnFactoriesMenu;
import org.bukkit.entity.Player;

public class GangFactoriesCommand implements GangCommand {
    @Override
    public String getId() {
        return "factories";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, Player player, String[] args) {

        if (gangsPlugin.getPlayerManager().getMember(player) == null) {
            gangsPlugin.getMessageManager().sendMessage("player-not-in-gang",player);
            return;
        }

        new OwnFactoriesMenu(player);
    }
}
