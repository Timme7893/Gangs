package net.timme7893.gangs.gangs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import me.clip.autosell.multipliers.Multipliers;
import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.bonds.Ally;
import net.timme7893.gangs.gangs.bonds.Enemy;
import net.timme7893.gangs.gangs.houses.GangHouse;
import net.timme7893.gangs.gangs.members.GMember;
import net.timme7893.gangs.gangs.storages.StorageMenu;
import net.timme7893.gangs.gangs.utils.Booster;
import net.timme7893.gangs.gangs.utils.GangUpgrade;
import net.timme7893.gangs.utils.file.Archive;
import net.timme7893.gangs.utils.file.File;
import net.timme7893.gangs.utils.text.Text;
import net.timme7893.gangs.utils.YamlFile;

public class Gang {

    private int id;
    private String gangName;
    private UUID gangLeader;
    private List<GMember> gangMemberList;
    private GangHouse gangHouse;
    private StorageMenu storageMenu;

    @Getter
    private List<Enemy> enemies;
    @Getter
    private List<Gang> allies;

    @Setter
    private List<Booster> permanentBoosters;
    private List<Booster> queueBoosters = new ArrayList<>();
    private Booster activeTemporaryBooster;

    private String gangColor;
    private double exp;
    private int storageLevel;
    private int mineBlockLevel;
    private int guildBlockPercentLevel;
    private int maxPlayersLevel;
    private int sellMultiLevel;
    private double expMultiplier;
    private int rank;

    @Getter
    private int maxSellLevel;
    @Getter
    private int maxMineLevel;

    private int points;
    private int slimeBlocks;
    private int lanternBlocks;
    private int prismarineBlocks;

    @Getter
    private YamlFile gangStorageFile;

    private File configFile = Archive.get("config");

    public Gang(int id, String gangName, UUID gangLeader, List<GMember> gangMemberList,
            int storageLevel, int mineBlockLevel, int guildBlockPercentLevel, int maxPlayersLevel, int sellMultiLevel,
            double exp, int slimeBlocks, int lanternBlocks, int prismarineBlocks, @Nullable List<Booster> permanentBoosters,
            List<Enemy> enemies, List<Gang> allies, String gangColor) {
        this.gangStorageFile = new YamlFile(gangName);

        this.id = id;
        this.gangName = gangName;
        this.gangLeader = gangLeader;
        this.gangMemberList = gangMemberList;
        this.storageLevel = storageLevel;
        this.mineBlockLevel = mineBlockLevel;
        this.guildBlockPercentLevel = guildBlockPercentLevel;
        this.maxPlayersLevel = maxPlayersLevel;
        this.sellMultiLevel = sellMultiLevel;
        this.exp = exp;
        this.slimeBlocks = slimeBlocks;
        this.lanternBlocks = lanternBlocks;
        this.prismarineBlocks = prismarineBlocks;
        this.expMultiplier = 1;
        this.storageMenu = new StorageMenu(this);
        this.gangColor = gangColor;

        this.permanentBoosters = permanentBoosters;
        if (permanentBoosters != null) {
            for (Booster booster : permanentBoosters) {
                activateBooster(booster);
            }
        }
        this.enemies = enemies;
        this.allies = allies;

        if (maxPlayersLevel < 2) {
            this.maxPlayersLevel = 2;
            markDirty();
            // At all moments there should be 3 unlocked slots in MemberGUI aka playerLevel 2
        }

        File config = Archive.get("config");
        this.maxMineLevel = config.asInt("gang-upgrades.max-levels.mine");
        this.maxSellLevel = config.asInt("gang-upgrades.max-levels.sell");
    }

    public int getTotalPoints() {
        return getSlimeBlocks() + (getLanternBlocks() * 10) + (getPrismarineBlocks() * 25);
    }

    public void broadcast(String message) {
        gangMemberList.forEach(member -> {
            if (member.isOnline()) {
                member.getOnlinePlayer().sendMessage(message);
            }
        });
    }

    public void addMember(GMember gMember) {
        if (!gangMemberList.contains(gMember)) {
            gangMemberList.add(gMember);
        }
    }

