package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.WxBillResult;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface WxBillResultMapper {

    int insert(WxBillResult record);

    int insertSelective(WxBillResult record);

    int insertWxResult(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    int clear();

    int countLocal();

    int countOuter();
}