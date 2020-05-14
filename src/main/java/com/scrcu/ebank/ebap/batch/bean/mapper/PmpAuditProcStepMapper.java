package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.PmpAuditProcStep;

public interface PmpAuditProcStepMapper {
    int deleteByPrimaryKey(String id);

    int insert(PmpAuditProcStep record);

    int insertSelective(PmpAuditProcStep record);

    PmpAuditProcStep selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PmpAuditProcStep record);

    int updateByPrimaryKey(PmpAuditProcStep record);

    /**
     * <!-- 查询符合条件的审核步骤 -->
     * @param map
     * @return
     */
    List<PmpAuditProcStep> selectTepList(Map<String,String> map);
}