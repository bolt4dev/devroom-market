package com.bolt.devroom.command;

import com.bolt.devroom.MarketHandler;
import com.bolt.devroom.model.MarketTransaction;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TransactionsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        List<MarketTransaction> transactions = MarketHandler.getPlayerTransactions(player.getUniqueId());

        if (transactions.isEmpty()) {
            player.sendMessage("You have no transactions");
            return true;
        }

        transactions.forEach(transaction -> {
            if (transaction.buyer().equals(player.getUniqueId().toString())) {
                player.sendMessage("You bought " + transaction.itemName() + "for " + transaction.cost() + " from " + transaction.seller());
            } else {
                player.sendMessage("You sold " + transaction.itemName() + "for " + transaction.cost() + " to " + transaction.buyer());
            }
        });
    return true;
    }
}
