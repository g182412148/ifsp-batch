package com.scrcu.ebank.ebap.batch.bean.vo;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtOrgRel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 机构信息缓存
 */
public class CacheMchtOrgRel {

    private static ConcurrentHashMap<String ,List<MchtOrgRel>> caches;

    private CacheMchtOrgRel(){}

    static {
        caches = new ConcurrentHashMap<>();
    }

    /**
     * 用于保存缓存
     * @param key
     * @param value
     */
    public static void addCache(String key ,List<MchtOrgRel> value){
        caches.put(key,value);
    }

    /**
     * 用于保存缓存
     * @param map
     */
    public static void addAllCache(Map<String ,List<MchtOrgRel>> map){
        caches.putAll(map);
    }

    /**
     * 用于得到缓存
     * @param key
     * @return
     */
    public static List<MchtOrgRel> getCache(String key){
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
