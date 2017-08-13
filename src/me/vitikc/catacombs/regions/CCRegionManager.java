package me.vitikc.catacombs.regions;

import java.util.HashMap;

public class CCRegionManager {
    private static HashMap<String, CCRegion> regions = new HashMap<>();

    public static HashMap<String, CCRegion> getRegions() {
        return regions;
    }
}
