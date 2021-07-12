package net.timme7893.gangs.gangs.riots;

import lombok.Getter;
import net.timme7893.gangs.GangsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

public class RiotTimer {

    private GangsPlugin gangsPlugin;
    private RiotsManager riotsManager;
    private int betweenRiotsTime;
    private int riotDuration;
    private int announceTime;
    private boolean silencePeriod = true;

    @Getter
    private boolean active = false;
    private int timer;
    private int ticks;

    public RiotTimer(GangsPlugin gangsPlugin, RiotsManager riotsManager, int announceTime, int betweenRiotsTime, int riotDuration) {
        this.gangsPlugin = gangsPlugin;
        this.riotsManager = riotsManager;
        this.announceTime = announceTime;
        this.betweenRiotsTime = betweenRiotsTime;
        this.riotDuration = riotDuration;

        start();
    }

    public void start() {
        if (active) {
            return;
        }
        BukkitScheduler scheduler = gangsPlugin.getServer().getScheduler();
        timer = scheduler.scheduleSyncRepeatingTask(gangsPlugin, new Runnable() {
            @Override
            public void run() {

                if (riotsManager.getRiot() == null) {
                    silencePeriod = true;
                }

                if (silencePeriod) {
                    ticks++;

                    if ((betweenRiotsTime - ticks) == announceTime) {
                        riotsManager.announce(announceTime);
                    }

                    if (ticks >= betweenRiotsTime) {
                        silencePeriod = false;
                        ticks = 0;
                    }
                    return;
                } else {
                    ticks++;
                    if (riotsManager.getRiot() == null) {
                        riotsManager.start();
                        return;
                    }

                    if (ticks == 300 || ticks > 300) {
                        ticks = 0;
                        riotsManager.announceRiotMessage();
                    }

                    if (riotsManager.getRiot().tick()) {
                        // tick == true -> means Riot is over.
                        riotsManager.stop();
                        silencePeriod = true;
                        ticks = 0;
                    }
                }
            }
        }, 0L, 1200L);
    }

    public void stop() {
        if (!active) {
            return;
        }
        active = false;
        Bukkit.getScheduler().cancelTask(timer);
    }
}
