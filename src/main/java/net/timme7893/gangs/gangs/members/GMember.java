package net.timme7893.gangs.gangs.members;

import java.util.UUID;

import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.file.File;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.GangRole;

public class GMember {

	private int id;
	private UUID playerId;
	private Gang gang;
	private GangRole gangRole;

	private int expGained;
	private int blocksBroken;
	// private long lastOnlineTime;
	// private int joinedStamp;

	private boolean borderInited = false;

	public GMember(int id, UUID playerId, Gang gang, GangRole gangRole, int expGained, int blocksBroken,
			long lastOnlineTime, int joinedStamp) {
		this.id = id;
		this.playerId = playerId;
		this.gang = gang;
		this.gangRole = gangRole;
		this.expGained = expGained;
		this.blocksBroken = blocksBroken;
		// this.lastOnlineTime = lastOnlineTime;
		// this.joinedStamp = joinedStamp;

		GMember member = this;
		if (gang != null) {
			Bukkit.getScheduler().runTaskLater(GangsPlugin.getInstance(), () -> {
				gang.addMember(member);
			}, 30l);
		}
	}

	public void initeBorder() {
		if (!borderInited) {
			borderInited = true;
			GangsPlugin.getInstance().getWorldBorderApi().setBorder(getOnlinePlayer(),150,gang.getGangHouse().getSpawnLoc());
		}
	}

	public void forceBorder() {
		borderInited = true;
		GangsPlugin.getInstance().getWorldBorderApi().resetWorldBorderToGlobal(getOnlinePlayer());
		GangsPlugin.getInstance().getWorldBorderApi().setBorder(getOnlinePlayer(),150,gang.getGangHouse().getSpawnLoc());
	}

	public void removeBorder() {
		if (borderInited) {
			GangsPlugin.getInstance().getWorldBorderApi().resetWorldBorderToGlobal(getOnlinePlayer());
			borderInited = false;
		}
	}

	public boolean isOnline() {
		if (getOnlinePlayer() != null) {
			return getOnlinePlayer().isOnline();
		} else {
			return false;
		}
	}

	public String getOnline() {
		if (isOnline()) {
			return "&ayes";
		} else {
			return "&4no";
		}
	}

	public void promote() {
		this.gangRole = getNextRole(gangRole);
		markDirty();
	}

	public GangRole getNextRole(GangRole currentRole) {
		if (currentRole == GangRole.MEMBER) {
			return GangRole.OFFICER;
		} else if (currentRole == GangRole.OFFICER) {
			return GangRole.ADMIN;
		} else if (currentRole == GangRole.ADMIN) {
			return GangRole.LEADER;
		}
		return null;
	}

	public void demote() {
		this.gangRole = getLowerRole(gangRole);
		markDirty();
	}

	public GangRole getLowerRole(GangRole currentRole) {
		if (currentRole == GangRole.MEMBER) {
			return GangRole.MEMBER;
		} else if (currentRole == GangRole.OFFICER) {
			return GangRole.MEMBER;
		} else if (currentRole == GangRole.ADMIN) {
			return GangRole.OFFICER;
		} else if (currentRole == GangRole.LEADER) {
			return GangRole.ADMIN;
		}
		return null;
	}

	public String getRoleCharacter() {
		File configFile = Archive.get("config");
		return configFile.asString("gang-chat.ranks." + gangRole.toString().toLowerCase());
	}

	public Player getOnlinePlayer() {
		return Bukkit.getPlayer(playerId);
	}

	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(playerId);
	}

	public void addExp(int amount) {
		expGained = expGained + amount;
		markDirty();
	}

	public void addBlocksBroken(int amount) {
		blocksBroken = blocksBroken + amount;
		markDirty();
	}

	public int getId() {
		return id;
	}

	public Gang getGang() {
		return gang;
	}

	public void setGang(Gang gang) {
		this.gang = null;
	}

	public UUID getPlayerId() {
		return playerId;
	}

	public GangRole getGangRole() {
		return gangRole;
	}

	public void setGangRole(GangRole gangRole) {
		this.gangRole = gangRole;
		markDirty();
	}

	public int getExpGained() {
		return expGained;
	}

	public int getBlocksBroken() {
		return blocksBroken;
	}

	private boolean dirty = false;

	public void markDirty() {
		this.dirty = true;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

}
