package net.timme7893.gangs.gangs.houses;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import lombok.Getter;
import lombok.Setter;
import net.timme7893.gangs.gangs.Gang;

public class GangMine {

    private Gang gang;

    private Location loc1;
    private Location loc2;

    private List<Block> blocksBetween;
    private int slimeAmount;
    private int lanternAmount;
    private int prismarineAmount;
    private Random randomGenerator;
    @Getter
    @Setter
    private int minutesSinceBreak = -1;

    public GangMine(Gang gang) {
        this.gang = gang;
        this.randomGenerator = new Random();
        Location originPoint = gang.getGangHouse().getSpawnLoc();

        this.loc1 = new Location(originPoint.getWorld(), originPoint.getX() - 11, originPoint.getY() + 2,
                originPoint.getZ() - 32);
        this.loc2 = new Location(originPoint.getWorld(), originPoint.getX() + 10, originPoint.getY() - 17,
                originPoint.getZ() - 54);
        this.blocksBetween = blocksFromTwoPoints(loc1, loc2);
        int totalBlocks = blocksBetween.size();
        this.slimeAmount = getBlocks(Material.SLIME_BLOCK);
        this.lanternAmount = getBlocks(Material.SEA_LANTERN);
        this.prismarineAmount = getBlocks(Material.PRISMARINE);

        resetMine();

    }

    public void resetMine() {

        List<Block> toFill = blocksFromTwoPoints(loc1, loc2);

        for (int i = 0; i < slimeAmount; i++) {
            Block block = anyBlock(toFill);
            toFill.remove(block);
            block.setType(Material.SLIME_BLOCK);
        }

        for (int i = 0; i < lanternAmount; i++) {
            Block block = anyBlock(toFill);
            toFill.remove(block);
            block.setType(Material.SEA_LANTERN);
        }

        for (int i = 0; i < prismarineAmount; i++) {
            Block block = anyBlock(toFill);
            toFill.remove(block);
            block.setType(Material.PRISMARINE);
        }

        toFill.forEach(block -> block.setType(Material.STONE));
    }

    public Block anyBlock(List<Block> toFill) {
        int index = randomGenerator.nextInt(toFill.size());
        Block block = toFill.get(index);
        return block;
    }

    public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2) {
        List<Block> blocks = new ArrayList<Block>();

        int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());

        int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());

        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);

                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    public int getBlocks(Material material) {
        if (material == Material.SLIME_BLOCK) {
            int totalBlocks = 0;
            double blocks = BlockChances.SLIME.get(gang.getGuildBlockPercentLevel());
            while (blocks >= 1) {
                blocks--;
                totalBlocks++;
            }
            if (blocks > 0) {
                totalBlocks = new Random().nextBoolean() ? totalBlocks + 1 : totalBlocks;
            }

            return totalBlocks;
        } else if (material == Material.SEA_LANTERN) {
            int totalBlocks = 0;
            double blocks = BlockChances.LANTERN.get(gang.getGuildBlockPercentLevel());
            while (blocks >= 1) {
                blocks--;
                totalBlocks++;
            }
            if (blocks > 0) {
                totalBlocks = new Random().nextBoolean() ? totalBlocks + 1 : totalBlocks;
            }
            return totalBlocks;
        } else if (material == Material.PRISMARINE) {
            int totalBlocks = 0;
            double blocks = BlockChances.PRISMARINE.get(gang.getGuildBlockPercentLevel());
            while (blocks >= 1) {
                blocks--;
                totalBlocks++;
            }
            if (blocks > 0) {
                totalBlocks = new Random().nextBoolean() ? totalBlocks + 1 : totalBlocks;
            }
            return totalBlocks;
        }
        throw new IllegalArgumentException("Unknown material " + material + " given");
    }

    public enum BlockChances {

        SLIME(5d, 6d, 7d, 8d, 9d, 10), LANTERN(2d, 2.5d, 3d, 3.5d, 4d, 5d), PRISMARINE(0.5, 1d, 1.5d, 2d, 2.5d, 3d);

        private double level1, level2, level3, level4, level5, level6;

        private BlockChances(double level1, double level2, double level3, double level4, double level5, double level6) {
            this.level1 = level1;
            this.level2 = level2;
            this.level3 = level3;
            this.level4 = level4;
            this.level5 = level5;
            this.level6 = level6;
        }

        public double get(int level) {
            if (level == 0) {
                return level1;
            } else if (level == 1) {
                return level2;
            } else if (level == 2) {
                return level3;
            } else if (level == 3) {
                return level4;
            } else if (level == 4) {
                return level5;
            } else if (level == 5) {
                return level5;
            }
            throw new IllegalArgumentException("Max gang level of 6 exceeded!");
        }
    }
}
