package net.timme7893.gangs.commands.admin;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.AGangCommand;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.utils.Booster;
import org.bukkit.command.CommandSender;

public class GangGiveBoosterCommand implements AGangCommand {

    @Override
    public String getId() {
        return "givebooster";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, CommandSender sender, String[] args) {

        if (args.length < 4) {
            gangsPlugin.getMessageManager().sendMessage("use-givebooster", sender);
            return;
        }

        String gangName = args[1];
        if (gangsPlugin.getGangsManager().getGangWithName(gangName) == null) {
            gangsPlugin.getMessageManager().sendMessage("gang-not-exists", sender);
            return;
        }

        Gang gang = gangsPlugin.getGangsManager().getGangWithName(gangName);

        if (getDouble(args[2]) == 0) {
            gangsPlugin.getMessageManager().sendMessage("use-valid-double", sender);
            return;
        }
        double value = getDouble(args[2]);

        if (getInteger(args[3]) == -1) {
            gangsPlugin.getMessageManager().sendMessage("invalid-integer", sender);
            return;
        }

        int minutes = getInteger(args[3]);

        Booster booster;

        if (minutes == 0) {
            booster = new Booster(gang, false, 0, value, true);
        } else {
            booster = new Booster(gang, true, minutes, value, true);
        }

        gangsPlugin.getMessageManager().sendMessage("booster-added", sender, value, gang.getGangName());
    }

    public double getDouble(String id) {
        try {
            return Double.parseDouble(id);
        } catch (Exception ex) {
            return 0;
        }
    }

    public int getInteger(String text) {
        try {
            return Integer.parseInt(text);
        } catch (Exception ex) {
            return -1;
        }
    }
}
