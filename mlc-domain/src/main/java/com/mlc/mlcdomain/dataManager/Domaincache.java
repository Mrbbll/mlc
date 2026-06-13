package com.mlc.mlcdomain.dataManager;

import java.util.HashMap;
import java.util.Map;

public class Domaincache {
    //String cacheKey = world + "_" + x + "_" + z;



    public static final Map<String,DomainData> domaincache = new HashMap<>();
    public static void clearCache(){
        domaincache.clear();
    }
    public static void addCache(String domain,DomainData domainData){
        domaincache.put(domain,domainData);
    }
    public static DomainData getCache(String domain){
        return domaincache.get(domain);
    }
    public static boolean hasCache(String domain){
        return domaincache.containsKey(domain);
    }
    public static void removeCache(String domain){
        domaincache.remove(domain);
    }
    public static void updateCache(String domain,DomainData domainData){
        domaincache.put(domain,domainData);
    }
}
