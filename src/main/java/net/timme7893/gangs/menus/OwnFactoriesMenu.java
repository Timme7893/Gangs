package net.timme7893.gangs.menus;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.members.GMember;
import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.file.File;
import net.timme7893.gangs.utils.menus.AbstractMenu;
import net.timme7893.gangs.utils.text.PKeys;
import net.timme7893.gangs.utils.text.PValues;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OwnFactoriesMenu extends AbstractMenu {

    private GangsPlugin gangsPlugin;
    private GMember gMember;
    private Player player;
    private File menuFile;
    private Gang gang;

    public OwnFactoriesMenu(Player player) {
        super(GangsPlugin.getInstance(), player, Archive.get("own-factories-menu"), Archive.get("own-factories-menu").asString("title"),
                Archive.get("own-factories-menu").asInt("rows"));
        this.player = player;
        this.gangsPlugin = GangsPlugin.getInstance();
        this.menuFile = Archive.get("own-factories-menu");
        this.gMember = gangsPlugin.getPlayerManager().getMember(player);
        this.gang = gMember.getGang();
        this.setup();
        System.out.println("Start opening...");
        this.show();
    }

    private int slot = 3;

    @Override
    protected void configure() {

        this.setCloseAction(new CloseAction() {
            @Override
            public void onClose() {
                openNewMenu(new GangMenu(player));
            }
        });

        gangsPlugin.getFactoriesManager().getFactoriesByGang(gang).forEach(factory -> {
            System.out.println(factory.getName() + " setting up.");
            slot++;
            this.addItem(menuFile, "items.factory",
                    PKeys.set("item", "id", "name", "rent", "rate", "quality", "blocks"),
                    PValues.set(factory.getItem(), factory.getId(), "&f(" + gang.getGangName() + "&f)",
                            factory.getRent(), factory.getProductionRate(), factory.getQuality(),
                            factory.getUnclaimedBlocks(), 1, 2, 3, 4, 5, 6),
                    2, slot).setAction((menuItem, clickType) -> {
                this.close();
                // OPEN SETTINGS MENU
                openNewMenu(new FactoryStatsMenu(player, factory));
            });
        });

        ItemStack yellowGlass = new ItemStack(Material.STAINED_GLASS_PANE,1, (byte) 4);
        ItemStack grayGlass = new ItemStack(Material.STAINED_GLASS_PANE,1, (byte) 7);

        this.addItem(yellowGlass,1,1);
        this.addItem(yellowGlass,1,2);
        this.addItem(yellowGlass,1,8);
        this.addItem(yellowGlass,1,9);

        this.addItem(grayGlass,1,3);
        this.addItem(grayGlass,1,4);
        this.addItem(grayGlass,1,5);
        this.addItem(grayGlass,1,6);
        this.addItem(grayGlass,1,7);

        this.addItem(grayGlass,3,3);
        this.addItem(grayGlass,3,4);
        this.addItem(grayGlass,3,5);
        this.addItem(grayGlass,3,6);
        this.addItem(grayGlass,3,7);

        this.addItem(grayGlass,2,2);
        this.addItem(grayGlass,2,3);
        this.addItem(grayGlass,2,7);
        this.addItem(grayGlass,2,8);

        this.addItem(yellowGlass,2,1);
        this.addItem(yellowGlass,2,9);

        this.addItem(yellowGlass,3,1);
        this.addItem(yellowGlass,3,2);
        this.addItem(yellowGlass,3,8);
        this.addItem(yellowGlass,3,9);

    }
}
