package com.bolt.devroom.command;

import com.bolt.devroom.configuration.MarketConfiguration;
import com.bolt.devroom.ui.BlackMarketUi;
import com.bolt.devroom.util.CooldownManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class BlackMarketCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        if (!player.hasPermission("marketplace.blackmarket")) {
            player.sendMessage(MarketConfiguration.getMessage("no-permission"));
            return true;
        }

        Duration timeLeft = CooldownManager.getRemainingCooldown(player.getUniqueId());
        if (timeLeft.isZero() || timeLeft.isNegative()) {
            BlackMarketUi.openToPlayer(player);
            CooldownManager.setCooldown(player.getUniqueId(), Duration.ofSeconds(Integer.parseInt(MarketConfiguration.getSetting("blackmarket-cooldown"))));
            return true;
        } else {
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                    MarketConfiguration.getMessage("blackmarket-cooldown"),
                    Placeholder.unparsed("cooldown", String.valueOf(timeLeft.toSeconds()))
            ));
            return true;
        }
    }
}

