package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtStaffInfoTemp;

/**
 * <p>名称 : 商户人员信息临时表Dao </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/8/14 </p>
 */
public interface MchtStaffInfoTempDao {
    
    List<MchtStaffInfoTemp> selectList(String statement,Map<String,Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    MchtStaffInfoTemp selectOne(String statement,Map<String,Object> parameter);
}
