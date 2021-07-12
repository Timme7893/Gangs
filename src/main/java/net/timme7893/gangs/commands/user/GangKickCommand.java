package net.timme7893.gangs.commands.user;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.GangCommand;
import net.timme7893.gangs.gangs.members.GMember;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GangKickCommand implements GangCommand {
    @Override
    public String getId() {
        return "kick";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, Player player, String[] args) {

        if (gangsPlugin.getPlayerManager().getMember(player) == null) {
            gangsPlugin.getMessageManager().sendMessage("player-not-in-gang",player);
            return;
        }

        // format: kick <name>
        if (args.length != 2) {
            gangsPlugin.getMessageManager().sendMessage("kick-form",player);
            return;
        }

        String targetName = args[1];
        if (Bukkit.getPlayer(targetName) == null) {
            gangsPlugin.getMessageManager().sendMessage("player-not-exists",player,targetName);
            return;
        }
        Player targetPlayer = Bukkit.getPlayer(targetName);
        GMember target = gangsPlugin.getPlayerManager().getMember(targetPlayer);

        if (target.getGang().getGangLeader().equals(target.getPlayerId())) {
            gangsPlugin.getMessageManager().sendMessage("cant-kick-leader",player);
            return;
        }

        target.getGang().removeMember(target);
        target.setGang(null);
        gangsPlugin.getMessageManager().sendMessage("player-kicked", target.getOnlinePlayer());
        gangsPlugin.getMessageManager().sendMessage("member-kicked", player, target.getOnlinePlayer().getName());

        gangsPlugin.getPlayerManager().deleteMember(target);

        if (targetPlayer.getLocation().getWorld().equals(gangsPlugin.getGangHouseManager().getHouseWorld())) {
            targetPlayer.chat("/spawn");
        }
        
    }
}