    public void removeMember(GMember gMember) {
        if (gangMemberList.contains(gMember)) {
            gangMemberList.remove(gMember);
        }
    }

    public void addBooster(Booster booster, boolean pushToDb) {
        if (!booster.isTemporary() && pushToDb) {
            GangsPlugin.getInstance().getGangsManager().addPermanentBooster(booster);
        }
        if (booster.isTemporary() && !queueBoosters.isEmpty()) {
            queueBoosters.add(booster);
        } else {
            activateBooster(booster);

            if (booster.isTemporary())
                activeTemporaryBooster = booster;
        }
    }

    public void removeBooster(Booster booster) {
        if (activeTemporaryBooster == booster) {
            expMultiplier = expMultiplier - booster.getValue();
            activeTemporaryBooster = null;
            queueBoosters();
        }
    }

    public int clearBoosters() {
        int boosters = activeTemporaryBooster != null ? queueBoosters.size() + 1 : queueBoosters.size();
        activeTemporaryBooster = null;
        queueBoosters.clear();
        boosters = boosters + GangsPlugin.getInstance().getGangsManager().removePermanentBoosters(this);
        return boosters;
    }

    public void activateBooster(Booster booster) {
        this.expMultiplier = expMultiplier + booster.getValue();
    }

    public void queueBoosters() {
        if (queueBoosters.isEmpty()) {
            return;
        }
        Booster nextBooster = queueBoosters.get(0);
        queueBoosters.remove(nextBooster);
        activeTemporaryBooster = nextBooster;
        activateBooster(nextBooster);
    }

    public void addPoints(int amount) {
        this.points = points + amount;
    }

    public double getUpgradeCost(GangUpgrade gangUpgrade) {
        if (gangUpgrade == GangUpgrade.GANG_MEMBERS) {
            if (maxPlayersLevel == 9) {
                return 0;
            }
            return configFile.asDouble("gang-upgrades.default-members") * (maxPlayersLevel + 1);
        } else if (gangUpgrade == GangUpgrade.GBLOCK) {
            return configFile.asDouble("gang-upgrades.default-gblock") * (guildBlockPercentLevel + 2);
        } else if (gangUpgrade == GangUpgrade.SELL_MULTIPLIER) {

            if (sellMultiLevel == maxSellLevel) {
                return 0;
            }

            return (configFile.asDouble("gang-upgrades.default-sell") * (sellMultiLevel + 1));
        } else if (gangUpgrade == GangUpgrade.MINE) {

            if (mineBlockLevel == maxMineLevel) {
                return 0;
            }

            return configFile.asDouble("gang-upgrades.default-mine") * (mineBlockLevel + 2);
        } else if (gangUpgrade == GangUpgrade.STORAGE) {
            return configFile.asDouble("gang-upgrades.default-storage") * (storageLevel + 3);
        } else {
            return -1;
        }
    }

    public void gangUpgrade(GangUpgrade gangUpgrade) {
        markDirty();
        if (gangUpgrade == GangUpgrade.GANG_MEMBERS) {
            if (maxPlayersLevel < 10) {
                maxPlayersLevel++;
            }
        } else if (gangUpgrade == GangUpgrade.GBLOCK) {
            guildBlockPercentLevel++;
        } else if (gangUpgrade == GangUpgrade.SELL_MULTIPLIER) {

            if (sellMultiLevel == maxSellLevel) {
                return;
            }

            sellMultiLevel++;
            updateSellMultiplier();
        } else if (gangUpgrade == GangUpgrade.MINE) {

            if (mineBlockLevel == maxMineLevel) {
                return;
            }
            mineBlockLevel++;

        } else {
            storageLevel++;
        }
    }

    public void updateSellMultiplier() {
        /*for (GMember member : getGangMemberList()) {
            Multipliers.addMultiplier(new Multiplier(member.getPlayerId().toString(), "", 43200, 0.1, false));
        }*/
    }

    public void removeMemberMultipliers(GMember member) {
        Multipliers.removeMultiplier(member.getOnlinePlayer());
    }

