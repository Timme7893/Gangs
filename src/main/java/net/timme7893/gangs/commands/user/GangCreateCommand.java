package net.timme7893.gangs.commands.user;

import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.GangCommand;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.GangRole;
import net.timme7893.gangs.gangs.members.GMember;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GangCreateCommand implements GangCommand {

    private List<String> blackList = new ArrayList<>();

    public GangCreateCommand() {
        blackList = Archive.get("gang-black-names").asList("blacklist");
    }

    @Override
    public String getId() {
        return "create";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, Player player, String[] args) {

        if (gangsPlugin.getGangsManager().getGangWithLeader(player.getUniqueId()) != null) {
            gangsPlugin.getMessageManager().sendMessage("member-already-owns-gang", player);
            return;
        }

        if (gangsPlugin.getPlayerManager().getMember(player) != null) {
            gangsPlugin.getMessageManager().sendMessage("member-in-gang", player);
            return;
        }

        // create <name>
        if (args.length != 2) {
            gangsPlugin.getMessageManager().sendMessage("create-form", player);
            return;
        }
        String name = ChatColor.stripColor(args[1]);

        if (name.length() > 12) {
            gangsPlugin.getMessageManager().sendMessage("name-error", player);
            return;
        }

        if (blackList.contains(name.toLowerCase())) {
            gangsPlugin.getMessageManager().sendMessage("gang-blacklist-name",player);
            return;
        }

        Gang gang = gangsPlugin.getGangsManager().createGang(player.getUniqueId(), ChatColor.stripColor(args[1]));
        gangsPlugin.getMessageManager().sendMessage("gang-created", player, ChatColor.stripColor(args[1]));
        GMember member = gangsPlugin.getPlayerManager().createMember(player, gang);
        member.setGangRole(GangRole.LEADER);
    }
}
