package net.timme7893.gangs.gangs.houses;

import org.bukkit.Location;

import lombok.Getter;
import net.timme7893.gangs.gangs.Gang;

public class GangHouse {

    private int id;
    private Location loc1;
    private Location loc2;
    private Location spawnLoc;
    private Gang gang;
    @Getter
    private GangMine gangMine;

    public GangHouse(int id, Location loc1, Location loc2, Location spawnLoc, Gang gang) {
        this.id = id;
        this.loc1 = loc1;
        this.loc2 = loc2;
        this.spawnLoc = spawnLoc;
        this.gang = gang;
        gang.setGangHouse(this);
        this.gangMine = new GangMine(gang);
    }

    public int getId() {
        return id;
    }

    public Location getLoc1() {
        return loc1;
    }

    public Location getLoc2() {
        return loc2;
    }

    public Gang getGang() {
        return gang;
    }

    public Location getSpawnLoc() {
        return spawnLoc;
    }

    public void setSpawnLoc(Location spawnLoc) {
        this.spawnLoc = spawnLoc;
    }

}
