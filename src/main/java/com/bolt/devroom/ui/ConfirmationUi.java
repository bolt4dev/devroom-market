package com.bolt.devroom.ui;

import com.bolt.devroom.MarketHandler;
import com.bolt.devroom.model.MarketItem;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ConfirmationUi {

    public ConfirmationUi() {}

    public void open(Player player, MarketItem item, boolean blackMarket) {
        Gui gui = Gui.gui()
                .title(MiniMessage.miniMessage().deserialize("<bold><white>Are you sure?"))
                .rows(6)
                .disableAllInteractions()
                .create();

        gui.setItem(3, 1, ItemBuilder.from(Material.GREEN_WOOL).name(MiniMessage.miniMessage().deserialize("<bold><white>Confirm - Price: " + item.price())).asGuiItem(event -> {
            MarketHandler.buyItemFromMarket(item, player, blackMarket);
                MarketUi.openToPlayer(player);

        }));
        gui.setItem(3, 9, ItemBuilder.from(Material.BARRIER).name(MiniMessage.miniMessage().deserialize("<bold><white>Cancel")).asGuiItem(event -> {
                MarketUi.openToPlayer(player);
        }));

        gui.setItem(4, 5, ItemBuilder.from(item.item())
                .asGuiItem());

        gui.open(player);
    }


    public static void openToPlayer(Player player, MarketItem item, boolean blackMarket) {
        ConfirmationUi ui = new ConfirmationUi();
        ui.open(player, item, blackMarket);
    }
}
