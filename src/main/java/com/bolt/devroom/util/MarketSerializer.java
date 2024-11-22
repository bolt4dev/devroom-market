package com.bolt.devroom.util;

import org.bukkit.inventory.ItemStack;
import java.util.Base64;

public class MarketSerializer {
    public static String serializeItem(ItemStack item) {
        byte[] serializedObject = item.serializeAsBytes();

        return Base64.getEncoder().encodeToString(serializedObject);
    }

    public static ItemStack deserializeItem(String serializedItem) {
        byte[] serializedObject = Base64.getDecoder().decode(serializedItem);

        return ItemStack.deserializeBytes(serializedObject);
    }

}
