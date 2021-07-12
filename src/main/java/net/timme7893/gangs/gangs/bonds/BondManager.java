package net.timme7893.gangs.gangs.bonds;

import java.util.HashMap;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;

public class BondManager {

    private GangsPlugin gangsPlugin;
    private HashMap<Gang, Ally> allyInvites = new HashMap<>();

    public BondManager(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
    }

    public void inviteAlly(Gang sender, Gang receiver) {
        allyInvites.put(sender, new Ally(sender, receiver));
        gangsPlugin.getMessageManager().sendMessage("ally-invite", receiver, sender.getGangName());
        gangsPlugin.getMessageManager().sendMessage("ally-invite-send", sender, receiver.getGangName());
    }

    public void acceptAlly(Gang receiver, Gang sender) {
        Ally ally = getAllyInvite(sender, receiver);
        if (ally != null) {
            allyInvites.remove(sender, ally);
            // WHY TIMME???
            // allyInvites.remove(ally);

            ally.accept();
            gangsPlugin.getMessageManager().sendMessage("ally-invite-accepted", sender, receiver.getGangName());
            gangsPlugin.getMessageManager().sendMessage("receiver-invite-accepted", receiver, sender.getGangName());
        }
    }

    public void declineAlly(Gang receiver, Gang sender) {
        Ally ally = getAllyInvite(sender, receiver);
        if (ally != null) {
            allyInvites.remove(sender, ally);
            // WHY TIMME???
            // allyInvites.remove(ally);

            gangsPlugin.getMessageManager().sendMessage("ally-invite-declined", sender, receiver.getGangName());
            gangsPlugin.getMessageManager().sendMessage("receiver-declined-request", receiver, sender.getGangName());
        }
    }

    public void declareEnemy(Gang sender, Gang receiver) {
        if (receiver.isEnemy(sender)) {
            return;
        }

        Enemy enemy = new Enemy(sender, receiver);
        enemy.accept();
        gangsPlugin.getMessageManager().sendMessage("enemy-declared", sender, sender.getGangName(), receiver.getGangName());
        gangsPlugin.getMessageManager().sendMessage("enemy-declared", receiver, sender.getGangName(), receiver.getGangName());
    }

    public void resolveAlly(Gang sender, Ally ally) {
        gangsPlugin.getMessageManager().sendMessage("ally-resolved", ally.getAlly(), sender.getGangName());
        ally.getAlly().removeAlly(ally);
        sender.removeAlly(ally);
    }

    public Ally getAllyInvite(Gang sender, Gang receiver) {
        return allyInvites.values().stream().filter(ally -> ally.getOwn() == sender && ally.getAlly() == receiver)
                .findFirst().orElse(null);
    }
}
