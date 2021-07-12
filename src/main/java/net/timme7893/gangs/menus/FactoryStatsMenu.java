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
import net.timme7893.gangs.gangs.factories.Factory;
import net.timme7893.gangs.gangs.members.GMember;
import net.timme7893.gangs.gangs.utils.GBlock;

public class FactoryStatsMenu extends AbstractMenu {

    private GangsPlugin gangsPlugin;
    private GMember gMember;
    private Player player;
    private File menuFile;
    private Gang gang;
    private Factory factory;

    public FactoryStatsMenu(Player player, Factory factory) {
        super(GangsPlugin.getInstance(), player, Archive.get("factory-stats-menu"),
                Text.format(Archive.get("factory-stats-menu").asString("title"), PKeys.set("id"),
                        PValues.set(factory.getId(), 1)),
                Archive.get("factory-stats-menu").asInt("rows"));
        this.player = player;
        this.gangsPlugin = GangsPlugin.getInstance();
        this.menuFile = Archive.get("factory-stats-menu");
        this.gMember = gangsPlugin.getPlayerManager().getMember(player);
        this.gang = gMember.getGang();
        this.factory = factory;

        this.setup();
        // this.show();
    }

    @Override
    protected void configure() {
        /*
         * this.addItem(menuFile, "exit").setAction((menuItem, clickType) -> { new
         * FactoryMenu(player); });
         */

        this.setCloseAction(new CloseAction() {
            @Override
            public void onClose() {
                openNewMenu(new OwnFactoriesMenu(player));
            }
        });

        this.addItem("production", PKeys.set("rate", "rate-upgrade"),
                PValues.set(factory.getProductionRate(), factory.getRateUpgradeCost(), 1, 2))
                .setAction((menuItem, clickType) -> {


                    if (gang.getExp() >= factory.getRateUpgradeCost()) {

                        gang.setExp(gang.getExp() - factory.getRateUpgradeCost());
                        gangsPlugin.getMessageManager().sendMessage("upgraded-rate", player,
                        factory.getRateUpgradeCost());
                        factory.upgradeRate();

                        injectPlaceholders(player,"production",PKeys.set("rate", "rate-upgrade"),
                                PValues.set(factory.getProductionRate(), factory.getRateUpgradeCost(), 1, 2),"");

                    } else {
                        gangsPlugin.getMessageManager().sendMessage("not-enough-exp", player);
                    }


                    new FactoryStatsMenu(player,factory);
                });

        this.addItem("info", PKeys.set("name", "rent-status", "blocks", "id"), PValues.set(gang.getGangName(),
                factory.getRentStatus(), factory.getUnclaimedBlocks(), factory.getId(), 1, 2, 3, 4))
                .setAction((menuItem, clickType) -> {

                    factory.setClaimedBlocks(0);
                    gangsPlugin.getMessageManager().sendMessage("claimed-blocks", player);

                    injectPlaceholders(player,"info", PKeys.set("name", "rent-status", "blocks", "id"), PValues.set(gang.getGangName(),
                            factory.getRentStatus(), factory.getUnclaimedBlocks(), factory.getId(), 1, 2, 3, 4),"");

                    if (factory.getBlockQuality() == GBlock.SLIME) {
                        gang.setSlimeBlocks(gang.getSlimeBlocks() + factory.getUnclaimedBlocks());
                    } else if (factory.getBlockQuality() == GBlock.LANTERN) {
                        gang.setLanternBlocks(gang.getLanternBlocks() + factory.getUnclaimedBlocks());
                    } else if (factory.getBlockQuality() == GBlock.PRISMARINE) {
                        gang.setPrismarineBlocks(gang.getPrismarineBlocks() + factory.getUnclaimedBlocks());
                    }
                });

        this.addItem("quality", PKeys.set("item", "quality", "quality-upgrade"),
                PValues.set(factory.getBlockItem(), factory.getQuality(), factory.getQualityUpgradeCost(), 1, 2, 3))
                .setAction((menuItem, clickType) -> {

                    if (factory.getBlockQuality() == GBlock.PRISMARINE) {
                        gangsPlugin.getMessageManager().sendMessage("max-upgrade-level-reached",player);
                        return;
                    }

                    if (gang.getExp() >= factory.getQualityUpgradeCost()) {
                            gang.setExp(gang.getExp() - factory.getQualityUpgradeCost());
                            gangsPlugin.getMessageManager().sendMessage("upgraded-quality", player,
                            factory.getRateUpgradeCost());
                            factory.upgradeQuality();

                        injectPlaceholders(player,"quality", PKeys.set("item", "quality", "quality-upgrade"),
                                PValues.set(factory.getBlockItem(), factory.getQuality(), factory.getQualityUpgradeCost(), 1, 2, 3),"");

                    } else {
                        gangsPlugin.getMessageManager().sendMessage("not-enough-exp", player);
                    }
                });

        this.addItem("rent", PKeys.set("rent"), PValues.set(factory.getRent(),1)).setAction((menuItem, clickType) -> {

            if (!factory.hasPaid()) {

                if (gangsPlugin.getEconomy().getBalance(player) >= factory.getRent()) {

                    gangsPlugin.getEconomy().withdrawPlayer(player, factory.getRent());
                    gangsPlugin.getMessageManager().sendMessage("rent-paid", player);
                    factory.setPaidRent(true);

                    injectPlaceholders(player,"rent", PKeys.set("rent"), PValues.set(factory.getRent(),1),"");
                } else {
                    gangsPlugin.getMessageManager().sendMessage("not-enough-money", player);
                }
            } else {
                gangsPlugin.getMessageManager().sendMessage("already-paid", player);
            }
        });

        this.addItem(menuFile, "sale").setAction((menuItem, clickType) -> {

            factory.setOwner(null);
            gangsPlugin.getMessageManager().sendMessage("factory-sold", player);
            openNewMenu(new FactoryMenu(player));

        });

        ItemStack grayGlass = new ItemStack(Material.STAINED_GLASS_PANE,1, (byte) 7);
        this.setFillerItem(grayGlass);
    }
}
