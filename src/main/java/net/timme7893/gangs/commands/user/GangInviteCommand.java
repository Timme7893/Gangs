package net.timme7893.gangs.commands.user;

import net.timme7893.gangs.menus.InviteMenu;
import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.commands.GangCommand;
import net.timme7893.gangs.gangs.members.GMember;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GangInviteCommand implements GangCommand {

	private List<World> blackListWorld = new ArrayList<>();

	public GangInviteCommand() {
		List<String> blackList = Archive.get("config").asList("gang-invite-disabled-worlds");
		for (String worldName : blackList) {
			if (Bukkit.getWorld(worldName) != null) {
				blackListWorld.add(Bukkit.getWorld(worldName));
			}
		}
	}

	@Override
	public String getId() {
		return "invite";
	}

	@Override
	public void executeCommand(GangsPlugin gangsPlugin, Player player, String[] args) {

		if (gangsPlugin.getPlayerManager().getMember(player) == null) {
			gangsPlugin.getMessageManager().sendMessage("player-not-in-gang", player);
			return;
		}
		GMember member = gangsPlugin.getPlayerManager().getMember(player);

		// format: invite <name>
		if (args.length != 2) {
			gangsPlugin.getMessageManager().sendMessage("invite-form", player);
			return;
		}
		
		if (member.getGang().getGangMemberList().size() - 1 >= member.getGang().getMaxPlayersLevel()) {
            gangsPlugin.getMessageManager().sendMessage("gang-is-already-full",player);
            return;
        }
		
		String targetName = args[1];
		if (Bukkit.getPlayer(targetName) == null) {
			gangsPlugin.getMessageManager().sendMessage("player-not-exists", player, targetName);
            return;
        }
		Player target = Bukkit.getPlayer(targetName);

		if (gangsPlugin.getPlayerManager().getMember(target) != null) {
			gangsPlugin.getMessageManager().sendMessage("player-already-in-gang", player, target.getName());
			return;
		}

		System.out.println("test-0");


		if (blackListWorld.contains(target.getLocation().getWorld())) {
			gangsPlugin.getMessageManager().sendMessage("invite-disabled-world",player);
			return;
		}

		System.out.println("test-1");


		new InviteMenu(target,member.getGang());

		System.out.println("test-2");


		gangsPlugin.getMessageManager().sendMessage("player-invited", target, member.getGang().getGangName());
		gangsPlugin.getMessageManager().sendMessage("member-invited", player, target.getName());
	}
}
