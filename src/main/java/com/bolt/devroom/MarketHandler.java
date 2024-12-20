package com.bolt.devroom;

import com.bolt.devroom.configuration.MarketConfiguration;
import com.bolt.devroom.database.MarketDatabase;
import com.bolt.devroom.hook.VaultHook;
import com.bolt.devroom.model.MarketItem;
import com.bolt.devroom.model.MarketTransaction;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bson.types.ObjectId;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;

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

    public static void save() {
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
            owner.sendMessage(MiniMessage.miniMessage().deserialize(MarketConfiguration.getMessage("item-not-found")));
            return;
        }

        if (price <= Integer.parseInt(MarketConfiguration.getSetting("min-price"))) {
            owner.sendMessage(MiniMessage.miniMessage().deserialize(MarketConfiguration.getMessage("invalid-price"),
                    Placeholder.unparsed("min-price", MarketConfiguration.getSetting("min-price"))));
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
        String itemName = item.getItemMeta().getDisplayName();
        if (itemName.isEmpty())
            itemName = item.getType().toString();

        owner.sendMessage(MiniMessage.miniMessage().deserialize(MarketConfiguration.getMessage("item-listed"),
                Placeholder.unparsed("price", String.valueOf(price)),
                Placeholder.unparsed("item-name", itemName)));
    }

    public static void buyItemFromMarket(MarketItem item, Player buyer, boolean blackMarket) {
        if (item == null)
            return;

        if (buyer == null)
            return;

        if (buyer.getInventory().firstEmpty() == -1) {
            buyer.sendMessage(MiniMessage.miniMessage().deserialize(MarketConfiguration.getMessage("inventory-full")));
            return;
        }

        double buyerBalance = VaultHook.getEconomy().getBalance(buyer);

        if (buyerBalance < item.price()) {
            buyer.sendMessage(MiniMessage.miniMessage().deserialize(MarketConfiguration.getMessage("not-enough-money"),
                    Placeholder.unparsed("price", String.valueOf(item.price())),
                    Placeholder.unparsed("balance", String.valueOf(buyerBalance))));
            return;
        }

        OfflinePlayer ownerPlayer = buyer.getServer().getOfflinePlayer(item.owner());

        double lastPrice = item.price();

        if (blackMarket) {
            lastPrice = (double) item.price() / 2;
        }

        VaultHook.getEconomy().withdrawPlayer(buyer, lastPrice);
        VaultHook.getEconomy().depositPlayer(ownerPlayer, item.price());

        String itemName = item.item().getItemMeta().getDisplayName();
        if (itemName.isEmpty())
            itemName = item.item().getType().toString();

        if (ownerPlayer.isOnline()) {
            Player owner = ownerPlayer.getPlayer();
            if (owner != null)
                owner.sendMessage(MiniMessage.miniMessage().deserialize(MarketConfiguration.getMessage("item-sold"),
                        Placeholder.unparsed("price", String.valueOf(item.price())),
                        Placeholder.unparsed("item-name", itemName)));
        }

        buyer.getInventory().addItem(item.item());
        marketItems.remove(item);
        buyer.sendMessage(MiniMessage.miniMessage().deserialize(MarketConfiguration.getMessage("item-bought"),
                Placeholder.unparsed("price", String.valueOf(lastPrice)),
                Placeholder.unparsed("item-name", itemName)));

        MarketTransaction transaction = new MarketTransaction(
                item.price(),
                item.owner().toString(),
                buyer.getUniqueId().toString(),
                itemName
        );

        WebHookHandler.sendTransaction(transaction);

        database.saveMarketTransaction(transaction);
    }

    public static void listPlayerTransactions(Player player) {
        List<MarketTransaction> transactions = database.getMarketTransactions(player.getUniqueId());

        if (transactions.isEmpty()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(MarketConfiguration.getMessage("no-transactions")));
            return;
        }

        player.sendMessage(MiniMessage.miniMessage().deserialize(MarketConfiguration.getMessage("transactions-header")));
        transactions.forEach(transaction -> {
            if (transaction.buyer().equals(player.getUniqueId().toString())) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(MarketConfiguration.getMessage("you-bought"),
                        Placeholder.unparsed("item-name", transaction.itemName()),
                        Placeholder.unparsed("price", String.valueOf(transaction.cost())),
                        Placeholder.unparsed("seller", transaction.seller())));
            } else {
                player.sendMessage(MiniMessage.miniMessage().deserialize(MarketConfiguration.getMessage("you-sold"),
                        Placeholder.unparsed("item-name", transaction.itemName()),
                        Placeholder.unparsed("price", String.valueOf(transaction.cost())),
                        Placeholder.unparsed("buyer", transaction.buyer())));
            }
        });

    }
}
