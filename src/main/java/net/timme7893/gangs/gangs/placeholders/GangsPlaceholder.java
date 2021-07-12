package net.timme7893.gangs.gangs.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.utils.Present;
import org.bukkit.entity.Player;

public class GangsPlaceholder extends PlaceholderExpansion {
    @Override
    public boolean canRegister() {
        return false;
    }

    @Override
    public String getIdentifier() {
        return "gangs";
    }

    @Override
    public String getPlugin() {
        return null;
    }

    @Override
    public String getAuthor() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {
        if (s.contains("rank")) {
            if (Present.isPlayer(player)) {
                Gang gang = GangsPlugin.getInstance().getPlayerManager().getMember(player).getGang();
                return gang.getRank() + "";
            } else {
                return "None";
            }
        }else if (s.contains("exp")) {
            if (Present.isPlayer(player)) {
                return GangsPlugin.getInstance().getPlayerManager().getMember(player).getGang().getExp() + "";
            } else {
                return "None";
            }
        }else if (s.contains("name")) {
            if (Present.isPlayer(player)) {
                return GangsPlugin.getInstance().getPlayerManager().getMember(player).getGang().getGangName();
            } else {
                return "None";
            }
        }
        
        return null;
    }
}
