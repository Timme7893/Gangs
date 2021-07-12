package net.timme7893.gangs.menus;

import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.file.File;
import net.timme7893.gangs.utils.menus.AbstractMenu;
import net.timme7893.gangs.utils.text.PKeys;
import net.timme7893.gangs.utils.text.PValues;
import net.timme7893.gangs.utils.text.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.GangRole;
import net.timme7893.gangs.gangs.members.GMember;
import net.timme7893.gangs.gangs.utils.GangUpgrade;

public class UpgradeMenu extends AbstractMenu {

    private GangsPlugin gangsPlugin;
    private GMember gMember;
    private Player player;
    private File menuFile;
    private Gang gang;

    public UpgradeMenu(Player player, Gang g) {
        super(GangsPlugin.getInstance(), player, Archive.get("upgrade-menu"),
                Text.format(Archive.get("upgrade-menu").asString("title"), PKeys.set("id"),
                        PValues.set(g.getGangName(), 1)),
                Archive.get("upgrade-menu").asInt("rows"));
        this.player = player;
        this.gangsPlugin = GangsPlugin.getInstance();
        this.menuFile = Archive.get("upgrade-menu");
        this.gMember = gangsPlugin.getPlayerManager().getMember(player);
        this.gang = g;

        this.setup();
        // this.show();
    }

