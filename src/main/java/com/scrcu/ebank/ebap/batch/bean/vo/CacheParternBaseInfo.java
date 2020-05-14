package com.scrcu.ebank.ebap.batch.bean.vo;

import com.scrcu.ebank.ebap.batch.bean.dto.ParternBaseInfo;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存物流合作方信息
 */
public class CacheParternBaseInfo {

    private static ConcurrentHashMap<String ,List<ParternBaseInfo>> caches;

    private CacheParternBaseInfo(){}

    static {
        caches = new ConcurrentHashMap<>();
    }

    /**
     * 用于保存缓存
     * @param key
     * @param value
     */
    public static void addCache(String key ,List<ParternBaseInfo> value){
        caches.put(key,value);
    }

    /**
     * 用于得到缓存
     * @param key
     * @return
     */
    public static List<ParternBaseInfo> getCache(String key){
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
