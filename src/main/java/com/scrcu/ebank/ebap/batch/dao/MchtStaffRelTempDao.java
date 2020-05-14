package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtStaffRelTemp;

/**
 * <p>名称 : 商户关联人员临时表 </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/8/14 </p>
 */
public interface MchtStaffRelTempDao {
	
    List<MchtStaffRelTemp> selectList(String statement,Map<String,Object> parameter);
    
    public Integer count(String statement, Map<String, Object> parameter);
    
    MchtStaffRelTemp selectOne(String statement,Map<String,Object> parameter);
}
