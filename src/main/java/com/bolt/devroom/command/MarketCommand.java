package com.bolt.devroom.command;

import com.bolt.devroom.ui.MarketUi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MarketCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        if (!player.hasPermission("marketplace.view")) {
            player.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }

        MarketUi.openToPlayer(player);
        return true;
    }
}
