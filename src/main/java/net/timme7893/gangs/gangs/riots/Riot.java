package net.timme7893.gangs.gangs.riots;

import lombok.Getter;
import net.timme7893.gangs.gangs.Gang;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;

public class Riot {

    private int minutesTicks = 0;
    private int ticks = 0;

    private HashMap<Block, GangLab> gangLabs = new HashMap<>();
    @Getter
    private HashMap<Gang, Double> gangGainedExp = new HashMap<>();

    public Riot(int minutesTicks) {
        this.minutesTicks = minutesTicks;
    }

    public boolean tick() {
        ticks++;
        if (ticks >= minutesTicks) {
            return true;
        }
        return false;
    }

    public void addGangLab(Block block, Gang gang) {
        gangLabs.put(block, new GangLab(gang,block));
    }

    public void removeGangLab(Block block) {
        if (gangLabs.containsKey(block)) {
            gangLabs.remove(block);
        }
    }

    public boolean hasGangLab(Gang gang) {
        return gangLabs.values().stream().filter(gangLab -> gangLab.getOwner() == gang).findFirst().orElse(null) != null;
    }

    public GangLab getGangLabByBlock(Block block) {
        return gangLabs.values().stream().filter(gangLab -> gangLab.getBlock() == block).findFirst().orElse(null);
    }

    public void destroyAllGangsLabs() {
        gangLabs.values().stream().forEach(gangLab -> gangLab.getBlock().setType(Material.AIR));
    }

    public void enterGang(Gang gang) {
        if (!gangPresent(gang)) {
            gangGainedExp.put(gang, 0.0);
        }
    }

    public void updateGang(Gang gang, double value) {
        if (!gangPresent(gang)) {
            enterGang(gang);
        }
        double expGained = gangGainedExp.get(gang);
        expGained = expGained + value;
        gangGainedExp.replace(gang,expGained);
    }

    public boolean gangPresent(Gang gang) {
        return gangGainedExp.containsKey(gang);
    }
}
