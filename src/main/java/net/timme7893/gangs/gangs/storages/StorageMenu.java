package net.timme7893.gangs.gangs.storages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Setter;
import net.timme7893.gangs.gangs.Gang;

public class StorageMenu {

    private Gang gang;
    private Inventory inventory;
    @Setter
    private List<ItemStack> items = new ArrayList<>();

    public StorageMenu(Gang gang) {
        this.gang = gang;
        String title = Text.format(Archive.get("storage-menu").asString("title"));
        inventory = Bukkit.createInventory(null, 54, title);
        loadData();
        loadInventory();
    }

    public void saveData() {
        int i = 0;
        items = Arrays
                .stream(inventory.getContents()).filter(item -> item != null
                        && !(item.getType() == Material.STAINED_GLASS_PANE && item.getDurability() == (short) 0))
                .collect(Collectors.toList());

        gang.getGangStorageFile().getData().set("Item", null);
        for (ItemStack item : items) {
            gang.getGangStorageFile().getData().set("Item." + i, item);
            System.out.println(gang.getGangName() + " -> " + i + " -> " + item.getType().name() + " (" + items.size() + ")");
            
            i++;
        }
        gang.getGangStorageFile().saveData();
    }

    public void loadData() {
        items.clear();

        if (gang.getGangStorageFile().getData().getKeys(false).contains("Item")) {
            ConfigurationSection section = gang.getGangStorageFile().getData().getConfigurationSection("Item");
            for (String key : section.getKeys(false)) {
                items.add(gang.getGangStorageFile().getData().getItemStack("Item." + key));
            }
        }
    }

    public void loadInventory() {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, new ItemStack(Material.AIR));
        }
        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        for (int i = gang.getStorageSlots(); i < inventory.getSize(); i++) {
            inventory.setItem(i, glass);
        }
        for (ItemStack itemStack : items) {
            inventory.addItem(itemStack);
        }
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public Gang getGang() {
        return gang;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
