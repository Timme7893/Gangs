package net.timme7893.gangs.menus;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.file.File;
import net.timme7893.gangs.utils.menus.AbstractMenu;
import net.timme7893.gangs.utils.text.PKeys;
import net.timme7893.gangs.utils.text.PValues;
import net.timme7893.gangs.utils.text.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.List;

public class InviteMenu extends AbstractMenu {

    private GangsPlugin gangsPlugin;
    private Player player;
    private File menuFile;
    private Gang invitation;

    public InviteMenu(Player player, Gang invitation) {
        super(GangsPlugin.getInstance(), player, Archive.get("invite-menu"),
                Text.format(Archive.get("invite-menu").asString("title"), PKeys.set("gang"), PValues.set(invitation.getGangName(),1)), Archive.get("invite-menu").asInt("rows"));
        this.player = player;
        this.gangsPlugin = GangsPlugin.getInstance();
        this.menuFile = Archive.get("invite-menu");
        this.invitation = invitation;
        this.setup();
        this.show();

        if (player == null) {
            System.out.println(" player is null!");
        } else {
            System.out.println("player: " + player.getName());
        }

        System.out.println("gui opens");
    }

    @Override
    protected void configure() {
        /*
         * this.addItem(menuFile, "exit").setAction((menuItem, clickType) -> { new
         * GangMenu(player); });
         */

        String acceptName = Text.format(menuFile.asString("items.accept.name"));
        List<String> acceptLore = Text.format(menuFile.asList("items.accept.lore"));
        int acceptCode = menuFile.asInt("items.accept.color-code");

        ItemStack acceptItem = new ItemStack(Material.STAINED_GLASS_PANE,1, (byte) acceptCode);
        ItemMeta acceptMeta = acceptItem.getItemMeta();
        acceptMeta.setDisplayName(acceptName);
        acceptMeta.setLore(acceptLore);
        acceptItem.setItemMeta(acceptMeta);

        this.addItem(acceptItem,2,4).setAction((menuItem, clickType) -> {

            this.close();

            if (gangsPlugin.getPlayerManager().getMember(player) != null) {
                System.out.println("test-3");
                gangsPlugin.getMessageManager().sendMessage("member-in-gang",player);
                return;
            }

            Collection<String> broadcastMessage = gangsPlugin.getMessageManager().getMessage("gang-new-member", player.getName());
            for (String message : broadcastMessage) {
                invitation.broadcast(message);
            }
            for (String message : broadcastMessage) {
                player.sendMessage(message);
            }
            gangsPlugin.getPlayerManager().createMember(player,invitation);
        });

        System.out.println("accept added");


        String declineName = Text.format(menuFile.asString("items.decline.name"));
        List<String> declineLore = Text.format(menuFile.asList("items.decline.lore"));
        int declineCode = menuFile.asInt("items.decline.color-code");

        ItemStack declineItem = new ItemStack(Material.STAINED_GLASS_PANE,1, (byte) declineCode);
        ItemMeta declineMeta = declineItem.getItemMeta();
        declineMeta.setDisplayName(declineName);
        declineMeta.setLore(declineLore);
        declineItem.setItemMeta(declineMeta);
        this.addItem(declineItem,2,6).setAction((menuItem, clickType) -> {
            System.out.println("test-4");

            this.close();
        });

        System.out.println("decline added");


        ItemStack grayGlass = new ItemStack(Material.STAINED_GLASS_PANE,1, (byte) 7);
        this.setFillerItem(grayGlass);

        System.out.println("test 5");
    }
}
