package net.timme7893.gangs.commands.user;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.GangCommand;
import net.timme7893.gangs.gangs.members.GMember;
import net.timme7893.gangs.utils.Present;
import org.bukkit.entity.Player;

public class GangChatCommand implements GangCommand {

    @Override
    public String getId() {
        return "chat";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, Player player, String[] args) {

        if (!Present.isPlayer(player)) {
            gangsPlugin.getMessageManager().sendMessage("player-not-in-gang", player);
            return;
        }
        GMember member = gangsPlugin.getPlayerManager().getMember(player);

        if (gangsPlugin.getGangChatListener().hasPlayer(player)) {
            gangsPlugin.getGangChatListener().removePlayer(player);
            gangsPlugin.getMessageManager().sendMessage("gang-chat-disabled", player, member.getGang().getGangName());
        } else {
            gangsPlugin.getGangChatListener().addPlayer(player);
            gangsPlugin.getMessageManager().sendMessage("gang-chat-enabled", player, member.getGang().getGangName());
        }

    }
}
