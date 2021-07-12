package net.timme7893.gangs.gangs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.gangs.bonds.Enemy;
import net.timme7893.gangs.gangs.members.GMember;
import net.timme7893.gangs.gangs.utils.Booster;
import org.bukkit.Bukkit;

import lombok.Getter;
import net.timme7893.gangs.GangsPlugin;

public class GangsManager {

    private GangsPlugin gangsPlugin;
    @Getter
    private HashMap<Integer, Gang> gangs = new HashMap<>();
    @Getter
    private String gangColor;

    public GangsManager(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
        this.gangColor = Archive.get("config").asString("gang-color");
        loadGangs();
    }

    public void loadGangs() {
        try {
            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection()
                    .prepareStatement("SELECT * FROM `gangs`;");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String gangName = resultSet.getString("gangName");
                UUID gangLeader = UUID.fromString(resultSet.getString("gangLeader"));
                int storageLevel = resultSet.getInt("storageLevel");
                int mineBlockLevel = resultSet.getInt("mineBlockLevel");
                int guildBlockPercentLevel = resultSet.getInt("guildBlockPercentLevel");
                int maxPlayersLevel = resultSet.getInt("maxPlayersLevel");
                int sellMultiLevel = resultSet.getInt("sellMultiLevel");
                double exp = resultSet.getDouble("points");
                int slimeBlocks = resultSet.getInt("slimeBlocks");
                int lanternBlocks = resultSet.getInt("lanternBlocks");
                int prismarineBlocks = resultSet.getInt("prismarineBlocks");

                // List<Booster> pernamentBoosters = new ArrayList<>();
                List<Gang> allies = new ArrayList<>();
                List<Enemy> enemies = new ArrayList<>();

                // gangMembers is inited after the playerManager loads.
                List<GMember> gangMembers = new ArrayList<>();

                Gang gang = new Gang(id, gangName, gangLeader, gangMembers, storageLevel, mineBlockLevel,
                        guildBlockPercentLevel, maxPlayersLevel, sellMultiLevel, exp, slimeBlocks, lanternBlocks,
                        prismarineBlocks, null, enemies, allies,gangColor);
                gangs.put(id, gang);
                
                Bukkit.getScheduler().runTaskLater(gangsPlugin, () -> {
                    gang.setPermanentBoosters(getPermanentBoosters(gang));
                }, 20l);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Gang loadGang(UUID leader) {
        try {
            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection()
                    .prepareStatement("SELECT * FROM `gangs` WHERE gangLeader=?;");
            preparedStatement.setString(1, leader.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String gangName = resultSet.getString("gangName");
                UUID gangLeader = UUID.fromString(resultSet.getString("gangLeader"));
                int storageLevel = resultSet.getInt("storageLevel");
                int mineBlockLevel = resultSet.getInt("mineBlockLevel");
                int guildBlockPercentLevel = resultSet.getInt("guildBlockPercentLevel");
                int maxPlayersLevel = resultSet.getInt("maxPlayersLevel");
                int sellMultiLevel = resultSet.getInt("sellMultiLevel");
                double exp = resultSet.getDouble("points");
                int slimeBlocks = resultSet.getInt("slimeBlocks");
                int lanternBlocks = resultSet.getInt("lanternBlocks");
                int prismarineBlocks = resultSet.getInt("prismarineBlocks");

                List<Booster> pernamentBoosters = new ArrayList<>();
                List<Gang> allies = new ArrayList<>();
                List<Enemy> enemies = new ArrayList<>();

                // gangMembers is inited after the playerManager loads.
                List<GMember> gangMembers = new ArrayList<>();

                Gang gang = new Gang(id, gangName, gangLeader, gangMembers, storageLevel, mineBlockLevel,
                        guildBlockPercentLevel, maxPlayersLevel, sellMultiLevel, exp, slimeBlocks, lanternBlocks,
                        prismarineBlocks, pernamentBoosters, enemies, allies,gangColor);
                gangs.put(id, gang);

                gangsPlugin.getGangHouseManager().createNewHouse(gang);
                return gang;
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public Gang createGang(UUID leader, String gangName) {
        try {
            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection().prepareStatement(
                    "INSERT INTO `gangs`(`gangName`, `gangLeader`, `storageLevel`, `mineBlockLevel`, `guildBlockPercentLevel`, `maxPlayersLevel`, `sellMultiLevel`, `points`, `slimeBlocks`, `lanternBlocks`, `prismarineBlocks`) VALUES (?,?,?,?,?,?,?,?,?,?,?);");
            preparedStatement.setString(1, gangName);
            preparedStatement.setString(2, leader.toString());
            preparedStatement.setInt(3, 0);
            preparedStatement.setInt(4, 0);
            preparedStatement.setInt(5, 0);
            preparedStatement.setInt(6, 0);
            preparedStatement.setInt(7, 0);
            preparedStatement.setInt(8, 0);
            preparedStatement.setInt(9, 0);
            preparedStatement.setInt(10, 0);
            preparedStatement.setInt(11, 0);

            preparedStatement.executeUpdate();
            preparedStatement.close();

            return loadGang(leader);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public void deleteGang(int id) {
        try {
            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection()
                    .prepareStatement("DELETE FROM `gangs` WHERE id=?;");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            Gang removeGang = getGangWithId(id);
            removeGang.delete();
            gangs.remove(id);

            for (GMember member : removeGang.getGangMemberList()) {
                gangsPlugin.getPlayerManager().deleteMember(member);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void updateGang(Gang gang) {
        try {

            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection().prepareStatement(
                    "UPDATE `gangs` SET `id`=?,`gangName`=?,`gangLeader`=?,`storageLevel`=?,`mineBlockLevel`=?,`guildBlockPercentLevel`=?,`maxPlayersLevel`=?,`sellMultiLevel`=?,`points`=?,`slimeBlocks`=?,`lanternBlocks`=?,`prismarineBlocks`=? WHERE id=?");
            preparedStatement.setInt(1, gang.getId());
            preparedStatement.setString(2, gang.getGangName());
            preparedStatement.setString(3, gang.getGangLeader().toString());
            preparedStatement.setInt(4, gang.getStorageLevel());
            preparedStatement.setInt(5, gang.getMineBlockLevel());
            preparedStatement.setInt(6, gang.getGuildBlockPercentLevel());
            preparedStatement.setInt(7, gang.getMaxPlayersLevel());
            preparedStatement.setInt(8, gang.getSellMultiLevel());
            preparedStatement.setDouble(9, gang.getExp());
            preparedStatement.setInt(10, gang.getSlimeBlocks());
            preparedStatement.setInt(11, gang.getLanternBlocks());
            preparedStatement.setInt(12, gang.getPrismarineBlocks());
            preparedStatement.setInt(13, gang.getId());
            
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public List<Gang> getAllGangs() {
        return gangs.values().stream().collect(Collectors.toList());
    }

    public Gang getGangWithId(int id) {
        return gangs.values().stream().filter(gang -> gang.getId() == id).findFirst().orElse(null);
    }

    public Gang getGangWithName(String id) {
        return gangs.values().stream().filter(gang -> gang.getGangName().equals(id)).findFirst().orElse(null);
    }

    public Gang getGangWithLeader(UUID leader) {
        return gangs.values().stream().filter(gang -> gang.getGangLeader().equals(leader)).findFirst().orElse(null);
    }

    public List<Booster> addPermanentBooster(Booster booster) {
        List<Booster> boosters = new ArrayList<>();
        try {
            // `gangId` INT NOT NULL , `temporary` INT NOT NULL, `duration` INT NOT NULL, `value` DOUBLE NOT NULL 
            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection()
                    .prepareStatement("INSERT INTO `boosters` (gangId, temporary, duration, value) VALUES (?, ?, ?, ?)");
            preparedStatement.setInt(1, booster.getGang().getId());
            preparedStatement.setBoolean(2, booster.isTemporary());
            preparedStatement.setInt(3, booster.getDuration());
            preparedStatement.setDouble(4, booster.getValue());
            
            preparedStatement.execute();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return boosters;
    }
    
    public int  removePermanentBoosters(Gang gang) {
        int affected = 0;
        try {
            // `gangId` INT NOT NULL , `temporary` INT NOT NULL, `duration` INT NOT NULL, `value` DOUBLE NOT NULL 
            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection()
                    .prepareStatement("DELETE FROM `boosters` WHERE gangId=?");
            preparedStatement.setInt(1, gang.getId());
            
            affected = preparedStatement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return affected;
    }
    
    public List<Booster> getPermanentBoosters(Gang gang) {
        List<Booster> boosters = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection()
                    .prepareStatement("SELECT * FROM `boosters` WHERE gangId=? AND temporary=0");
            preparedStatement.setInt(1, gang.getId());
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Booster booster = new Booster(gang, rs.getBoolean("temporary"), rs.getInt("duration"), rs.getDouble("value"), false);
                boosters.add(booster);
            }

            rs.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return boosters;
    }

}
