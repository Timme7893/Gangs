package net.timme7893.gangs.menus;

import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.file.File;
import net.timme7893.gangs.utils.menus.AbstractMenu;
import net.timme7893.gangs.utils.text.PKeys;
import net.timme7893.gangs.utils.text.PValues;
import org.bukkit.entity.Player;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.GangRole;
import net.timme7893.gangs.gangs.members.GMember;

public class AllyMenu extends AbstractMenu {

    private GangsPlugin gangsPlugin;
    private GMember gMember;
    private Player player;
    private File menuFile;
    private Gang gang;

    public AllyMenu(Player player) {
        super(GangsPlugin.getInstance(), player, Archive.get("allies-menu"),
                Archive.get("allies-menu").asString("title"), Archive.get("allies-menu").asInt("rows"));
        this.player = player;
        this.gangsPlugin = GangsPlugin.getInstance();
        this.menuFile = Archive.get("allies-menu");
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

        for (Gang ally : gang.getAllies()) {
            slot++;
            if (slot > 9) {
                slot = 1;
                row++;
            }

            this.addItem(menuFile, "ally", PKeys.set("name"), PValues.set(ally.getGangName(), 1), row, slot)
                    .setAction((menuItem, clickType) -> {
                        this.close();

                        if (gMember.getGangRole() != GangRole.LEADER && gMember.getGangRole() != GangRole.ADMIN) {
                            gangsPlugin.getMessageManager().sendMessage("no-permissions", player);
                            return;
                        }
                        // gangsPlugin.getBondManager().resolveAlly(gang,);
                    });
        }
    }
}
