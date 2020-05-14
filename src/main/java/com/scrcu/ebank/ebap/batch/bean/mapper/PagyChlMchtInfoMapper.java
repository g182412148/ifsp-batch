package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.PagyChlMchtInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface PagyChlMchtInfoMapper {
    int deleteByPrimaryKey(String chlMchtNo);

    int insert(PagyChlMchtInfo record);

    int insertSelective(PagyChlMchtInfo record);

    PagyChlMchtInfo selectByPrimaryKey(String chlMchtNo);

    int updateByPrimaryKeySelective(PagyChlMchtInfo record);

    int updateByPrimaryKey(PagyChlMchtInfo record);

    void updateStateByChlMchtNo(@Param("chlMchtNo") String chlMchtNo,@Param("date") Date date);
}