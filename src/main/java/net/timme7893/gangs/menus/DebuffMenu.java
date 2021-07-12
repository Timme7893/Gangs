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
import net.timme7893.gangs.gangs.GangRole;
import net.timme7893.gangs.gangs.debuff.DebuffType;
import net.timme7893.gangs.gangs.members.GMember;

public class DebuffMenu extends AbstractMenu {

    private GangsPlugin gangsPlugin;
    private GMember gMember;
    private Player player;
    private File menuFile;
    private Gang gang;
    private Gang enemy;

    public DebuffMenu(Player player, Gang enemy) {
        super(GangsPlugin.getInstance(), player, Archive.get("enemies-menu"),
                Text.format(Archive.get("enemies-menu").asString("title"), PKeys.set("name"),
                        PValues.set(enemy.getGangName(), 1)),
                Archive.get("enemies-menu").asInt("rows"));
        this.player = player;
        this.gangsPlugin = GangsPlugin.getInstance();
        this.menuFile = Archive.get("enemies-menu");
        this.gMember = gangsPlugin.getPlayerManager().getMember(player);
        this.gang = gMember.getGang();
        this.enemy = enemy;
        this.setup();
        // this.show();
    }

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

        this.addItem(menuFile, "mine-nuker").setAction((menuItem, clickType) -> {
            this.close();

            if (gMember.getGangRole() != GangRole.LEADER && gMember.getGangRole() != GangRole.ADMIN) {
                gangsPlugin.getMessageManager().sendMessage("no-permissions", player);
                return;
            }

            gangsPlugin.getDebuffManager().executeDebuff(DebuffType.MINE_NUKER, gang, enemy);
        });

        this.addItem(menuFile, "factory-disable").setAction((menuItem, clickType) -> {
            this.close();

            if (gMember.getGangRole() != GangRole.LEADER && gMember.getGangRole() != GangRole.ADMIN) {
                gangsPlugin.getMessageManager().sendMessage("no-permissions", player);
                return;
            }

            gangsPlugin.getDebuffManager().executeDebuff(DebuffType.FACTORIES_DISABLER, gang, enemy);
        });

        this.addItem(menuFile, "exp-leaching").setAction((menuItem, clickType) -> {
            this.close();

            if (gMember.getGangRole() != GangRole.LEADER && gMember.getGangRole() != GangRole.ADMIN) {
                gangsPlugin.getMessageManager().sendMessage("no-permissions", player);
                return;
            }

            gangsPlugin.getDebuffManager().executeDebuff(DebuffType.EXP_LEACHING, gang, enemy);
        });

        this.setFillerItem(new ItemStack(Material.STAINED_GLASS_PANE, 1));

    }

}
