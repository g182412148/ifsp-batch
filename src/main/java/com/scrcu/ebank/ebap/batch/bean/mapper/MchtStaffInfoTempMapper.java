package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtStaffInfoTemp;

public interface MchtStaffInfoTempMapper {
    /**
     * 根据商户号查询员工信息
     */
	MchtStaffInfoTemp selectCertNobyMchtId(Map<String, String> map);

    MchtStaffInfoTemp selectMchtStaffInfoTemp_isLimkman(String staffId);
}