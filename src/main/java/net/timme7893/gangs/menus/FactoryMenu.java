package net.timme7893.gangs.menus;

import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.file.File;
import net.timme7893.gangs.utils.menus.AbstractMenu;
import net.timme7893.gangs.utils.text.PKeys;
import net.timme7893.gangs.utils.text.PValues;
import net.timme7893.gangs.utils.text.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.members.GMember;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.inventory.ItemStack;

public class FactoryMenu extends AbstractMenu {

    private GangsPlugin gangsPlugin;
    private GMember gMember;
    private Player player;
    private File menuFile;
    private Gang gang;

    private String bought;
    private String forsale;
    private String settings;

    private Economy economy;

    public FactoryMenu(Player player) {
        super(GangsPlugin.getInstance(), player, Archive.get("factory-menu"),
                Archive.get("factory-menu").asString("title"), Archive.get("factory-menu").asInt("rows"));
        this.player = player;
        this.gangsPlugin = GangsPlugin.getInstance();
        this.menuFile = Archive.get("factory-menu");
        this.gMember = gangsPlugin.getPlayerManager().getMember(player);
        this.gang = gMember.getGang();

        this.bought = Text.format(menuFile.asString("status.bought"));
        this.forsale = Text.format(menuFile.asString("status.for-sale"));
        this.settings = Text.format(menuFile.asString("status.settings"));

        this.economy = gangsPlugin.getEconomy();

        this.setup();
        // this.show();
    }

    private int slot = 1;
    private int row = 2;

    @Override
    protected void configure() {
        /*
         * this.addItem(menuFile, "exit").setAction((menuItem, clickType) -> { new
         * GangMenu(player); });
         */

        this.setCloseAction(new CloseAction() {
            @Override
            public void onClose() {
                openNewMenu(new GangMenu(player));
            }
        });

        gangsPlugin.getFactoriesManager().getFactories().values().stream().forEach(factory -> {

            if (!factory.isBought()) {

                slot++;
                if (slot > 8) {
                    slot = 2;
                    row++;
                }

                if (row > 5) {
                    return;
                }

                this.addItem(menuFile, "items.factory",
                        PKeys.set("item", "id", "name", "rent", "rate", "quality", "status", "blocks"),
                        PValues.set(factory.getItem(), factory.getId(), "", factory.getRent(),
                                factory.getProductionRate(), factory.getQuality(), forsale,
                                factory.getUnclaimedBlocks(), 1, 2, 3, 4, 5, 6, 7),
                        row, slot).setAction((menuItem, clickType) -> {
                            this.close();
                            // BUY FACTORY

                            if (gangsPlugin.getFactoriesManager().getFactories().values().stream()
                                    .filter(streamFactory -> streamFactory.getOwner() != null
                                            && streamFactory.getOwner().getId() == gang.getId())
                                    .count() >= gangsPlugin.getConfig().getInt("factories.max-amount-per-gang")) {
                                gangsPlugin.getMessageManager().sendMessage("too-much-factories", player,
                                        gangsPlugin.getConfig().getInt("factories.max-amount-per-gang"));
                                return;
                            }
                            if (economy.getBalance(player) >= factory.getRent()) {
                                economy.withdrawPlayer(player, factory.getRent());
                                factory.setOwner(gang);
                                factory.setDirty(true);
                                new FactoryMenu(player);
                                gangsPlugin.getMessageManager().sendMessage("bought-factory", player, factory.getId());
                            }
                        });
            }
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

    /*

    if (factory.getOwner() == gang) {
                    this.addItem(menuFile, "items.factory",
                            PKeys.set("item", "id", "name", "rent", "rate", "quality", "status", "blocks"),
                            PValues.set(factory.getItem(), factory.getId(), "&f(" + gang.getGangName() + "&f)",
                                    factory.getRent(), factory.getProductionRate(), factory.getQuality(), settings,
                                    factory.getUnclaimedBlocks(), 1, 2, 3, 4, 5, 6, 7),
                            row, slot).setAction((menuItem, clickType) -> {
                                this.close();
                                // OPEN SETTINGS MENU
                                openNewMenu(new FactoryStatsMenu(player, factory));
                            });
                } else {
                    String boughtText = Text.format(bought, PKeys.set("name"),
                            PValues.set(factory.getOwner().getGangName(), 1));
                    this.addItem(menuFile, "items.factory",
                            PKeys.set("item", "id", "name", "rent", "rate", "quality", "status", "blocks"),
                            PValues.set(factory.getItem(), factory.getId(),
                                    "&f(" + factory.getOwner().getGangName() + "&f)", factory.getRent(),
                                    factory.getProductionRate(), factory.getQuality(), boughtText,
                                    factory.getUnclaimedBlocks(), 1, 2, 3, 4, 5, 6, 7),
                            row, slot).setAction((menuItem, clickType) -> {
                            });
                }
            } else {
     */
}
