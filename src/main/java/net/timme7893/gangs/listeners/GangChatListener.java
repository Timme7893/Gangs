package net.timme7893.gangs.listeners;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.members.GMember;
import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.text.PKeys;
import net.timme7893.gangs.utils.text.PValues;
import net.timme7893.gangs.utils.text.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

public class GangChatListener implements Listener {

    private GangsPlugin gangsPlugin;
    private String format;
    private List<Player> inGangChat = new ArrayList<>();

    public GangChatListener(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
        this.format = Archive.get("config").asString("gang-chat.format");
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();

        if (inGangChat.contains(event.getPlayer())) {
            GMember member = gangsPlugin.getPlayerManager().getMember(event.getPlayer());
            event.setCancelled(true);
            String formattedMessage = Text.format(format, PKeys.set("rank", "name", "message"), PValues.set(member.getRoleCharacter(), member.getOnlinePlayer().getName(),message,1,2,3));
            member.getGang().broadcast(formattedMessage);
        }
    }

    public boolean hasPlayer(Player player) {
        return inGangChat.contains(player);
    }

    public void addPlayer(Player player) {
        if (!inGangChat.contains(player)) {
            inGangChat.add(player);
        }
    }

    public void removePlayer(Player player) {
        if (inGangChat.contains(player)) {
            inGangChat.remove(player);
        }
    }
}
