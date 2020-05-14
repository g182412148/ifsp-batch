package com.scrcu.ebank.ebap.batch.common.utils;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtBaseInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtContInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtGainsInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtSettlRateCfg;

import java.util.HashMap;
import java.util.Map;


public class MapConstant {


    // 商户基本信息
    public static Map<String,MchtBaseInfo> mchtBaseInfoMap = new HashMap<String,MchtBaseInfo>();
    // 商户合同信息
    public static Map<String,MchtContInfo> mchtContInfoMap = new HashMap<String,MchtContInfo>();
    // 商户结算费率配置表
    public static Map<String,MchtSettlRateCfg> mchtSettlRateCfgMap = new HashMap<>();
    // 商户分润表
    public static Map<String,MchtGainsInfo> mchtGainsInfoMap = new HashMap<>();

    // 节假日表
//    public static Map<String,ParamHolidays> paramHolidaysMap = new HashMap<String,ParamHolidays>();



    public static Map<String, MchtGainsInfo> getMchtGainsInfoMap() {
        return mchtGainsInfoMap;
    }

    public static void setMchtGainsInfoMap(Map<String, MchtGainsInfo> mchtGainsInfoMap) {
        MapConstant.mchtGainsInfoMap = mchtGainsInfoMap;
    }

    public static Map<String, MchtSettlRateCfg> getMchtSettlRateCfgMap() {
        return mchtSettlRateCfgMap;
    }

    public static void setMchtSettlRateCfgMap(Map<String, MchtSettlRateCfg> mchtSettlRateCfgMap) {
        MapConstant.mchtSettlRateCfgMap = mchtSettlRateCfgMap;
    }

    public static Map<String, MchtBaseInfo> getMchtBaseInfoMap() {
        return mchtBaseInfoMap;
    }

    public static void setMchtBaseInfoMap(Map<String, MchtBaseInfo> mchtBaseInfoMap) {
        MapConstant.mchtBaseInfoMap = mchtBaseInfoMap;
    }

    public static Map<String, MchtContInfo> getMchtContInfoMap() {
        return mchtContInfoMap;
    }

    public static void setMchtContInfoMap(Map<String, MchtContInfo> mchtContInfoMap) {
        MapConstant.mchtContInfoMap = mchtContInfoMap;
    }
}
