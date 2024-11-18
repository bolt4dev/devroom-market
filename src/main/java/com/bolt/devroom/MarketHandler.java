package com.bolt.devroom;

import com.bolt.devroom.database.MarketDatabase;
import com.bolt.devroom.hook.VaultHook;
import com.bolt.devroom.model.MarketItem;
import com.bolt.devroom.model.MarketTransaction;
import org.bson.types.ObjectId;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MarketHandler {
    private static final List<MarketItem> marketItems = new ArrayList<>();
    private static MarketDatabase database;

    public static void initialize(MarketDatabase database) {
        MarketHandler.database = database;
        List<MarketItem> items = null;
        try {
            items = database.loadMarketItems();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (items != null)
            marketItems.addAll(items);
    }

    public static void stop() {
        try {
            database.saveMarketItems(marketItems);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<MarketItem> getMarketItems() {
        return marketItems;
    }

    public static void sellItemOnMarket(ItemStack item, Player owner, int price) {
        if (owner == null)
            return;

        if (item == null || item.getType().isAir()) {
            owner.sendMessage("§cYou can't sell air!");
            return;
        }

        if (price <= 0) {
            owner.sendMessage("§cPrice must be greater than 0!");
            return;
        }

        MarketItem marketItem = new MarketItem(
                new ObjectId().toHexString(),
                item,
                owner.getUniqueId(),
                price
        );

        marketItems.add(marketItem);
        owner.getInventory().removeItem(item);
        owner.sendMessage("§aItem has been put on the market!");
    }

    public static void buyItemFromMarket(MarketItem item, Player buyer, boolean blackMarket) {
        if (item == null)
            return;

        if (buyer == null)
            return;

        if (buyer.getInventory().firstEmpty() == -1) {
            buyer.sendMessage("§cYour inventory is full!");
            return;
        }

        double buyerBalance = VaultHook.getEconomy().getBalance(buyer);

        if (buyerBalance < item.price()) {
            buyer.sendMessage("§cYou don't have enough money to buy this item!");
            return;
        }

        OfflinePlayer ownerPlayer = buyer.getServer().getOfflinePlayer(item.owner());

        if (blackMarket) {
            VaultHook.getEconomy().withdrawPlayer(buyer, (double) item.price() /2);
        } else {
            VaultHook.getEconomy().withdrawPlayer(buyer, item.price());
        }


        VaultHook.getEconomy().depositPlayer(ownerPlayer, item.price());

        if (ownerPlayer.isOnline()) {
            Player owner = ownerPlayer.getPlayer();
            if (owner != null)
                owner.sendMessage("§aYour item has been sold!");
        }

        buyer.getInventory().addItem(item.item());
        marketItems.remove(item);
        buyer.sendMessage("§aYou have bought the item!");

        MarketTransaction transaction = new MarketTransaction(
                item.price(),
                item.owner().toString(),
                buyer.getUniqueId().toString(),
                item.item().getItemMeta().getDisplayName()
        );

        database.saveMarketTransaction(transaction);
    }

    public static List<MarketTransaction> getPlayerTransactions(UUID uid) {
        return database.getMarketTransactions(uid);
    }
}
