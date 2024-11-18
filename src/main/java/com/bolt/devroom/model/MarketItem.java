package com.bolt.devroom.model;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;


public record MarketItem(String id, ItemStack item, UUID owner, int price) { }
