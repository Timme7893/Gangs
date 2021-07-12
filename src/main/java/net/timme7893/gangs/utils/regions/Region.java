package net.timme7893.gangs.utils.regions;

import org.apache.commons.lang.math.IntRange;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Region {

    public static boolean inCuboid(Location origin, Location l1, Location l2) {
        return new IntRange(l1.getX(), l2.getX()).containsDouble(origin.getX())
                && new IntRange(l1.getY(), l2.getY()).containsDouble(origin.getY())
                && new IntRange(l1.getZ(), l2.getZ()).containsDouble(origin.getZ());
    }

    public static List<Location> getNearbyLocations(Location location, int radius) {
        List<Location> locs = new ArrayList<Location>();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    locs.add(new Location(location.getWorld(), x, y, z));
                }
            }
        }
        return locs;
    }
}
