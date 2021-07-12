package net.timme7893.gangs.gangs.placeholders;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.members.GMember;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ScoreboardPlaceholders {

    public ScoreboardPlaceholders(GangsPlugin gangsPlugin) {

        if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
            // The plugin is enabled
            PlaceholderAPI.registerPlaceholder(gangsPlugin, "gang-name",
                    new PlaceholderReplacer() {

                        @Override
                        public String onPlaceholderReplace(
                                PlaceholderReplaceEvent event) {
                            Player player = event.getPlayer();
                            if (player == null) {
                                return "error-1";
                            }
                            Optional<GMember> gMember = Optional.ofNullable(gangsPlugin.getPlayerManager().getMember(player));
                            if (gMember.isPresent()) {
                                return gMember.get().getGang().getGangName();
                            } else {
                                return "None";
                            }
                        }
            });

            PlaceholderAPI.registerPlaceholder(gangsPlugin, "gang-rank",
                    new PlaceholderReplacer() {

                        @Override
                        public String onPlaceholderReplace(
                                PlaceholderReplaceEvent event) {
                            Player player = event.getPlayer();
                            if (player == null) {
                                return "error-1";
                            }
                            Optional<GMember> gMember = Optional.ofNullable(gangsPlugin.getPlayerManager().getMember(player));
                            if (gMember.isPresent()) {
                                return gMember.get().getGang().getRank() + "";
                            } else {
                                return "None";
                            }
                        }
                    });
        }
    }
}




