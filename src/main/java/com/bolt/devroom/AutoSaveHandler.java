package com.bolt.devroom;

import com.bolt.devroom.configuration.MarketConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

public class AutoSaveHandler {

    public static void start() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(MarketPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                MarketHandler.save();
            }
        }, 0L, 20L * Integer.parseInt(MarketConfiguration.getSetting("auto-save-interval")));
    }

}
