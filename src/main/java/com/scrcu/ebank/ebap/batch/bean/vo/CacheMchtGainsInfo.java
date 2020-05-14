package com.scrcu.ebank.ebap.batch.bean.vo;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtGainsInfo;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分润信息缓存
 */
public class CacheMchtGainsInfo {

    private static ConcurrentHashMap<String ,List<MchtGainsInfo>> caches;

    private CacheMchtGainsInfo(){}

    static {
        caches = new ConcurrentHashMap<>();
    }

    /**
     * 用于保存缓存
     * @param key
     * @param value
     */
    public static void addCache(String key ,List<MchtGainsInfo> value){
        caches.put(key,value);
    }

    /**
     * 用于得到缓存
     * @param key
     * @return
     */
    public static List<MchtGainsInfo> getCache(String key){
        return caches.get(key);
    }

    /**
     * 用于清除缓存
     */
    public static void clearCache(){
        caches.clear();
    }

    /**
     * 用于清除指定的缓存
     * @param key
     */
    public static void removeCache(String key){
        caches.remove(key);
    }
}
