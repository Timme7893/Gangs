package net.timme7893.gangs.commands.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.timme7893.gangs.gangs.members.GMember;
import net.timme7893.gangs.utils.Present;
import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.AGangCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GangBypassCommand implements AGangCommand {
    
    public static List<UUID> gangBypassEnabled = new ArrayList<>();
    
    @Override
    public String getId() {
        return "bypass";
    }

    @Override
    public void executeCommand(GangsPlugin gangsPlugin, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players are allowed to use this command!");
            return;
        }
        Player player = (Player) sender;
        if (gangBypassEnabled.contains(player.getUniqueId())) {
            //disabled bypass mode
            gangsPlugin.getMessageManager().sendMessage("bypass-disabled", sender);
            gangBypassEnabled.remove(player.getUniqueId());

            if (Present.isPlayer(player) && player.getLocation().getWorld().equals(gangsPlugin.getGangHouseManager().getHouseWorld())) {
                GMember member = gangsPlugin.getPlayerManager().getMember(player);
                member.initeBorder();
            }
        }else {
            //enabled bypass mode
            gangsPlugin.getMessageManager().sendMessage("bypass-enabled", sender);
            gangBypassEnabled.add(player.getUniqueId());

            if (Present.isPlayer(player)) {
                GMember member = gangsPlugin.getPlayerManager().getMember(player);
                member.removeBorder();
            }
        }        
    }
}
