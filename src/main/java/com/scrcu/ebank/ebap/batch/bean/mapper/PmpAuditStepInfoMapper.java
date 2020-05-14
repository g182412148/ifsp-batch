package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.PmpAuditStepInfo;

public interface PmpAuditStepInfoMapper {
    int deleteByPrimaryKey(String seqId);

    int insert(PmpAuditStepInfo record);

    int insertSelective(PmpAuditStepInfo record);

    PmpAuditStepInfo selectByPrimaryKey(String seqId);

    int updateByPrimaryKeySelective(PmpAuditStepInfo record);

    int updateByPrimaryKey(PmpAuditStepInfo record);

    /**
     * 查询 审核流程编号
     * @param brno
     * @return
     */
    List<String> selectAuditIdByBrno(String brno);
    /**
     * 根据操作员编号和角色编号查询流程步骤
     * @param map
     * @return
     */
	PmpAuditStepInfo selectAuditStep(Map<String, String> map);
	/**
	 * 查询下一个审核
	 * @param map
	 * @return
	 */

	PmpAuditStepInfo selectNextStep(HashMap<Object, Object> map);

    List<PmpAuditStepInfo> selectAuditStepList(Map<String, String> hashMap);
}