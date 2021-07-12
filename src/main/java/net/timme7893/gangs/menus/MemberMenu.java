package net.timme7893.gangs.menus;

import net.timme7893.gangs.utils.text.Text;
import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.file.File;
import net.timme7893.gangs.utils.menus.AbstractMenu;
import net.timme7893.gangs.utils.text.PKeys;
import net.timme7893.gangs.utils.text.PValues;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.GangRole;
import net.timme7893.gangs.gangs.members.GMember;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class MemberMenu extends AbstractMenu {

    private GangsPlugin gangsPlugin;
    private GMember gMember;
    private Player player;
    private File menuFile;
    private Gang gang;

    public MemberMenu(Player player) {
        super(GangsPlugin.getInstance(), player, Archive.get("member-menu"),
                Archive.get("member-menu").asString("title"), Archive.get("member-menu").asInt("rows"));
        this.player = player;
        this.gangsPlugin = GangsPlugin.getInstance();
        this.menuFile = Archive.get("member-menu");
        this.gMember = gangsPlugin.getPlayerManager().getMember(player);
        this.gang = gMember.getGang();
        this.setup();
        // this.show();
    }

    private int slot = 0;
    private int row = 1;

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

        // Filler item can't be used in this context because there could be empty items
        // on purpose
        // this.setFillerItem(new ItemStack(Material.STAINED_GLASS_PANE));
        ItemStack grayGlass = new ItemStack(Material.STAINED_GLASS_PANE,1, (byte) 7);
        for (int i = 1; i <= 9; i++) {
            addItem(grayGlass, 1, i);
        }

        addItem(gang, 0, 2, 1, player);
        addItem(gang, 1, 2, 2, player);
        addItem(gang, 2, 2, 3, player);
        addItem(gang, 3, 2, 4, player);
        addItem(gang, 4, 2, 5, player);
        addItem(gang, 5, 2, 6, player);
        addItem(gang, 6, 2, 7, player);
        addItem(gang, 7, 2, 8, player);
        addItem(gang, 8, 2, 9, player);
        addItem(gang, 9, 3, 5, player);

        for (int i = 1; i <= 9; i++) {
            if (i != 5) {
                addItem(grayGlass, 3, i);
            }
        }

        /*
         * gangsPlugin.getGangInviteManager().getInvitesWithGang(gang).forEach(
         * gangInvite -> {
         * 
         * slot++; if (slot > 9) { slot = 1; row++; }
         * 
         * this.addItem(menuFile, "items.invite", PKeys.set("name"),
         * PValues.set(gangInvite.getPlayer().getName(), 1), row,
         * slot).setAction((menuItem, clickType) -> { this.close();
         * gangsPlugin.getGangInviteManager().removeInvite(gangInvite);
         * gangsPlugin.getMessageManager().sendMessage("invite-revoked", player,
         * gangInvite.getPlayer().getName()); });
         * 
         * });
         */
    }

    public void addItem(Gang gang, int position, int row, int slot, Player player) {
        if (position > gang.getMaxPlayersLevel()) {
            this.addItem(file.getItem(player, "items.not-enough-slots"), row, slot);
        } else if (position >= gang.getGangMemberList().size()) {
            //nothing
        } else {

            GMember member = gang.getGangMemberList().get(position);
            String itemName = menuFile.asString("items.member.name");
            List<String> lore = menuFile.asList("items.member.lore");
            PKeys pKeys = PKeys.set("name", "exp", "blocks", "role", "online");
            PValues pValues = PValues.set(member.getOfflinePlayer().getName(), member.getExpGained(), member.getBlocksBroken(),
                    member.getGangRole().toString().toLowerCase(), member.getOnline(), 1, 2, 3, 4, 5);

            this.addItem(getHeadItem(member.getOfflinePlayer(),itemName,lore, pKeys,pValues),row,slot).setAction((menuItem, clickType) -> {
                        this.close();

                        if (gMember.getGangRole() == GangRole.MEMBER) {
                            gangsPlugin.getMessageManager().sendMessage("no-kick-perms", player);
                            return;
                        }

                        if (member.getGang().getGangLeader().equals(member.getPlayerId())) {
                            gangsPlugin.getMessageManager().sendMessage("cant-kick-leader", player);
                            return;
                        }

                        member.getGang().removeMember(member);
                        member.setGang(null);
                        gangsPlugin.getPlayerManager().deleteMember(member);
                        if (member.isOnline()) {
                            gangsPlugin.getMessageManager().sendMessage("player-kicked", member.getOnlinePlayer());
                        }
                        gangsPlugin.getMessageManager().sendMessage("member-kicked", player,
                                member.getOfflinePlayer().getName());

                        // Reopen GUI to update content
                        show();
                    });
            ;
        }
    }

    private ItemStack getHeadItem(OfflinePlayer owner, String itemName, List<String> lore, PKeys pKeys, PValues pValues) {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        System.out.println("Skull owner: "+ owner.getName());
        skullMeta.setOwningPlayer(owner);
        if (itemName != null)
            skullMeta.setDisplayName(Text.format(itemName,pKeys,pValues));
        if (lore != null && !lore.isEmpty())
            skullMeta.setLore(Text.format(lore,pKeys,pValues));
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }
}
