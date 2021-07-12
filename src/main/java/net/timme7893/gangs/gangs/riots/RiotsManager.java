package net.timme7893.gangs.gangs.riots;

import lombok.Getter;
import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.utils.Present;
import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.file.File;
import net.timme7893.gangs.utils.menus.ItemStackHelp;
import net.timme7893.gangs.utils.regions.Region;
import net.timme7893.gangs.utils.text.PKeys;
import net.timme7893.gangs.utils.text.PValues;
import net.timme7893.gangs.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RiotsManager {

    private GangsPlugin gangsPlugin;
    private File configFile;

    @Getter
    private Riot riot = null;
    private boolean isActive = false;
    private String announcementMessage;
    private String riotOnMessage;
    private String startMessage;
    private String stopMessage;
    private String gangLabMessage;
    private String expMessage;
    private Location loc1;
    private Location loc2;
    private int riotDuration;
    private int betweenRiotsTime;
    private int announceTime;

    @Getter
    private ItemStack gangLab;

    @Getter
    private RiotTimer riotTimer = null;

    public RiotsManager(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
        this.configFile = Archive.get("riots");

        if (configFile.asString("riots.enabled").equals("false")) {
            System.out.println("[Gangs] Riots are disabled.");
            return;
        }

        String worldName = configFile.asString("riots-area.world");
        if (worldName.equals("null")) {
            System.out.println("[Gangs] Riots isn't configured and now also disabled.");
            return;
        }

        if (!Present.isWorld(worldName)) {
            System.out.println("[Gangs] Riots isn't configured and now also disabled.");
            return;
        }
        World world = Bukkit.getWorld(worldName);
        double x1 = configFile.asDouble("riots-area.loc1.x"), y1 = configFile.asDouble("riots-area.loc1.y"), z1 = configFile.asDouble("riots-area.loc1.x");
        double x2 = configFile.asDouble("riots-area.loc2.x"), y2 = configFile.asDouble("riots-area.loc2.y"), z2 = configFile.asDouble("riots-area.loc2.x");

        this.loc1 = new Location(world,x1,y1,z1);
        this.loc2 = new Location(world,x2,y2,z2);

        this.announcementMessage = Text.format(configFile.asString("riots.announce-message"));
        this.startMessage = Text.format(configFile.asString("riots.start-message"));
        this.stopMessage = Text.format(configFile.asString("riots.stop-message"));
        this.riotOnMessage = Text.format(configFile.asString("riots.riot-on"));
        this.gangLabMessage = Text.format(configFile.asString("gang-labs.placed"));
        this.expMessage = configFile.asString("riots.exp-message");
        this.riotDuration = configFile.asInt("riots.duration");
        this.betweenRiotsTime = configFile.asInt("riots.between-riots");
        this.announceTime = configFile.asInt("riots.announce-time");

        this.gangLab = ItemStackHelp.newItemStack(configFile.asString("gang-labs.item"), Text.format(configFile.asString("gang-labs.name")), Text.format(configFile.asList("gang-labs.lore")));

        this.riotTimer = new RiotTimer(gangsPlugin,this,announceTime,riotDuration,betweenRiotsTime);
    }

    public void announce(int minutes) {
        String message = Text.format(announcementMessage, PKeys.set("time"), PValues.set(minutes,1));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    public void announceRiotMessage() {
        announce(Text.format(riotOnMessage));
    }

    public void announceGangLab(Player player) {
        announce(Text.format(gangLabMessage, PKeys.set("player"),PValues.set(player.getName(),1)));
    }

    public void announce(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    public void start() {
        if (isActive) {
            return;
        }

        this.riot = new Riot(riotDuration);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(startMessage);
        }

        isActive = true;
    }

    public void stop() {
        if (!isActive) {
            return;
        }
        riot.destroyAllGangsLabs();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(stopMessage);
        }
        List<Gang> gangs = riot.getGangGainedExp().entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        for (Gang gang : gangs) {
            double aquiredExp = riot.getGangGainedExp().get(gang);
            gang.broadcast(Text.format(expMessage, PKeys.set("gang","exp"), PValues.set(gang.getGangName(),aquiredExp,1,2)));
        }

        this.riot = null;


    }

    public boolean isActive() {
        return isActive;
    }

    public void saveArea(Location loc1, Location loc2) {
        FileConfiguration fileConfiguration = configFile.getFileConfiguration();
        fileConfiguration.set("riots-area.world", loc1.getWorld().getName());

        fileConfiguration.set("riots-area.loc1.x", loc1.getX());
        fileConfiguration.set("riots-area.loc1.y", loc1.getY());
        fileConfiguration.set("riots-area.loc1.z", loc1.getZ());

        fileConfiguration.set("riots-area.loc2.x", loc2.getX());
        fileConfiguration.set("riots-area.loc2.y", loc2.getY());
        fileConfiguration.set("riots-area.loc2.z", loc2.getZ());

        configFile.getFileWrapper().saveConfig();
        this.loc1 = loc1;
        this.loc2 = loc2;
    }

    public boolean applies(Location location) {
        if (!isActive) {
            return false;
        }

        return Region.inCuboid(location,loc1,loc2);
    }
}
