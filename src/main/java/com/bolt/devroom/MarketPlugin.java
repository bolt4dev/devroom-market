package com.bolt.devroom;

import com.bolt.devroom.command.*;
import com.bolt.devroom.configuration.MarketConfiguration;
import com.bolt.devroom.database.MongoMarketDatabase;
import com.bolt.devroom.hook.VaultHook;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

// TODO: Add discord webhook for transactions

public class MarketPlugin extends JavaPlugin {
    private static MarketPlugin instance;

    public static MarketPlugin getInstance() {
        return instance;
    }


    /*
    ROOT
     */
    @Override
    public void onEnable() {
        instance = this;
        Logger logger = getLogger();
        boolean vault = VaultHook.setupEconomy();
        if (!vault) {
           logger.severe("Vault not found! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        MarketConfiguration.initialize(this);
        MarketHandler.initialize(new MongoMarketDatabase(MarketConfiguration.getSetting("database.connection-string"), MarketConfiguration.getSetting("database.name")));
        logger.info("Market plugin has been enabled.");

        this.getCommand("market").setExecutor(new MarketCommand());
        this.getCommand("blackmarket").setExecutor(new BlackMarketCommand());
        this.getCommand("transactions").setExecutor(new TransactionsCommand());
        this.getCommand("sell").setExecutor(new SellCommand());
        this.getCommand("reloadMarket").setExecutor(new ReloadCommand());
    }

    @Override
    public void onDisable() {
        MarketHandler.stop();
    }
}
