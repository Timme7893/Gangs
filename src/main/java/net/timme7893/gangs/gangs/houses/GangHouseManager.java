package net.timme7893.gangs.gangs.houses;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import net.timme7893.gangs.utils.file.Archive;
import org.bukkit.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.DataException;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;

public class GangHouseManager {

    private GangsPlugin gangsPlugin;
    private HashMap<Integer, GangHouse> gangHouses = new HashMap<>();
    private World houseWorld;
    private int lastX = 0;

    public GangHouseManager(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;

        if (Bukkit.getWorld("gangHouse") != null) {
            this.houseWorld = Bukkit.getWorld("gangHouse");
        } else {
            WorldCreator worldCreator = new WorldCreator("gangHouse");
            worldCreator.type(WorldType.FLAT);
            worldCreator.generatorSettings("2;0;1;"); //This is what makes the world empty (void)
            World world = worldCreator.createWorld();
            world.setGameRuleValue("DO_MOB_SPAWNING","false");
            this.houseWorld = world;
        }

        this.lastX = Archive.get("config").asInt("gang-house.last-house-x");
        loadHouses();
    }

    public void loadHouses() {
        try {
            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection()
                    .prepareStatement("SELECT * FROM `gangHouses`;");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int gangId = resultSet.getInt("gangId");

                String loc1Json = resultSet.getString("loc1");
                String loc2Json = resultSet.getString("loc2");
                String spawnLocJson = resultSet.getString("spawnLoc");

                Location loc1 = getLocationFromJSON(loc1Json);
                Location loc2 = getLocationFromJSON(loc2Json);
                Location spawnLoc = getLocationFromJSON(spawnLocJson);

                if (gangsPlugin.getGangsManager().getGangWithId(gangId) != null) {
                    Gang gang = gangsPlugin.getGangsManager().getGangWithId(gangId);
                    GangHouse gangHouse = new GangHouse(id, loc1, loc2, spawnLoc, gang);
                    gangHouses.put(id, gangHouse);
                } else {
                    System.out.println(
                            "[Gangs] Gang (" + gangId + ") is unknown! Failed to load the house for this gang.");
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void createNewHouse(Gang gang) {
        try {
            Location newHouse = null;
            float yaw = 178.5F;
            float pitch = 8.0F;
            if (gangHouses.isEmpty()) {
                newHouse = new Location(houseWorld, 10, 80, 10, yaw, pitch);
            } else {
                newHouse = new Location(houseWorld, lastX + 1000, 80, 10, yaw, pitch);
            }
            pasteHouse(newHouse);
            Archive.get("config").addFileChange("gang-house.last-house-x",
                    net.timme7893.gangs.utils.file.File.ChangeType.SET, lastX + 1000);
            Archive.get("config").saveChanges();
            lastX = lastX + 1000;
            String jsonLocation = getJSONFromLocation(newHouse);

            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection().prepareStatement(
                    "INSERT INTO `gangHouses`(`gangId`, `loc1`, `loc2`, `spawnLoc`) VALUES (?,?,?,?)");
            preparedStatement.setInt(1, gang.getId());
            preparedStatement.setString(2, "");
            preparedStatement.setString(3, "");
            preparedStatement.setString(4, jsonLocation);

            preparedStatement.executeUpdate();
            preparedStatement.close();

            loadHouse(gang);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void loadHouse(Gang gang) {
        try {
            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection()
                    .prepareStatement("SELECT * FROM `gangHouses` WHERE gangId=?;");
            preparedStatement.setInt(1, gang.getId());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                int gangId = resultSet.getInt("gangId");

                String loc1Json = resultSet.getString("loc1");
                String loc2Json = resultSet.getString("loc2");
                String spawnLocJson = resultSet.getString("spawnLoc");

                Location loc1 = getLocationFromJSON(loc1Json);
                Location loc2 = getLocationFromJSON(loc2Json);
                Location spawnLoc = getLocationFromJSON(spawnLocJson);

                GangHouse gangHouse = new GangHouse(id, loc1, loc2, spawnLoc, gang);
                gangHouses.put(id, gangHouse);

            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void deleteHouse(GangHouse gangHouse) {
        try {
            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection()
                    .prepareStatement("DELETE FROM `gangHouses` WHERE id=?;");
            preparedStatement.setInt(1, gangHouse.getId());

            preparedStatement.executeUpdate();
            preparedStatement.close();

            gangHouses.keySet().removeIf(house -> gangHouses.get(house).equals(gangHouse));
            // Are you joking Timme?
            // gangHouses.remove(gangHouse);
            gangHouse.getGang().setGangHouse(null);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Location getLocationFromJSON(String locationJson) {

        if (locationJson.equals("")) {
            return null;
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject locObject = (JSONObject) parser.parse(locationJson);
            double x = (Double) locObject.get("x");
            double y = (Double) locObject.get("y");
            double z = (Double) locObject.get("z");
            float yaw = Float.valueOf(locObject.get("yaw") + "F");
            float pitch = Float.valueOf(locObject.get("pitch") + "F");
            Location loc = new Location(houseWorld, x, y, z, yaw, pitch);
            return loc;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public String getJSONFromLocation(Location location) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("x", location.getX());
        jsonObject.put("y", location.getY());
        jsonObject.put("z", location.getZ());
        jsonObject.put("yaw", location.getYaw());
        jsonObject.put("pitch", location.getPitch());
        return jsonObject.toJSONString();
    }

    @SuppressWarnings("deprecation")
    public void pasteHouse(Location location) {
        // TODO: Should be updated to a proper way
        try {
            EditSession es = new EditSession(new BukkitWorld(houseWorld), -1);
            CuboidClipboard cc = CuboidClipboard
                    .loadSchematic(new File(gangsPlugin.getDataFolder(), "ganghouse.schematic"));
            cc.paste(es, Vector.toBlockPoint(location.getX(), location.getY(), location.getZ()), true);
        } catch (MaxChangedBlocksException | DataException | IOException e) {
            e.printStackTrace();
        }
    }

    public World getHouseWorld() {
        return houseWorld;
    }
}
