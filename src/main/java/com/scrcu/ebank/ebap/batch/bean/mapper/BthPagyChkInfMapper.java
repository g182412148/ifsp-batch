package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BthPagyChkInf;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface BthPagyChkInfMapper {
    int deleteByPrimaryKey(@Param("pagyNo") String pagyNo, @Param("chkDataDt") Date chkDataDt);

    int insert(BthPagyChkInf record);

    int insertSelective(BthPagyChkInf record);

    BthPagyChkInf selectByPrimaryKey(@Param("pagyNo") String pagyNo, @Param("chkDataDt") Date chkDataDt);

    int updateByPrimaryKeySelective(BthPagyChkInf record);

    int updateByPrimaryKey(BthPagyChkInf record);
    /**
     * 根据通道编号和清算日期删除BthPagyChkInf信息
     * @param pagyNo
     * @param settleDate
     * @return
     */
	int deleteBypagyNoAndDate(@Param("pagyNo")String pagyNo, @Param("settleDate")String settleDate);

    List<BthPagyChkInf> selectByPagyNoAndDate(Map<String,Object> mapParam);
}