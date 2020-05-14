package com.scrcu.ebank.ebap.batch.dao;


import com.scrcu.ebank.ebap.batch.bean.dto.AreaInfo;

/**
 * <p>名称 : 地区表查询Dao </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/8/2 </p>
 */
public interface AreaInfoDao {
    /**
     * 根据地区号查询地区名称
     * @param areaCode 地区号
     * @return 地区信息
     */
    AreaInfo selectByPrimaryKey(String areaCode);

}
