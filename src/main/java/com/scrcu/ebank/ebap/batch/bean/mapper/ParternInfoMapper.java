package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.ParternInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ParternInfoMapper {
    int deleteByPrimaryKey(String parternId);

    int insert(ParternInfo record);

    int insertSelective(ParternInfo record);

    ParternInfo selectByPrimaryKey(String parternId);

    int updateByPrimaryKeySelective(ParternInfo record);

    int updateByPrimaryKey(ParternInfo record);

    List<ParternInfo> selectParternList(@Param("parternCodeList") List<String>parternCodeList);
}