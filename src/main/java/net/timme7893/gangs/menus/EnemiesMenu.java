package net.timme7893.gangs.menus;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.members.GMember;
import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.file.File;
import net.timme7893.gangs.utils.menus.AbstractMenu;
import net.timme7893.gangs.utils.text.PKeys;
import net.timme7893.gangs.utils.text.PValues;

import org.bukkit.entity.Player;

public class EnemiesMenu extends AbstractMenu {

    private GangsPlugin gangsPlugin;
    private GMember gMember;
    private Player player;
    private File menuFile;
    private Gang gang;

    public EnemiesMenu(Player player) {
        super(GangsPlugin.getInstance(), player, Archive.get("enemies-menu"),
                Archive.get("enemies-menu").asString("title"), Archive.get("enemies-menu").asInt("rows"));
        this.player = player;
        this.gangsPlugin = GangsPlugin.getInstance();
        this.menuFile = Archive.get("enemies-menu");
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

        gang.getEnemies().stream().forEach(enemy -> {
            slot++;
            if (slot > 9) {
                slot = 1;
                row++;
            }

            if (row == 6 && slot > 8) {
                return;
            }

            this.addItem(menuFile, "enemy", PKeys.set("name"), PValues.set(enemy.getEnemy().getGangName(), 1), row,
                    slot).setAction((menuItem, clickType) -> {
                        this.close();
                        new DebuffMenu(player, enemy.getEnemy());
                    });
        });

    }
}
