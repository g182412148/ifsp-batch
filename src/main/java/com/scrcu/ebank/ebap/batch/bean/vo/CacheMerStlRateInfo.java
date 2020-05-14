package com.scrcu.ebank.ebap.batch.bean.vo;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtSettlRateCfg;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 根据商户号、渠道号、交易账户类型查询手续费费率信息缓存
 */
public class CacheMerStlRateInfo
{


    private static ConcurrentHashMap<String ,MchtSettlRateCfg> caches;

    private CacheMerStlRateInfo(){}

    static {
        caches = new ConcurrentHashMap<>();
    }

    /**
     * 用于保存缓存
     * @param key
     * @param value
     */
    public static void addCache(String key ,MchtSettlRateCfg value){
        caches.put(key,value);
    }

    /**
     * 用于得到缓存
     * @param key
     * @return
     */
    public static MchtSettlRateCfg getCache(String key){
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
