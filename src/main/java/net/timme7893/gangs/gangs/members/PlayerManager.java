package net.timme7893.gangs.gangs.members;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.Getter;
import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.GangRole;

public class PlayerManager {

    private GangsPlugin gangsPlugin;
    @Getter
    private HashMap<UUID, GMember> gangMembers = new HashMap<>();

    public PlayerManager(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
        loadMembers();
    }

    public void loadMembers() {
        try {
            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection()
                    .prepareStatement("SELECT * FROM `gangMembers`;");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                UUID playerId = UUID.fromString(resultSet.getString("playerId"));
                Gang gang = gangsPlugin.getGangsManager().getGangWithId(resultSet.getInt("gangId"));
                GangRole gangRole = GangRole.valueOf(resultSet.getString("gangRole"));
                int expGained = resultSet.getInt("expGained");
                int blocksBroken = resultSet.getInt("blocksBroken");
                long lastOnlineTime = resultSet.getLong("lastOnlineTime");
                int joinedStamp = resultSet.getInt("joinedStamp");

                GMember gMember = new GMember(id, playerId, gang, gangRole, expGained, blocksBroken, lastOnlineTime,
                        joinedStamp);
                gangMembers.put(playerId, gMember);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public GMember createMember(Player player, Gang gang) {
        try {
            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection().prepareStatement(
                    "INSERT INTO `gangMembers`(`playerId`, `gangId`, `gangRole`, `expGained`, `blocksBroken`, `lastOnlineTime`, `joinedStamp`) VALUES (?,?,?,0,0,0,0);");
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setInt(2, gang.getId());
            preparedStatement.setString(3, GangRole.MEMBER.toString());

            preparedStatement.executeUpdate();
            preparedStatement.close();

            return loadMember(player.getUniqueId());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public void deleteMember(GMember gMember) {
        try {
            gMember.setGang(null);

            gangMembers.keySet().removeIf(uuid -> gangMembers.get(uuid).equals(gMember));

            // Why, Timme?
            // gangMembers.remove(gMember);
            System.out.println(gMember.getId() + " is removed");

            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection()
                    .prepareStatement("DELETE FROM `gangMembers` WHERE id=?;");
            preparedStatement.setInt(1, gMember.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public GMember loadMember(UUID playerId) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = gangsPlugin.getMysql().getConnection()
                    .prepareStatement("SELECT * FROM `gangMembers` WHERE playerId=?;");
            preparedStatement.setString(1, playerId.toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                Gang gang = gangsPlugin.getGangsManager().getGangWithId(resultSet.getInt("gangId"));
                GangRole gangRole = GangRole.valueOf(resultSet.getString("gangRole"));
                int expGained = resultSet.getInt("expGained");
                int blocksBroken = resultSet.getInt("blocksBroken");
                long lastOnlineTime = resultSet.getLong("lastOnlineTime");
                int joinedStamp = resultSet.getInt("joinedStamp");

                GMember gMember = new GMember(id, playerId, gang, gangRole, expGained, blocksBroken, lastOnlineTime,
                        joinedStamp);
                gangMembers.put(playerId, gMember);
                return gMember;
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public void updateMember(GMember member) {
        try {

            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection().prepareStatement(
                    "UPDATE `gangMembers` SET `id`=?,`playerId`=?,`gangId`=?,`gangRole`=?,`expGained`=?,`blocksBroken`=?,`lastOnlineTime`=?,`joinedStamp`=? WHERE id=?");
            preparedStatement.setInt(1, member.getId());
            preparedStatement.setString(2, member.getPlayerId().toString());
            preparedStatement.setInt(3, member.getGang().getId());
            preparedStatement.setString(4, member.getGangRole().toString());
            preparedStatement.setInt(5, member.getExpGained());
            preparedStatement.setInt(6, member.getBlocksBroken());
            preparedStatement.setInt(7, 0);
            preparedStatement.setInt(8, 0);
            preparedStatement.setInt(9, member.getId());

            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public GMember getMember(Player player) {
        return gangMembers.values().stream().filter(member -> member.getPlayerId().equals(player.getUniqueId()))
                .findFirst().orElse(null);
    }

    public GMember getMember(String playerName) {
        return gangMembers.values().stream().filter(member -> member.getOnlinePlayer().getName().equals(playerName))
                .findFirst().orElse(null);
    }


}
