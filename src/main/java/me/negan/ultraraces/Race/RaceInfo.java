package me.negan.ultraraces.Race;

import java.util.Set;
import java.util.HashSet;

public class RaceInfo {

    private static final Set<String> validRaces = new HashSet<>();

    static {
        validRaces.add("goblin");
        validRaces.add("merman");
        validRaces.add("human");
        validRaces.add("ghost");
        validRaces.add("werewolf");
        validRaces.add("undead");
        validRaces.add("cosmic");
        validRaces.add("vampire");
        validRaces.add("angel");
        validRaces.add("piglin");
        validRaces.add("sentinel");
        validRaces.add("howler");
        validRaces.add("marionette");
        validRaces.add("goddess");
        validRaces.add("assassin");

    }

    public static boolean isValidRace(String raceKey) {
        return validRaces.contains(raceKey.toLowerCase());
    }

    public static Iterable<String> getAllRaceKeys() {
        return validRaces;
    }
}