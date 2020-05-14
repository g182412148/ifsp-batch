package com.scrcu.ebank.ebap.batch.bean.mapper;

import org.apache.ibatis.annotations.Param;

import com.scrcu.ebank.ebap.batch.bean.dto.BthAliFileDet;

public interface BthAliFileDetMapper {
    int deleteByPrimaryKey(@Param("orderNo")String orderNo,@Param("orderTp")String orderTp);

    int insert(BthAliFileDet record);

    int insertSelective(BthAliFileDet record);

    BthAliFileDet selectByPrimaryKey(@Param("orderNo")String orderNo,@Param("orderTp")String orderTp);

    int updateByPrimaryKeySelective(BthAliFileDet record);

    int updateByPrimaryKey(BthAliFileDet record);
    /**
     * 根据系统编号和清算日期删除记录
     * @param pagyNo
     * @param settleDate
     * @return
     */
	int deleteBypagyNoAndDate(@Param("pagyNo")String pagyNo,@Param("settleDate") String settleDate);
	
	BthAliFileDet queryAliFileDetByOrderNo(String orderNo);

}