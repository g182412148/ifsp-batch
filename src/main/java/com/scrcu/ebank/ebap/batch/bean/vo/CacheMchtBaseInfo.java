package com.scrcu.ebank.ebap.batch.bean.vo;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtBaseInfo;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 商户信息缓存
 */
public class CacheMchtBaseInfo {


    private static ConcurrentHashMap<String ,MchtBaseInfo> caches;

    private CacheMchtBaseInfo(){}

    static {
        caches = new ConcurrentHashMap<>();
    }

    /**
     * 用于保存缓存
     * @param key
     * @param value
     */
    public static void addCache(String key ,MchtBaseInfo value){
        caches.put(key,value);
    }

    /**
     * 用于得到缓存
     * @param key
     * @return
     */
    public static MchtBaseInfo getCache(String key){
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
