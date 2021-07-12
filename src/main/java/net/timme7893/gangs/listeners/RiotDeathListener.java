package net.timme7893.gangs.listeners;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.members.GMember;
import net.timme7893.gangs.utils.Present;
import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.text.PKeys;
import net.timme7893.gangs.utils.text.PValues;
import net.timme7893.gangs.utils.text.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class RiotDeathListener implements Listener {

    private GangsPlugin gangsPlugin;
    private String deathMessage;

    public RiotDeathListener(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
        this.deathMessage = Archive.get("messages").asString("death-message.message");
    }

    // This event listener also handles the death messages.

    @EventHandler(priority = EventPriority.HIGH)
    public void playerDeath(PlayerDeathEvent event) {

        if (event.getEntity() instanceof Player) {
            Player deathPlayer = (Player) event.getEntity();
            if (event.getEntity().getKiller() instanceof Player) {
                Player killerPlayer = (Player) event.getEntity().getKiller();

                if (Present.isPlayer(deathPlayer) && Present.isPlayer(killerPlayer)) {
                    GMember member = gangsPlugin.getPlayerManager().getMember(deathPlayer);
                    GMember killMember = gangsPlugin.getPlayerManager().getMember(killerPlayer);
                    event.setDeathMessage(Text.format(deathMessage, PKeys.set("gang1", "killer", "gang2", "death"),
                            PValues.set("(" +killMember.getGang().getGangName()+ ")", killerPlayer.getName(),
                                    "(" +member.getGang().getGangName()+ ")", deathPlayer.getName(), 1, 2, 3, 4)));

                    if (gangsPlugin.getRiotsManager().applies(deathPlayer.getLocation())) {

                        gangsPlugin.getRiotsManager().getRiot().enterGang(killMember.getGang());

                        double halfExp = Math.round(member.getExpGained() / 2);
                        if (member.getGang().getExp() >= halfExp) {
                            member.getGang().setExp(member.getGang().getExp() - halfExp);
                            gangsPlugin.getRiotsManager().getRiot().updateGang(killMember.getGang(),halfExp);
                        } else {
                            member.getGang().setExp(0);
                        }
                        killMember.getGang().setExp(killMember.getGang().getExp() + halfExp);
                        gangsPlugin.getMessageManager().sendActionBar("exp-gained",killerPlayer,halfExp);
                    }

                } else if (Present.isPlayer(deathPlayer) && !Present.isPlayer(killerPlayer)) {
                    // killer = not in gang.
                    // death player = in gang.
                    GMember member = gangsPlugin.getPlayerManager().getMember(deathPlayer);
                    event.setDeathMessage(Text.format(deathMessage, PKeys.set("gang1", "killer", "gang2", "death"),
                            PValues.set("&f", killerPlayer.getName(), "(" + member.getGang().getGangName() + ")",
                                    deathPlayer.getName(), 1, 2, 3, 4)));

                } else if (!Present.isPlayer(deathPlayer) && Present.isPlayer(killerPlayer)) {
                    // killer = in gang.
                    // death player = not in gang.
                    GMember killerMember = gangsPlugin.getPlayerManager().getMember(killerPlayer);
                    event.setDeathMessage(Text.format(deathMessage, PKeys.set("gang1", "killer", "gang2", "death"),
                            PValues.set("(" + killerMember.getGang().getGangName() + ")", killerPlayer.getName(), "&f",
                                    deathPlayer.getName(), 1, 2, 3, 4)));
                } else {
                    // both players aren't in a gang.
                    event.setDeathMessage(Text.format(deathMessage, PKeys.set("gang1", "killer", "gang2", "death"),
                            PValues.set("&f", killerPlayer.getName(), "&f", deathPlayer.getName(), 1, 2, 3, 4)));
                }
            }
        }

    }
}
