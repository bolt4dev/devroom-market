package com.bolt.devroom.database;

import com.bolt.devroom.model.MarketItem;
import com.bolt.devroom.model.MarketTransaction;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface MarketDatabase {
    void saveMarketItems(List<MarketItem> items) throws IOException;
    List<MarketItem> loadMarketItems() throws IOException, ClassNotFoundException;
    List<MarketTransaction> getMarketTransactions(UUID userId);
    void saveMarketTransaction(MarketTransaction transaction);
}