    @Override
    protected void configure() {

        this.setCloseAction(new CloseAction() {
            @Override
            public void onClose() {
                openNewMenu(new GangMenu(player));
            }
        });

        this.addItem("storage", PKeys.set("level", "exp"),
                PValues.set(gang.getStorageLevel(), gang.getUpgradeCost(GangUpgrade.STORAGE), 1, 2))
                .setAction((menuItem, clickType) -> {

                    if (gMember.getGangRole() == GangRole.MEMBER || gMember.getGangRole() == GangRole.OFFICER) {
                        gangsPlugin.getMessageManager().sendMessage("not-perms-gang", player);
                        this.close();
                        return;
                    }
                    if (gang.getStorageLevel() >= 53) {
                        gangsPlugin.getMessageManager().sendMessage("max-upgrade-level-reached", player);
                        return;
                    }
                    if (gang.getExp() >= gang.getUpgradeCost(GangUpgrade.STORAGE)) {
                        gang.setExp(gang.getExp() - gang.getUpgradeCost(GangUpgrade.STORAGE));
                        gangsPlugin.getMessageManager().sendMessage("storage-upgrade", player,
                                gang.getUpgradeCost(GangUpgrade.STORAGE));
                        gang.gangUpgrade(GangUpgrade.STORAGE);
                        // Add extra slot to cached inventory
                        gang.getStorageMenu().loadInventory();

                        injectPlaceholders(player,"storage",PKeys.set("level", "exp"),
                                PValues.set(gang.getStorageLevel(), gang.getUpgradeCost(GangUpgrade.STORAGE), 1, 2),"");

                    } else {
                        gangsPlugin.getMessageManager().sendMessage("not-enough-exp-gang", player);
                        this.close();
                    }

                });

        this.addItem("sell", PKeys.set("multiplier", "exp"),
                PValues.set(gang.getSellMultiplier(), gang.getUpgradeCost(GangUpgrade.SELL_MULTIPLIER), 1, 2))
                .setAction((menuItem, clickType) -> {
                    if (gMember.getGangRole() == GangRole.MEMBER || gMember.getGangRole() == GangRole.OFFICER) {
                        gangsPlugin.getMessageManager().sendMessage("not-perms-gang", player);
                        this.close();
                        return;
                    }

                    if (gang.getSellMultiLevel() == gang.getMaxSellLevel()) {
                        gangsPlugin.getMessageManager().sendMessage("max-upgrade-level-reached", player);
                        return;
                    }

                    if (gang.getExp() >= gang.getUpgradeCost(GangUpgrade.SELL_MULTIPLIER)) {
                        gang.setExp(gang.getExp() - gang.getUpgradeCost(GangUpgrade.SELL_MULTIPLIER));
                        gangsPlugin.getMessageManager().sendMessage("sell-upgrade", player,
                                gang.getUpgradeCost(GangUpgrade.SELL_MULTIPLIER));
                        gang.gangUpgrade(GangUpgrade.SELL_MULTIPLIER);

                        injectPlaceholders(player,"sell", PKeys.set("multiplier", "exp"),
                                PValues.set(gang.getSellMultiplier(), gang.getUpgradeCost(GangUpgrade.SELL_MULTIPLIER), 1, 2),"");

                    } else {
                        gangsPlugin.getMessageManager().sendMessage("not-enough-exp-gang", player);
                        this.close();
                    }
                });

        this.addItem("mine", PKeys.set("mine", "exp"),
                PValues.set(gang.getMineBlockLevel(), gang.getUpgradeCost(GangUpgrade.MINE), 1, 2))
                .setAction((menuItem, clickType) -> {
                    if (gMember.getGangRole() == GangRole.MEMBER || gMember.getGangRole() == GangRole.OFFICER) {
                        gangsPlugin.getMessageManager().sendMessage("not-perms-gang", player);
                        this.close();
                        return;
                    }

                    if (gang.getMineBlockLevel() == gang.getMaxMineLevel()) {
                        gangsPlugin.getMessageManager().sendMessage("max-upgrade-level-reached", player);
                        return;
                    }

                    if (gang.getExp() >= gang.getUpgradeCost(GangUpgrade.MINE)) {
                        gang.gangUpgrade(GangUpgrade.MINE);
                        gang.setExp(gang.getExp() - gang.getUpgradeCost(GangUpgrade.MINE));
                        gangsPlugin.getMessageManager().sendMessage("mine-upgrade", player,
                        gang.getUpgradeCost(GangUpgrade.MINE));

                        injectPlaceholders(player,"mine", PKeys.set("mine", "exp"),
                                PValues.set(gang.getMineBlockLevel(), gang.getUpgradeCost(GangUpgrade.MINE), 1, 2),"");

                    } else {
                        gangsPlugin.getMessageManager().sendMessage("not-enough-exp-gang", player);
                    }
                });

        this.addItem("members", PKeys.set("members", "exp", "amount"), PValues.set(gang.getMaxPlayersLevel(),
                gang.getUpgradeCost(GangUpgrade.GANG_MEMBERS), gang.getMaxPlayersLevel(), 1, 2, 3))
                .setAction((menuItem, clickType) -> {
                    if (gMember.getGangRole() == GangRole.MEMBER || gMember.getGangRole() == GangRole.OFFICER) {
                        gangsPlugin.getMessageManager().sendMessage("not-perms-gang", player);
                        this.close();
                        return;
                    }
                    if (gang.getUpgradeCost(GangUpgrade.GANG_MEMBERS) == 0) {
                        gangsPlugin.getMessageManager().sendMessage("max-upgrade-level-reached", player);
                        return;
                    }
                    
                    if (gang.getExp() >= gang.getUpgradeCost(GangUpgrade.GANG_MEMBERS)) {
                        gang.setExp(gang.getExp() - gang.getUpgradeCost(GangUpgrade.GANG_MEMBERS));
                        gangsPlugin.getMessageManager().sendMessage("members-upgrade", player,
                                gang.getUpgradeCost(GangUpgrade.GANG_MEMBERS));
                        gang.gangUpgrade(GangUpgrade.GANG_MEMBERS);

                        injectPlaceholders(player,"members", PKeys.set("members", "exp", "amount"), PValues.set(gang.getMaxPlayersLevel(),
                                gang.getUpgradeCost(GangUpgrade.GANG_MEMBERS), gang.getMaxPlayersLevel(), 1, 2, 3),"");

                    } else {
                        gangsPlugin.getMessageManager().sendMessage("not-enough-exp-gang", player);
                        this.close();
                    }
                });

        this.addItem("gblock", PKeys.set("gblock", "exp"),
                PValues.set(gang.getGuildBlockPercentLevel(), gang.getUpgradeCost(GangUpgrade.GBLOCK)))
                .setAction((menuItem, clickType) -> {
                    if (gMember.getGangRole() == GangRole.MEMBER || gMember.getGangRole() == GangRole.OFFICER) {
                        gangsPlugin.getMessageManager().sendMessage("not-perms-gang", player);
                        this.close();
                        return;
                    }
                    if (gang.getGuildBlockPercentLevel() == 5) {
                        gangsPlugin.getMessageManager().sendMessage("max-upgrade-level-reached", player);
                        return;
                    }
                    if (gang.getExp() >= gang.getUpgradeCost(GangUpgrade.GBLOCK)) {
                        gang.setExp(gang.getExp() - gang.getUpgradeCost(GangUpgrade.GBLOCK));
                        gangsPlugin.getMessageManager().sendMessage("gblock-upgrade", player,
                                gang.getUpgradeCost(GangUpgrade.GBLOCK));
                        gang.gangUpgrade(GangUpgrade.GBLOCK);

                        injectPlaceholders(player,"gblock", PKeys.set("gblock", "exp"),
                                PValues.set(gang.getGuildBlockPercentLevel(), gang.getUpgradeCost(GangUpgrade.GBLOCK)),"");

                    } else {
                        gangsPlugin.getMessageManager().sendMessage("not-enough-exp-gang", player);
                        this.close();
                    }
                });

        ItemStack grayGlass = new ItemStack(Material.STAINED_GLASS_PANE,1, (byte) 7);
        this.setFillerItem(grayGlass);
    }
}
