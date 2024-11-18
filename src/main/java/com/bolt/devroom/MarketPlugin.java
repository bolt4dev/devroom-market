package com.bolt.devroom;

import com.bolt.devroom.command.BlackMarketCommand;
import com.bolt.devroom.command.MarketCommand;
import com.bolt.devroom.command.SellCommand;
import com.bolt.devroom.command.TransactionsCommand;
import com.bolt.devroom.database.MongoMarketDatabase;
import com.bolt.devroom.hook.VaultHook;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

// TODO: Add configuration files and configurable messages
// TODO: Add permissions

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
        MarketHandler.initialize(new MongoMarketDatabase("mongodb://localhost:32769"));
        logger.info("Market plugin has been enabled.");

        this.getCommand("market").setExecutor(new MarketCommand());
        this.getCommand("blackmarket").setExecutor(new BlackMarketCommand());
        this.getCommand("transactions").setExecutor(new TransactionsCommand());
        this.getCommand("sell").setExecutor(new SellCommand());
    }

    @Override
    public void onDisable() {
        MarketHandler.stop();
    }
}
