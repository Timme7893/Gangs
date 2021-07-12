package net.timme7893.gangs.gangs.debuff;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;

public class DebuffManager {

    private GangsPlugin gangsPlugin;

    public DebuffManager(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
    }

    public boolean checkGang(Gang gang) {
        return true;
    }

    public void executeDebuff(DebuffType type, Gang sender, Gang enemy) {
        if (!checkGang(sender)) {
            // send wait message
            return;
        }
    }
}
