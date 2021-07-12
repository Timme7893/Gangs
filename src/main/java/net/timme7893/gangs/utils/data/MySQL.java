package net.timme7893.gangs.utils.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.file.File;
import org.bukkit.Bukkit;

import net.timme7893.gangs.GangsPlugin;

public class MySQL {

    private Connection connection;

    private String host;
    private String database;
    private String user;
    private String password;

    public MySQL() {
        File configFile = Archive.get("config");
        host = configFile.asString("mysql.host");
        database = configFile.asString("mysql.database");
        user = configFile.asString("mysql.user");
        password = configFile.asString("mysql.password");
        connect();
    }

    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager
                    .getConnection("jdbc:mysql://" + host + "/" + database + "?user=" + user + "&password=" + password);

            if (connection != null) {
                initTables();
                System.out.println("[Gangs] Data initiated.");
            } else {
                System.out.println("[Gangs] Shutting down. No data connection.");
                Bukkit.getPluginManager().disablePlugin(GangsPlugin.getInstance());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (!connection.isClosed() && connection != null) {
                connection.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void initTables() {
        if (connection != null) {
            try {
                List<String> tables = new ArrayList<String>();
                tables.add(
                        "CREATE TABLE IF NOT EXISTS `gangs` ( `id` INT NOT NULL AUTO_INCREMENT , `gangName` TEXT NOT NULL , `gangLeader` TEXT NOT NULL , `storageLevel` INT NOT NULL , `mineBlockLevel` INT NOT NULL , `guildBlockPercentLevel` INT NOT NULL , `maxPlayersLevel` INT NOT NULL , `sellMultiLevel` INT NOT NULL , `points` INT NOT NULL , `slimeBlocks` INT NOT NULL , `lanternBlocks` INT NOT NULL , `prismarineBlocks` INT NOT NULL , PRIMARY KEY (`id`)); ");
                tables.add(
                        "CREATE TABLE IF NOT EXISTS `gangMembers` ( `id` INT NOT NULL AUTO_INCREMENT , `playerId` TEXT NOT NULL , `gangId` INT NOT NULL , `gangRole` TEXT NOT NULL , `expGained` INT NOT NULL , `blocksBroken` INT NOT NULL , `lastOnlineTime` INT NOT NULL , `joinedStamp` INT NOT NULL , PRIMARY KEY (`id`));");
                tables.add(
                        "CREATE TABLE IF NOT EXISTS `gangHouses` ( `id` INT NOT NULL AUTO_INCREMENT , `gangId` INT NOT NULL , `loc1` TEXT NOT NULL , `loc2` TEXT NOT NULL , `spawnLoc` TEXT NOT NULL , PRIMARY KEY (`id`));");
                tables.add(
                        "CREATE TABLE IF NOT EXISTS `gangFactories` ( `id` INT NOT NULL AUTO_INCREMENT , `gangId` INT NOT NULL , `productionRate` INT NOT NULL , `qualityBlock` TEXT NOT NULL , PRIMARY KEY (`id`));");

                // temporary = 1 -> temporary booster, temporary = 0 -> permanent booster.
                // Currently just used for consistency
                tables.add(
                        "CREATE TABLE IF NOT EXISTS `boosters` ( `id` INT NOT NULL AUTO_INCREMENT , `gangId` INT NOT NULL , `temporary` INT NOT NULL, `duration` INT NOT NULL, `value` DOUBLE NOT NULL , PRIMARY KEY (`id`));");
                for (String tableSQL : tables) {
                    PreparedStatement preparedStatement = connection.prepareStatement(tableSQL);
                    preparedStatement.execute();
                    preparedStatement.close();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

}
