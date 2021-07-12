package net.timme7893.gangs.gangs.utils;

import org.bukkit.Bukkit;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;

public class Booster {

    private Booster booster;
    private Gang gang;
    private boolean temporary;
    private int duration;
    private double value;

    public Booster(Gang gang, boolean temporary, int duration, double value, boolean pushToDatabase) {
        this.booster = this;
        this.gang = gang;
        this.temporary = temporary;
        this.duration = duration * 60 * 20;
        this.value = value;

        gang.addBooster(this, pushToDatabase);

        if (!temporary) {
            this.duration = 0;
        } else {
            startCooldown();
        }
    }

    public void startCooldown() {
        Bukkit.getScheduler().runTaskLater(GangsPlugin.getInstance(), () -> {
            gang.removeBooster(booster);
        }, duration);
    }

    public Gang getGang() {
        return gang;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public int getDuration() {
        return duration;
    }

    public double getValue() {
        return value;
    }
}
