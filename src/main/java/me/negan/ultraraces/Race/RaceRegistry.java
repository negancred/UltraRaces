package me.negan.ultraraces.Race;

import java.util.HashMap;
import java.util.Map;

public class RaceRegistry {

    private static final Map<String, Race> raceMap = new HashMap<>();

    public static void register(String name, Race race) {
        raceMap.put(name.toLowerCase(), race);
    }

    public static Race getRace(String name) {
        return raceMap.get(name.toLowerCase());
    }

}