package com.bolt.devroom;

import com.bolt.devroom.configuration.MarketConfiguration;
import com.bolt.devroom.model.MarketTransaction;
import io.github._4drian3d.jdwebhooks.Embed;
import io.github._4drian3d.jdwebhooks.WebHook;
import io.github._4drian3d.jdwebhooks.WebHookClient;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class WebHookHandler {
    private static WebHookClient client;

    public static void initialize(String url) {
        client = WebHookClient.fromURL(url);
    }

    public static void sendTransaction(MarketTransaction transaction) {
        if (client == null) {
            return;
        }

        String title = MarketConfiguration.getSetting("webhook.title");
        String description = MarketConfiguration.getSetting("webhook.description");
        String userName = MarketConfiguration.getSetting("webhook.username");
        String avatarUrl = MarketConfiguration.getSetting("webhook.avatar-url");
        // placeholders
        Player player = Bukkit.getPlayer(UUID.fromString(transaction.buyer()));
        title = title.replaceAll("<item>", transaction.itemName());
        title = title.replaceAll("<price>", String.valueOf(transaction.cost()));
        title = title.replaceAll("<player>", player.getDisplayName());
        description = description.replaceAll("<item>", transaction.itemName());
        description = description.replaceAll("<price>", String.valueOf(transaction.cost()));
        description = description.replaceAll("<player>", player.getDisplayName());

        final Embed embed = Embed.builder()
                .author(Embed.Author.builder().name(transaction.itemName() + " - " + transaction.cost()).build())
                .timestamp(Instant.now())
                .color(0xFF0000)
                .image(Embed.Image.builder().url(avatarUrl).build())
                .title(title)
                .description(description)
                .build();

        final WebHook webHook = WebHook.builder()
                .username(userName)
                .embed(embed)
                .build();

        final CompletableFuture<HttpResponse<String>> futureResponse = client.sendWebHook(webHook);
    }
}
