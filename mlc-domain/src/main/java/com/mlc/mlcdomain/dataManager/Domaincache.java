package com.mlc.mlcdomain.dataManager;

import java.util.HashMap;
import java.util.Map;

public class Domaincache {
    // String cacheKey = world + "_" + x + "_" + z

    private static final Map<String, DomainData> domaincache = new HashMap<>();

    public static void clearCache() {
        domaincache.clear();
    }

    public static void addCache(String key, DomainData domainData) {
        domaincache.put(key, domainData);
    }

    public static DomainData getCache(String key) {
        return domaincache.get(key);
    }

    public static boolean hasCache(String key) {
        return domaincache.containsKey(key);
    }

    public static void removeCache(String key) {
        domaincache.remove(key);
    }

    public static void updateCache(String key, DomainData domainData) {
        domaincache.put(key, domainData);
    }

    public static int size() {
        return domaincache.size();
    }
}
