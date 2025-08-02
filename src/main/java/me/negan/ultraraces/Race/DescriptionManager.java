package me.negan.ultraraces.Race;

import me.negan.ultraraces.UltraRaces;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DescriptionManager {

    public static class Description {
        public final String name;
        public final String passive;
        public final String skill;

        public Description(String name, String passive, String skill) {
            this.name = name;
            this.passive = passive;
            this.skill = skill;
        }
    }
    private final Map<String, Description> descriptions = new HashMap<>();

    public DescriptionManager(UltraRaces plugin) {
        File file = new File(plugin.getDataFolder(), "racedescriptions.yml");

        if (!file.exists()) {
            plugin.saveResource("racedescriptions.yml", false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getKeys(false)) {
            String name = config.getString(key + ".name", key);
            String passive = config.getString(key + ".passive", "No passive");
            String skill = config.getString(key + ".skill", "No skill");
            descriptions.put(key.toLowerCase(), new Description(name, passive, skill));
        }
    }

    public Description getDescription(String raceKey) {
        return descriptions.get(raceKey.toLowerCase());
    }

    public boolean hasRace(String raceKey) {
        return descriptions.containsKey(raceKey.toLowerCase());
    }

}
