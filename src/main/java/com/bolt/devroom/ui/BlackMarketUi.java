package com.bolt.devroom.ui;

import com.bolt.devroom.MarketHandler;
import com.bolt.devroom.model.MarketItem;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlackMarketUi {

    public BlackMarketUi() {}

    public void open(Player player) {
        int i = 0;

        PaginatedGui gui = Gui.paginated()
                .title(MiniMessage.miniMessage().deserialize("<bold><white>Market"))
                .rows(6)
                .disableAllInteractions()
                .create();

        gui.setItem(6, 3, ItemBuilder.from(Material.ARROW).name(MiniMessage.miniMessage().deserialize("<bold><white>Previous")).asGuiItem(event -> gui.previous()));
        gui.setItem(6, 7, ItemBuilder.from(Material.ARROW).name(MiniMessage.miniMessage().deserialize("<bold><white>Next")).asGuiItem(event -> gui.next()));
        gui.setItem(6, 5, ItemBuilder.from(Material.BARRIER).name(MiniMessage.miniMessage().deserialize("<bold><white>Close")).asGuiItem(event -> gui.close(player)));
        gui.setItem(6, 1, ItemBuilder.from(Material.BOOK).name(MiniMessage.miniMessage().deserialize("<bold><white>Information"))
                .lore(
                        List.of(
                                MiniMessage.miniMessage().deserialize("<gray>You can use <white>/sell <price> <gray>to sell your item.")
                        )
                )
                .asGuiItem());


        List<GuiItem> guiItems = new ArrayList<>();
        List<MarketItem> marketItems = new ArrayList<>();

        Random rand = new Random();

        if (MarketHandler.getMarketItems().size() <= 2) {
            for (MarketItem item : MarketHandler.getMarketItems()) {
                guiItems.add(ItemBuilder.from(item.item())
                        .asGuiItem(event -> {
                            ConfirmationUi.openToPlayer(player, item, true);
                        }));
                i++;
            }
        } else {
            for (int j = 0; j < 2; j++) {
                int randomIndex = rand.nextInt(MarketHandler.getMarketItems().size());
                MarketItem randomItem = MarketHandler.getMarketItems().get(randomIndex);
                if (!marketItems.contains(randomItem)) {
                    marketItems.add(randomItem);
                    guiItems.add(ItemBuilder.from(randomItem.item())
                            .asGuiItem(event -> {
                                ConfirmationUi.openToPlayer(player, randomItem, true);
                            }));
                } else {
                    j--;
                }
            }
        }

        guiItems.forEach(gui::addItem);


        gui.open(player);
    }


    public static void openToPlayer(Player player) {
        BlackMarketUi ui = new BlackMarketUi();
        ui.open(player);
    }
}
