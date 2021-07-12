package net.timme7893.gangs.gangs.factories;

import java.util.HashMap;

import org.bukkit.scheduler.BukkitScheduler;

import net.timme7893.gangs.GangsPlugin;

public class FactoryTimer {

    private GangsPlugin gangsPlugin;
    private int timer;

    private int hour;

    public FactoryTimer(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
        start();
    }

    public void start() {
        BukkitScheduler scheduler = gangsPlugin.getServer().getScheduler();
        timer = scheduler.scheduleSyncRepeatingTask(gangsPlugin, new Runnable() {
            @Override
            public void run() {

                HashMap<Integer,Factory> factories = new HashMap<Integer, Factory>(gangsPlugin.getFactoriesManager().getFactories());
                hour++;

                // UTILS
                if (hour == 8 || hour == 16 || hour == 24) {
                    gangsPlugin.getUpdateManager().startUpdate();
                    // UPDATING DATA TO PREVENT LOSS.
                }
                // END UTILS

                if (hour == 24 || hour > 24) {
                    hour = 0;
                    factories.values().stream().forEach(factory -> {
                        if (!factory.hasPaid()) {
                            factory.setOwner(null);
                        }
                        factory.setPaidRent(false);
                    });
                }


                factories.values().stream().forEach(factory -> {
                    if (factory.isBought()) {
                        factory.produce();
                    }
                });
            }
        }, 20L, 3600L);
    }
}
