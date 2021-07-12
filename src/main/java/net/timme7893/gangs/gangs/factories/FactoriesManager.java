package net.timme7893.gangs.gangs.factories;

import lombok.Getter;
import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.utils.GBlock;
import net.timme7893.gangs.utils.Present;
import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.file.File;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FactoriesManager {

    private GangsPlugin gangsPlugin;
    private int totalFactories;
    @Getter
    private HashMap<Integer, Factory> factories = new HashMap<>();
    private FactoryTimer factoryTimer;

    private double defaultCost;
    private double multiplierSlime;
    private double multiplierLantern;
    private double multiplierPrismarine;
    private double lanternUpgrade;
    private double prismarineUpgrade;

    public FactoriesManager(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
        File file = Archive.get("config");
        this.totalFactories = file.asInt("factories.total");
        this.defaultCost = file.asDouble("factories.default-cost");
        this.multiplierSlime = file.asDouble("factories.slime-multiplier");
        this.multiplierLantern = file.asDouble("factories.lantern-multiplier");
        this.multiplierPrismarine = file.asDouble("factories.prismarine-multiplier");
        this.lanternUpgrade = file.asDouble("factories.upgrade-quality.lantern");
        this.prismarineUpgrade = file.asDouble("factories.upgrade-quality.prismarine");
        loadFactories();
    }

    public void loadFactories() {
        try {
            if (gangsPlugin.getMysql().getConnection() == null) {
                System.out.println("[Gangs] Factories not loaded. Connection doesn't exists.");
                return;
            }

            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection().prepareStatement("SELECT * FROM `gangFactories`;");
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                int gangId = resultSet.getInt("gangId");
                int productionRate = resultSet.getInt("productionRate");
                String qualityBlock = resultSet.getString("qualityBlock");
                if (GBlock.valueOf(qualityBlock) == null) {
                    return;
                }
                GBlock quality = GBlock.valueOf(qualityBlock);

                double multiplier = 0;
                if (quality == GBlock.SLIME) {
                    multiplier = multiplierSlime;
                } else if (quality == GBlock.LANTERN) {
                    multiplier = multiplierLantern;
                } else {
                    multiplier = multiplierPrismarine;
                }

                if (Present.isGang(gangId)) {
                    Gang gang = gangsPlugin.getGangsManager().getGangWithId(gangId);
                    Factory factory = new Factory(id,gang,productionRate,quality,defaultCost,multiplier,lanternUpgrade,prismarineUpgrade);
                    factories.put(id,factory);
                } else {
                    Factory factory = new Factory(id,null,productionRate,quality,defaultCost,multiplier,lanternUpgrade,prismarineUpgrade);
                    factories.put(id,factory);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (factories.size() < totalFactories) {
            int lessFactories = totalFactories - factories.size();
            for (int i = 0; i < lessFactories; i++) {
                createFactory();
            }
            factories.clear();
            loadFactories();
        } else {
            this.factoryTimer = new FactoryTimer(gangsPlugin);
        }
    }

    public void updateFactory(int id, int gangId, int productionRate, GBlock qualityBlock) {
        if (!factories.containsKey(id)) {
            return;
        }

        try {
            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection().prepareStatement("UPDATE `gangFactories` SET `id`=?,`gangId`=?,`productionRate`=?,`qualityBlock`=? WHERE id=?;");
            preparedStatement.setInt(1,id);
            preparedStatement.setInt(2,gangId);
            preparedStatement.setInt(3,productionRate);
            preparedStatement.setString(4,qualityBlock.toString());
            preparedStatement.setInt(5,id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void createFactory() {
        try {
            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection().prepareStatement("INSERT INTO `gangFactories`(`gangId`, `productionRate`, `qualityBlock`) VALUES (?,?,?);");
            preparedStatement.setInt(1,-1);
            preparedStatement.setInt(2,1);
            preparedStatement.setString(3,GBlock.SLIME.toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void deleteFactory(Factory factory) {
        try {
            PreparedStatement preparedStatement = gangsPlugin.getMysql().getConnection().prepareStatement("DELETE FROM `gangFactories` WHERE id=?");
            preparedStatement.setInt(1,factory.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            factories.remove(factory.getId());
            createFactory();
        } catch (Exception exception) {
            exception.printStackTrace();
		}
	}

	public List<Factory> getFactoriesByGang(Gang gang) {
        return factories.values().stream().filter(factory -> gang.equals(factory.getOwner())).collect(Collectors.toList());
    }
}
