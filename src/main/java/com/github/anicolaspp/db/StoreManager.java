package com.github.anicolaspp.db;

import lombok.val;
import org.ojai.store.Connection;
import org.ojai.store.DocumentStore;

import java.util.HashMap;
import java.util.Map;

public class StoreManager {
    private static Map<String, DocumentStore> stores = new HashMap<>();

    private StoreManager() {}

    public static DocumentStore getStoreFor(String table, Connection connection) {
        if (stores.containsKey(table)) {
            return stores.get(table);
        } else {
            val store = connection.getStore(table);

            stores.put(table, store);

            return store;
        }
    }
}
