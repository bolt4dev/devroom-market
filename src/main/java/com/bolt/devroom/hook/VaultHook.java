package com.bolt.devroom.hook;

import com.bolt.devroom.MarketPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private static Economy economy = null;


    public static Economy getEconomy() {
        return economy;
    }

    public static boolean setupEconomy() {
        if (MarketPlugin.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = MarketPlugin.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }


}
