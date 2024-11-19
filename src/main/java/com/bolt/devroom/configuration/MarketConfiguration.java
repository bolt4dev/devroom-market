package com.bolt.devroom.configuration;

import com.bolt.devroom.MarketPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MarketConfiguration {
    private static File file;
    private static YamlConfiguration configuration;

    public static void initialize(MarketPlugin instance) {
        file = new File(instance.getDataFolder(), "settings.yml");
        if (!file.exists()) {
            instance.saveResource("settings.yml", false);
        }

        configuration = new YamlConfiguration();
        configuration.options().parseComments(true);
        try {
            configuration.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getMessage(String message) {
        return configuration.getString("messages." + message);
    }

    public static String getSetting(String setting) {
        return configuration.getString("settings." + setting);
    }

    public static void reload() {
        try {
            configuration.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
