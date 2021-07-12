package net.timme7893.gangs.commands.user;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.GangCommand;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.utils.Present;
import org.bukkit.entity.Player;

public class GangStorageCommand implements GangCommand {
    @Override
    public String getId() {
        return "storage";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, Player player, String[] args) {

        if (!Present.isPlayer(player)) {
            gangsPlugin.getMessageManager().sendMessage("player-not-in-gang",player);
            return;
        }

        Gang gang = gangsPlugin.getPlayerManager().getMember(player).getGang();
        gang.getStorageMenu().open(player);
    }
}
