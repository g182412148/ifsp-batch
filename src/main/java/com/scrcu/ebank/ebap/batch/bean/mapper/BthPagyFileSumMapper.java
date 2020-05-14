package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BthPagyFileSum;
import org.apache.ibatis.annotations.Param;

public interface BthPagyFileSumMapper {
    int deleteByPrimaryKey(@Param("pagyNo") String pagyNo, @Param("chkDataDt") String chkDataDt);

    int insert(BthPagyFileSum record);

    int insertSelective(BthPagyFileSum record);

    BthPagyFileSum selectByPrimaryKey(@Param("pagyNo") String pagyNo, @Param("chkDataDt") String chkDataDt);

    int updateByPrimaryKeySelective(BthPagyFileSum record);

    int updateByPrimaryKey(BthPagyFileSum record);

    void deleteByPagySysNoAndDate(@Param("pagySysNo")String pagySysNo, @Param("settleDate")String settleDate);
}