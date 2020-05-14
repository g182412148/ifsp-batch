package com.scrcu.ebank.ebap.batch.bean.vo;

import com.scrcu.ebank.ebap.batch.bean.dto.ParternBaseInfo;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务商信息缓存
 */
public class CacheServiceBaseInfo {


    private static ConcurrentHashMap<String ,ParternBaseInfo> caches;

    private CacheServiceBaseInfo(){}

    static {
        caches = new ConcurrentHashMap<>();
    }

    /**
     * 用于保存缓存
     * @param key
     * @param value
     */
    public static void addCache(String key ,ParternBaseInfo value){
        caches.put(key,value);
    }

    /**
     * 用于得到缓存
     * @param key
     * @return
     */
    public static ParternBaseInfo getCache(String key){
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
