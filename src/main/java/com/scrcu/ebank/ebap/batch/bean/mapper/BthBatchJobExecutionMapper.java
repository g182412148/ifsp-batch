package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BthBatchJobExecution;

public interface BthBatchJobExecutionMapper {
    int deleteByPrimaryKey(String jobExeId);

    int insert(BthBatchJobExecution record);

    int insertSelective(BthBatchJobExecution record);

    BthBatchJobExecution selectByPrimaryKey(String jobExeId);

    int updateByPrimaryKeySelective(BthBatchJobExecution record);

    int updateByPrimaryKeyWithBLOBs(BthBatchJobExecution record);

    int updateByPrimaryKey(BthBatchJobExecution record);
}