    // if sellMultiplier level = 5 -> means 0,5 added to 1 = total of 1.5
    public Double getSellMultiplier() {
        return 1 + (sellMultiLevel / 10.0);
    }

    public boolean isAlly(Gang gang) {
        return allies.contains(gang);
    }

    public boolean isEnemy(Gang gang) {
        Enemy result = enemies.stream().filter(enemy -> enemy.getEnemy() == gang).findFirst().orElse(null);
        return result != null;
    }

    public void addAlly(Ally ally) {
        if (!isAlly(ally.getAlly())) {
            allies.add(ally.getAlly());
        }
    }

    public void removeAlly(Ally ally) {
        if (isAlly(ally.getAlly())) {
            allies.remove(ally.getAlly());
        }
    }

    public void addEnemy(Enemy enemy) {
        if (!isEnemy(enemy.getEnemy())) {
            enemies.add(enemy);
        }
    }

    public void removeEnemy(Enemy enemy) {
        if (isEnemy(enemy.getEnemy())) {
            enemies.remove(enemy);
        }
    }

    public void delete() {
        // gangHouse.delete();
        getGangStorageFile().delete();
    }

    public int getStorageSlots() {
        return storageLevel + 1;
    }

    // default methods:

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGangName() {
        return gangName;
    }

    public String getGangPrefix() {
        return Text.format(gangColor + gangName);
    }

    public void setGangName(String gangName) {
        this.gangName = gangName;
        markDirty();
    }

    public UUID getGangLeader() {
        return gangLeader;
    }

    public void setGangLeader(UUID gangLeader) {
        this.gangLeader = gangLeader;
        markDirty();
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public List<GMember> getGangMemberList() {
        return gangMemberList;
    }

    public void setGangMemberList(List<GMember> gangMemberList) {
        this.gangMemberList = gangMemberList;
    }

    public int getStorageLevel() {
        return storageLevel;
    }

    public void setStorageLevel(int storageLevel) {
        this.storageLevel = storageLevel;
        markDirty();
    }

    public int getMineBlockLevel() {
        return mineBlockLevel;
    }

    public void setMineBlockLevel(int mineBlockLevel) {
        this.mineBlockLevel = mineBlockLevel;
        markDirty();
    }

    public int getGuildBlockPercentLevel() {
        return guildBlockPercentLevel;
    }

    public void setGuildBlockPercentLevel(int guildBlockPercentLevel) {
        this.guildBlockPercentLevel = guildBlockPercentLevel;
        markDirty();
    }

    public int getMaxPlayersLevel() {
        return maxPlayersLevel;
    }

    public void setMaxPlayersLevel(int maxPlayersLevel) {
        this.maxPlayersLevel = maxPlayersLevel;
        markDirty();
    }

    public int getSellMultiLevel() {
        return sellMultiLevel;
    }

    public void setSellMultiLevel(int sellMultiLevel) {
        this.sellMultiLevel = sellMultiLevel;
        markDirty();
    }

    public double getExp() {
        return exp;
    }

    public void addExp(double amount) {
        this.exp = exp + amount;
    }

    public void setExp(double exp) {
        this.exp = exp;
        markDirty();
    }

    public int getSlimeBlocks() {
        return slimeBlocks;
    }

    public void setSlimeBlocks(int slimeBlocks) {
        this.slimeBlocks = slimeBlocks;
        markDirty();
    }

    public int getLanternBlocks() {
        return lanternBlocks;
    }

    public void setLanternBlocks(int lanternBlocks) {
        this.lanternBlocks = lanternBlocks;
        markDirty();
    }

    public int getPrismarineBlocks() {
        return prismarineBlocks;
    }

    public void setPrismarineBlocks(int prismarineBlocks) {
        this.prismarineBlocks = prismarineBlocks;
        markDirty();
    }

    public GangHouse getGangHouse() {
        return gangHouse;
    }

    public void setGangHouse(GangHouse gangHouse) {
        this.gangHouse = gangHouse;
    }

    public StorageMenu getStorageMenu() {
        return storageMenu;
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
