package com.scrcu.ebank.ebap.batch.bean.mapper;


import com.scrcu.ebank.ebap.batch.bean.dto.MchtBlackGreyInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MchtBlackGreyInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(MchtBlackGreyInfo record);

    int insertSelective(MchtBlackGreyInfo record);

    MchtBlackGreyInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(MchtBlackGreyInfo record);

    int updateByPrimaryKey(MchtBlackGreyInfo record);

    MchtBlackGreyInfo selectByMchtId(String mchtId);
	MchtBlackGreyInfo queryStateByMchtId(@Param("mchtId") String mchtId);



}