package net.timme7893.gangs;

import com.github.yannicklamprecht.worldborder.api.BorderAPI;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import net.timme7893.gangs.gangs.placeholders.ScoreboardPlaceholders;
import net.timme7893.gangs.commands.GangAdminCommand;
import net.timme7893.gangs.commands.GangUserCommand;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.GangsManager;
import net.timme7893.gangs.gangs.bonds.BondManager;
import net.timme7893.gangs.gangs.debuff.DebuffManager;
import net.timme7893.gangs.gangs.factories.FactoriesManager;
import net.timme7893.gangs.gangs.houses.GangHouseManager;
import net.timme7893.gangs.gangs.houses.GangMine;
import net.timme7893.gangs.gangs.invites.GangInviteManager;
import net.timme7893.gangs.gangs.members.PlayerManager;
import net.timme7893.gangs.gangs.placeholders.GangsPlaceholder;
import net.timme7893.gangs.gangs.riots.RiotsManager;
import net.timme7893.gangs.gangs.top.GangTopManager;
import net.timme7893.gangs.gangs.updates.UpdateManager;
import net.timme7893.gangs.listeners.*;
import net.timme7893.gangs.utils.MessageManager;
import net.timme7893.gangs.utils.Present;
import net.timme7893.gangs.utils.data.MySQL;
import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.file.File;
import net.timme7893.gangs.utils.menus.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;


public class GangsPlugin extends JavaPlugin {

    @Getter
    private static GangsPlugin instance = null;
    @Getter
    private Archive archive = null;
    @Getter
    private GangsManager gangsManager = null;
    @Getter
    private PlayerManager playerManager = null;
    @Getter
    private MySQL mysql = null;
    @Getter
    private MessageManager messageManager = null;
    @Getter
    private GangHouseManager gangHouseManager = null;
    @Getter
    private GangInviteManager gangInviteManager = null;
    @Getter
    private GangChatListener gangChatListener = null;
    @Getter
    private RiotsManager riotsManager = null;
    @Getter
    private RiotAreaListener riotAreaListener = null;
    @Getter
    private FactoriesManager factoriesManager = null;
    @Getter
    private Economy economy = null;
    @Getter
    private UpdateManager updateManager = null;
    @Getter
    private BondManager bondManager = null;
    @Getter
    private DebuffManager debuffManager = null;
    @Getter
    private GangTopManager gangTopManager = null;
    @Getter
    private WorldBorderApi worldBorderApi = null;

    public void onEnable() {
        instance = this;

        // file system
        archive = new Archive(this);
        archive.insertDefaultConfig();
        archive.insertWrapperFiles("config.yml", "messages.yml", "riots.yml", "gmine.yml", "gang-black-names.yml", "menus/gang-menu.yml",
                "menus/gang-top-menu.yml", "menus/storage-menu.yml", "menus/factory-menu.yml",
                "menus/factory-stats-menu.yml", "menus/member-menu.yml", "menus/upgrade-menu.yml",
                "menus/debuff-menu.yml", "menus/allies-menu.yml", "menus/enemies-menu.yml", "menus/invite-menu.yml", "menus/own-factories-menu.yml");

        // instances
        mysql = new MySQL();
        new Present(this);
        gangsManager = new GangsManager(this);
        playerManager = new PlayerManager(this);
        messageManager = new MessageManager();
        gangHouseManager = new GangHouseManager(this);
        gangInviteManager = new GangInviteManager();
        riotsManager = new RiotsManager(this);
        factoriesManager = new FactoriesManager(this);
        updateManager = new UpdateManager(this);
        bondManager = new BondManager(this);
        debuffManager = new DebuffManager(this);
        gangTopManager = new GangTopManager(this);

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new MenuListener(), this);
        pluginManager.registerEvents(gangChatListener = new GangChatListener(this), this);
        pluginManager.registerEvents(riotAreaListener = new RiotAreaListener(this), this);
        pluginManager.registerEvents(new GangExpListener(this), this);
        pluginManager.registerEvents(new PlayerHandlerListener(this), this);
        pluginManager.registerEvents(new GangHouseListener(this), this);
        pluginManager.registerEvents(new StorageListener(this), this);
        pluginManager.registerEvents(new RiotDeathListener(this), this);
        pluginManager.registerEvents(new RiotUtilListeners(this), this);
        pluginManager.registerEvents(new GangBlockListener(this), this);
        pluginManager.registerEvents(new BorderListener(this),this);
        pluginManager.registerEvents(new BondsListener(this),this);

        getCommand("gang").setExecutor(new GangUserCommand(this));
        getCommand("agang").setExecutor(new GangAdminCommand(this));

        setupEconomy();
        setupPlaceholder();
        setupWorldBorderAPI();
        new ScoreboardPlaceholders(this);

        // Members aren't loaded on first calculation, this should fix this issue
        Bukkit.getScheduler().runTaskLater(this, () -> {
            gangTopManager.calculateTop();
        }, 60l);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                File file = Archive.get("config");

                for (Gang gang : getInstance().getGangsManager().getAllGangs()) {
                    GangMine mine = gang.getGangHouse().getGangMine();
                    if (mine.getMinutesSinceBreak() != -1) {
                        mine.setMinutesSinceBreak(mine.getMinutesSinceBreak() + 1);
                    }
                    if (mine.getMinutesSinceBreak() == file.asInt("mines.minutes-before-reset") - 1) {
                        gang.broadcast(String.join("\n", getMessageManager().getMessage("mine-reset-warning")));
                    }else if (mine.getMinutesSinceBreak() == file.asInt("mines.minutes-before-reset")) {
                        mine.setMinutesSinceBreak(-1);
                        mine.resetMine();
                        gang.broadcast(String.join("\n", getMessageManager().getMessage("mine-reset")));
                    }
                }
            }
        }, 60 * 20l, 60 * 20l);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public void setupPlaceholder() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PlaceholderAPI.registerPlaceholderHook(this, new GangsPlaceholder());
        }
    }

    public void setupWorldBorderAPI() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldBorderAPI");
        if (plugin == null || !plugin.isEnabled()) {
            getLogger().info("World border api not found");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            this.worldBorderApi = BorderAPI.getApi();
        }
    }

    public void onDisable() {
        updateManager.startUpdate();
        instance = null;
    }
}
