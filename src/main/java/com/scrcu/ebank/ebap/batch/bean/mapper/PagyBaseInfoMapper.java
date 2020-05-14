package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.PagyBaseInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PagyBaseInfoMapper {
    int deleteByPrimaryKey(String pagyNo);

    int insert(PagyBaseInfo record);

    int insertSelective(PagyBaseInfo record);

    PagyBaseInfo selectByPrimaryKey(String pagyNo);

    int updateByPrimaryKeySelective(PagyBaseInfo record);

    int updateByPrimaryKey(PagyBaseInfo record);

    List<PagyBaseInfo> selectByPagyNo(@Param("wxSysNo") String wxSysNo);
}