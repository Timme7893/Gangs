package net.timme7893.gangs.gangs.invites;

import net.timme7893.gangs.gangs.Gang;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GangInviteManager {

    private List<GangInvite> invites = new ArrayList<>();

    public GangInviteManager() {}

    public void addInvite(GangInvite invite) {
        invites.add(invite);
    }

    public void removeInvite(GangInvite invite) {
        if (invites.contains(invite))
            invites.remove(invite);
    }

    public List<GangInvite> getInvites(Player player) {
        return invites.stream().filter(invite -> invite.getPlayer().equals(player)).collect(Collectors.toList());
    }

    public List<GangInvite> getInvitesWithGang(Gang gang) {
        return invites.stream().filter(invite -> invite.getGang().equals(gang)).collect(Collectors.toList());
    }

    public GangInvite hasInvite(Player player, Gang gang) {
        return invites.stream().filter(invite -> invite.getGang().equals(gang) && invite.getPlayer().equals(player)).findFirst().orElse(null);
    }
}
