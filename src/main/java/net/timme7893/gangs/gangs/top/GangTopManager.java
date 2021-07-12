package net.timme7893.gangs.gangs.top;

import lombok.Getter;
import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.text.PKeys;
import net.timme7893.gangs.utils.text.PValues;
import net.timme7893.gangs.utils.text.Text;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GangTopManager {

    private GangsPlugin gangsPlugin;
    @Getter
    private HashMap<Integer, Gang> gangsTop = new HashMap<>();
    @Getter
    private List<String> textLines = new ArrayList<>();
    private String formatMessage;
    private String firstLine;

    public GangTopManager(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
        this.formatMessage = Text.format(Archive.get("messages").asString("gang-top.format"));
        this.firstLine = Text.format(Archive.get("messages").asString("gang-top.message"));
        calculateTop();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(gangsPlugin, new Runnable() {
            public void run() {
                calculateTop();
            }
        }, 20l, 600L);
    }

    private int place = 0;

    public void calculateTop() {
        textLines.clear();
        textLines.add(firstLine);
        place = 0;
        @SuppressWarnings("unchecked")
        HashMap<Integer,Gang> gangs = (HashMap<Integer, Gang>) gangsPlugin.getGangsManager().getGangs().clone();
        gangs.entrySet().stream().sorted((a1,a2) -> {
            int points1 = a1.getValue().getTotalPoints();
            int points2 = a2.getValue().getTotalPoints();
            return points2 - points1;
        }).forEach(item -> {
            place++;
            gangsTop.put(place, item.getValue());
            Gang gang = item.getValue();
            gang.setRank(place);
            if (place < 11) {
                textLines.add(Text.format(formatMessage, PKeys.set("place","name", "points", "max"), PValues.set(place,gang.getGangName(), gang.getTotalPoints(), gang.getGangMemberList().size(), 1, 2, 3,4)));
            }
        });
    }

    public Gang getGangWithPlace(int place) {
        if (gangsTop.size() > place) {
            return null;
        }
        return gangsTop.get(place);
    }

}
