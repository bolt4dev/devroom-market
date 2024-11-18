package com.bolt.devroom.command;

import com.bolt.devroom.MarketHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SellCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;

        if (strings.length > 0) {
            String costString = String.join(" ", strings);
            int cost = Integer.parseInt(costString);

            MarketHandler.sellItemOnMarket(player.getInventory().getItemInMainHand(), player, cost);
            player.sendMessage("Item has been put on the market for " + cost + " coins.");
        }

        return true;
    }
}
