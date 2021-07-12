package net.timme7893.gangs.commands.user;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.GangCommand;
import org.bukkit.entity.Player;
import java.util.List;

public class GangTopCommand implements GangCommand {

    @Override
    public String getId() {
        return "top";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, Player player, String[] args) {
        List<String> textLines = gangsPlugin.getGangTopManager().getTextLines();
        for (String line : textLines) {
            player.sendMessage(line);
        }
    }
}
