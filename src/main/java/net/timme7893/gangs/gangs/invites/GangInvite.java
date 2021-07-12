package net.timme7893.gangs.gangs.invites;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;
import org.bukkit.entity.Player;

import java.util.Collection;

public class GangInvite {

    private Player player;
    private Gang gang;

    public GangInvite(Player player, Gang gang) {
        this.gang = gang;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Gang getGang() {
        return gang;
    }

    public void accept() {
        GangsPlugin gangsPlugin = GangsPlugin.getInstance();

        Collection<String> broadcastMessage = gangsPlugin.getMessageManager().getMessage("gang-new-member", player.getName());
        for (String message : broadcastMessage) {
            gang.broadcast(message);
        }
        for (String message : broadcastMessage) {
            player.sendMessage(message);
        }
        gangsPlugin.getGangInviteManager().removeInvite(this);
        gangsPlugin.getPlayerManager().createMember(player,gang);
    }
}
