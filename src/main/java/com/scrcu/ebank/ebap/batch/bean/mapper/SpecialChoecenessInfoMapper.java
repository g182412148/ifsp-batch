package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.SpecialChoecenessInfo;

import java.util.List;

public interface SpecialChoecenessInfoMapper {
    int deleteByPrimaryKey(String specialId);

    int insert(SpecialChoecenessInfo record);

    int insertSelective(SpecialChoecenessInfo record);

    SpecialChoecenessInfo selectByPrimaryKey(String specialId);

    int updateByPrimaryKeySelective(SpecialChoecenessInfo record);

    int updateByPrimaryKey(SpecialChoecenessInfo record);

    List<SpecialChoecenessInfo> selectSpecialChoecenessInfo(String specialParCpt);
}