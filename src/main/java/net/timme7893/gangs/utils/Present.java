package net.timme7893.gangs.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.timme7893.gangs.GangsPlugin;

public class Present {

    private static GangsPlugin gangsPlugin;

    public Present(GangsPlugin gangsPlugin) {
        Present.gangsPlugin = gangsPlugin;
    }

    // Function: check if Player is present in the database.
    public static boolean isPlayer(Player player) {
        return gangsPlugin.getPlayerManager().getMember(player) != null;
    }

    public static boolean isWorld(String name) {
        return Bukkit.getWorld(name) != null;
    }

    public static boolean isGang(int gangId) {
        return gangsPlugin.getGangsManager().getGangWithId(gangId) != null;
    }

    public static boolean isSimilar(ItemStack one, ItemStack two) {
        ItemMeta oneMeta = one.getItemMeta();
        ItemMeta twoMeta = two.getItemMeta();

        if (one.getType() != two.getType() ||
                one.getDurability() != two.getDurability()) {
            return false;
        }

        if (!one.getEnchantments().equals(two.getEnchantments())) {
            return false;
        }

        if (oneMeta instanceof SkullMeta && twoMeta instanceof SkullMeta) {
            // Fixed deprecation
            String oneOwner = ((SkullMeta) twoMeta).getOwningPlayer().getName();
            String twoOwner = ((SkullMeta) twoMeta).getOwningPlayer().getName();
            if (oneOwner != null && !oneOwner.equals(twoOwner)) {
                return false;
            }
        }

        if (oneMeta.hasDisplayName() && twoMeta.hasDisplayName()) {
            if (!oneMeta.getDisplayName().equals(twoMeta.getDisplayName())) {
                return false;
            }
        }

        if (oneMeta.hasLore() && twoMeta.hasLore()) {
            List<String> oneLore = oneMeta.getLore();
            List<String> twoLore = twoMeta.getLore();
            if (oneLore.size() != twoLore.size()) {
                return false;
            }

            for (int i = 0; i < oneLore.size(); i++) {
                String oneLine = oneLore.get(i);
                String twoLine = twoLore.get(i);

                if (oneLine != null && !oneLine.equals(twoLine)) {
                    return false;
                }
            }
        }

        return oneMeta.hasDisplayName() == twoMeta.hasDisplayName() &&
                oneMeta.hasLore() && twoMeta.hasLore();
    }
}
