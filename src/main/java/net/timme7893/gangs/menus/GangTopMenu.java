package net.timme7893.gangs.menus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.file.File;
import net.timme7893.gangs.utils.menus.AbstractMenu;
import net.timme7893.gangs.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;

public class GangTopMenu extends AbstractMenu {

    private GangsPlugin gangsPlugin;
    private Player player;
    private File menuFile;

    public GangTopMenu(Player player) {
        super(GangsPlugin.getInstance(), player, Archive.get("gang-top-menu"),
                Archive.get("gang-top-menu").asString("title"), Archive.get("gang-top-menu").asInt("rows"));
        this.player = player;
        this.gangsPlugin = GangsPlugin.getInstance();
        this.menuFile = Archive.get("gang-top-menu");
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

        HashMap<Integer, Gang> gangs = gangsPlugin.getGangTopManager().getGangsTop();

        Gang first = gangs.get(1);
        Gang second = gangs.get(2);
        Gang third = gangs.get(3);

        if (first != null) {
            this.addItem(getGangItem(1, first.getGangLeader(), first.getGangName(), first.getTotalPoints(),
                    first.getGangMemberList().size()), 1, 5).setAction((menuItem, clickType) -> {
                    });
        }

        if (second != null) {
            this.addItem(getGangItem(2, second.getGangLeader(), second.getGangName(), second.getTotalPoints(),
                    second.getGangMemberList().size()), 2, 4).setAction((menuItem, clickType) -> {
                    });
        }

        if (third != null) {
            this.addItem(getGangItem(3, third.getGangLeader(), third.getGangName(), third.getTotalPoints(),
                    third.getGangMemberList().size()), 2, 6).setAction((menuItem, clickType) -> {
                    });
        }

        ItemStack grayGlass = new ItemStack(Material.STAINED_GLASS_PANE,1, (byte) 7);
        this.setFillerItem(grayGlass);

    }

    private ItemStack getGangItem(int position, UUID gangOwner, String gangName, int points, int members) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(gangOwner));

        meta.setDisplayName(Text.format(menuFile.getFileConfiguration().getString("msg.name")
                .replace("%gang-name%", gangName).replace("%position%", "" + position)));
        meta.setLore(Arrays.asList(Text.format(menuFile.getFileConfiguration().getString("msg.lore")
                .replace("%gang-points%", "" + points).replace("%gang-members%", "" + members))));
        skull.setItemMeta(meta);
        return skull;
    }

}
