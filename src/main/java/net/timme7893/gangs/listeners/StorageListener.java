package net.timme7893.gangs.listeners;

import net.timme7893.gangs.menus.GangMenu;
import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.utils.Present;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.event.inventory.InventoryCloseEvent;

public class StorageListener implements Listener {

    private GangsPlugin gangsPlugin;

    public StorageListener(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
        System.out.println("[Gangs] UPDATE: StorageListener loaded.");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if (e.getWhoClicked() instanceof Player) {
            Player player = (Player) e.getWhoClicked();
            if (Present.isPlayer(player)) {
                Gang gang = gangsPlugin.getPlayerManager().getMember(player).getGang();

                if (!e.getInventory().getName().equals(gang.getStorageMenu().getInventory().getName())) {
                    return;
                }

                /*int allItems = 0;

                for (ItemStack item : e.getInventory().getContents()) {
                    if (item != null && item.getType() != Material.AIR) {
                        allItems = allItems + item.getAmount();
                    }
                }*/

                if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.STAINED_GLASS_PANE
                        && e.getCurrentItem().getDurability() == (short) 0) {
                    e.setCancelled(true);
                    gangsPlugin.getMessageManager().sendMessage("storage-max-size", player);
                    return;
                }

            }
        }
    }

    @EventHandler
    public void closeStorage(InventoryCloseEvent e) {

        if (e.getPlayer() instanceof Player) {
            Player player = (Player) e.getPlayer();
            if (Present.isPlayer(player)) {
                Gang gang = gangsPlugin.getPlayerManager().getMember(player).getGang();

                if (!e.getInventory().getName().equals(gang.getStorageMenu().getInventory().getName())) {
                    return;
                }

                new GangMenu(player);
            }
        }

    }

}
