package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BthVatInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BthVatInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(BthVatInfo record);

    int insertSelective(BthVatInfo record);

    BthVatInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BthVatInfo record);

    int updateByPrimaryKey(BthVatInfo record);

    int insertBatch(@Param("recordList")List<BthVatInfo> recordList);
}