package com.scrcu.ebank.ebap.batch.bean.vo;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtGainsInfo;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分润信息缓存
 */
public class CacheServiceGainsInfo {

    static final String a = "_";
    private static ConcurrentHashMap<String ,List<MchtGainsInfo>> caches;

    private CacheServiceGainsInfo(){}

    static {
        caches = new ConcurrentHashMap<>();

    }

    /**
     * 用于保存缓存
     * @param key1
     * @param value
     */
    public static void addCache(String key1,String key2 ,List<MchtGainsInfo> value){
        caches.put(key1+a+key2,value);
    }

    /**
     * 用于得到缓存
     * @param key1
     * @return
     */
    public static List<MchtGainsInfo> getCache(String key1,String key2){
        return caches.get(key1+a+key2);
    }

    /**
     * 用于清除缓存
     */
    public static void clearCache(){
        caches.clear();
    }

    /**
     * 用于清除指定的缓存
     * @param key1
     */
    public static void removeCache(String key1,String key2){
        caches.remove(key1+a+key2);
    }
}
