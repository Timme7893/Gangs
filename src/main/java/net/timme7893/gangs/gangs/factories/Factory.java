package net.timme7893.gangs.gangs.factories;

import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.utils.GBlock;

public class Factory {

    private int id;
    private String name;
    private Gang owner;
    private int productionRate;
    private GBlock quality;
    private double defaultCost;
    private double rent;
    public boolean paidRent;
    private int unclaimedBlocks;
    private double lanternUpgrade;
    private double prismarineUpgrade;

    public boolean dirty = false;

    public Factory(int id, Gang owner, int productionRate, GBlock quality, double defaultCost, double multiplier, double lanternUpgrade, double prismarineUpgrade) {
        this.id = id;
        this.name = "Factory-" + id;
        this.owner = owner;
        this.productionRate = productionRate;
        this.quality = quality;
        this.defaultCost = defaultCost;
        this.rent = defaultCost * multiplier * productionRate;
        this.lanternUpgrade = lanternUpgrade;
        this.prismarineUpgrade = prismarineUpgrade;
    }

    public void upgradeRate() {
        productionRate = productionRate + 1;
        setDirty(true);
    }

    public void upgradeQuality() {
        if (quality == GBlock.SLIME) {
            quality = GBlock.LANTERN;
        } else if (quality == GBlock.LANTERN) {
            quality = GBlock.PRISMARINE;
        }
        setDirty(true);
    }

    public double getRateUpgradeCost() {
        return 2000 * productionRate;
    }

    public double getQualityUpgradeCost() {
        if (quality == GBlock.SLIME) {
            return lanternUpgrade;
        } else if (quality == GBlock.LANTERN) {
            return prismarineUpgrade;
        } else {
            return 0;
        }
    }

    public void claimBlocks() {
        if (quality == GBlock.SLIME) {
            owner.setSlimeBlocks(owner.getSlimeBlocks() + unclaimedBlocks);
            unclaimedBlocks = 0;
        } else if (quality == GBlock.LANTERN) {
            owner.setLanternBlocks(owner.getLanternBlocks() + unclaimedBlocks);
            unclaimedBlocks = 0;
        } else {
            owner.setPrismarineBlocks(owner.getPrismarineBlocks() + unclaimedBlocks);
            unclaimedBlocks = 0;
        }
    }

    public double getRent() {
        return rent;
    }

    public boolean hasPaid() {
        return paidRent;
    }

    public String getRentStatus() {
        if (hasPaid()) {
            return "&a&lPAID";
        } else {
            return "&4&lUNPAID";
        }
    }

    public void setPaidRent(boolean paidRent) {
        this.paidRent = paidRent;
    }

    public void setClaimedBlocks(int blocks) {
        this.unclaimedBlocks = blocks;
    }

    public GBlock getBlockQuality() {
        return quality;
    }

    public void produce() {
        this.unclaimedBlocks = unclaimedBlocks + productionRate;
    }

    public boolean isBought() {
        return owner != null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gang getOwner() {
        return owner;
    }

    public void setOwner(Gang owner) {
        this.owner = owner;
        markDirty();
    }

    public int getProductionRate() {
        return productionRate;
    }

    public String getQuality() {
        if (quality == GBlock.SLIME) {
            return "Slime blocks";
        } else if (quality == GBlock.LANTERN) {
            return "Sea-lantern blocks";
        } else {
            return "Prismarine blocks";
        }
    }

    public int getUnclaimedBlocks() {
        return unclaimedBlocks;
    }

    public String getItem() {
        if (quality == GBlock.SLIME) {
            return "WOODEN_PICKAXE";
        } else if (quality == GBlock.LANTERN) {
            return "IRON_PICKAXEs";
        } else {
            return "DIAMOND_PICKAXE";
        }
    }

    public String getBlockItem() {
        if (quality == GBlock.SLIME) {
            return "SLIMEBLOCK";
        } else if (quality == GBlock.LANTERN) {
            return "SEA_LANTERN";
        } else {
            return "PRISMARINE";
        }
    }

    public void setQuality(GBlock quality) {
        this.quality = quality;
        markDirty();
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }
}
