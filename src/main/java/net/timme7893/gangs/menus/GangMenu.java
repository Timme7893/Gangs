package net.timme7893.gangs.menus;

import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.file.File;
import net.timme7893.gangs.utils.menus.AbstractMenu;
import net.timme7893.gangs.utils.text.PKeys;
import net.timme7893.gangs.utils.text.PValues;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.members.GMember;

public class GangMenu extends AbstractMenu {

    private GangsPlugin gangsPlugin;
    private GMember gMember;
    private Player player;
    private File menuFile;
    private Gang gang;

    public GangMenu(Player player) {
        super(GangsPlugin.getInstance(), player, Archive.get("gang-menu"), Archive.get("gang-menu").asString("title"),
                Archive.get("gang-menu").asInt("rows"));
        this.player = player;
        this.gangsPlugin = GangsPlugin.getInstance();
        this.menuFile = Archive.get("gang-menu");
        this.gMember = gangsPlugin.getPlayerManager().getMember(player);
        this.gang = gMember.getGang();
        this.setup();
        // this.show();
    }

    @Override
    protected void configure() {
        /*
         * this.addItem(menuFile, "exit").setAction((menuItem, clickType) -> { new
         * GangMenu(player); });
         */

        this.addItem("info", PKeys.set("gang-name", "leader", "points", "members", "exp"),
                PValues.set(gang.getGangName(),
                        gangsPlugin.getPlayerManager().getGangMembers().get(gang.getGangLeader()).getOfflinePlayer()
                                .getName(),
                        gang.getTotalPoints(), gang.getGangMemberList().size(), gang.getExp(), 1, 2, 3, 4, 5))
                .setAction((menuItem, clickType) -> {
                });

        this.addItem(menuFile, "top").setAction((menuItem, clickType) -> {
            this.close();
            openNewMenu(new GangTopMenu(player));
        });

        this.addItem(menuFile, "house").setAction((menuItem, clickType) -> {
            this.close();
            player.teleport(gang.getGangHouse().getSpawnLoc());
            gMember.initeBorder();
        });

        this.addItem(menuFile, "storage").setAction((menuItem, clickType) -> {
            this.close();
            Bukkit.getScheduler().runTaskLater(GangsPlugin.getInstance(), () -> {
                gang.getStorageMenu().open(player);
            }, 1l);
        });
        this.addItem(menuFile, "upgrades").setAction((menuItem, clickType) -> {
            this.close();
            openNewMenu(new UpgradeMenu(player, gang));
        });
        this.addItem(menuFile, "members").setAction((menuItem, clickType) -> {
            this.close();
            openNewMenu(new MemberMenu(player));
        });
        this.addItem(menuFile, "factories").setAction((menuItem, clickType) -> {
            this.close();
            openNewMenu(new FactoryMenu(player));
        });
        this.addItem(menuFile, "wars").setAction((menuItem, clickType) -> {
            this.close();
            gangsPlugin.getMessageManager().sendMessage("soon-message", player);
        });
        this.addItem(menuFile, "enemies").setAction((menuItem, clickType) -> {
            this.close();
            gangsPlugin.getMessageManager().sendMessage("soon-message", player);
        });
        this.addItem(menuFile, "allies").setAction((menuItem, clickType) -> {
            this.close();
            gangsPlugin.getMessageManager().sendMessage("soon-message", player);
        });
        this.addItem(menuFile,"own-factories").setAction((menuItem, clickType) -> {
            this.close();
            new OwnFactoriesMenu(player);
        });

        ItemStack yellowGlass = new ItemStack(Material.STAINED_GLASS_PANE,1, (byte) 4);
        ItemStack grayGlass = new ItemStack(Material.STAINED_GLASS_PANE,1, (byte) 7);

        this.addItem(yellowGlass,1,1);
        this.addItem(yellowGlass,1,2);
        this.addItem(yellowGlass,1,8);
        this.addItem(yellowGlass,1,9);

        this.addItem(yellowGlass,2,1);
        this.addItem(yellowGlass,2,9);
        this.addItem(yellowGlass,3,1);
        this.addItem(yellowGlass,3,9);
        this.addItem(yellowGlass,4,1);
        this.addItem(yellowGlass,4,9);
        this.addItem(yellowGlass,5,1);
        this.addItem(yellowGlass,5,9);

        this.addItem(yellowGlass,6,1);
        this.addItem(yellowGlass,6,2);
        this.addItem(yellowGlass,6,8);
        this.addItem(yellowGlass,6,9);

        this.setFillerItem(grayGlass);

    }
}
