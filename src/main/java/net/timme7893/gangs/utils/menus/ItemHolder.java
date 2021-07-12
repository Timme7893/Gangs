package net.timme7893.gangs.utils.menus;

import lombok.Builder;
import lombok.Singular;

import net.timme7893.gangs.utils.text.Text;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public class ItemHolder {

    private final Material type;
    private final byte data;
    private final int amount;
    private final String name;
    private final List<String> lore;
    @Singular
    private final Set<ItemFlag> itemFlags;
    private ItemStack itemStack;

    /**
     * Turn all variables into an {@link ItemStack}
     *
     * @return the {@link ItemStack} that has been generated from variables
     */
    public ItemStack getItem() {
        if (this.itemStack == null) {
            ItemStack itemStack = new ItemStack(this.type, this.amount, this.data);
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (Objects.isNull(itemMeta))
                return this.itemStack = itemStack;
            if (Objects.nonNull(this.name))
                itemMeta.setDisplayName(Text.format(this.name));
            if (!this.lore.isEmpty())
                itemMeta.setLore(Text.format(lore));
            itemStack.setItemMeta(itemMeta);
            this.itemStack = itemStack;
        }
        return this.itemStack;
    }

    /**
     * Re-create the {@link ItemStack} from variables
     *
     * @return
     */
    public ItemStack reSetGetItem() {
        this.itemStack = null;
        return this.getItem();
    }

    public static class ItemHolderBuilder {

        public ItemHolderBuilder lore(String... strings) {
            this.lore.addAll(Arrays.asList(strings));
            return this;
        }

        public ItemHolderBuilder replace(String string, Object replacer) {
            this.name(this.name.replace(string, Text.format(Objects.toString(replacer))));
            this.lore.addAll(this.lore.stream().map(line -> line.replace(string, Text.format(Objects.toString(replacer)))).collect(Collectors.toList()));
            return this;
        }
    }
}
