package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BthUnionFileDet;
import org.apache.ibatis.annotations.Param;

public interface BthUnionFileDetMapper {
    int deleteByPrimaryKey(@Param("traceNum") String traceNum, @Param("proxyInsCode") String proxyInsCode, @Param("sendInsCode") String sendInsCode, @Param("transDate") String transDate);

    int insert(BthUnionFileDet record);

    int insertSelective(BthUnionFileDet record);

    BthUnionFileDet selectByPrimaryKey(@Param("traceNum") String traceNum, @Param("proxyInsCode") String proxyInsCode, @Param("sendInsCode") String sendInsCode, @Param("transDate") String transDate);

    int updateByPrimaryKeySelective(BthUnionFileDet record);

    int updateByPrimaryKey(BthUnionFileDet record);

	int deleteBypagyNoAndDate(@Param("pagyNo")String pagyNo, @Param("settleDate")String settleDate);

	BthUnionFileDet queryUnionFileDetByOrderNo(@Param("proxyInsCode")String proxyInsCode, @Param("sendInsCode")String sendInsCode, @Param("traceNum")String traceNum, @Param("transDate")String transDate);

    void deleteByPagySysNoAndDate(@Param("pagySysNo")String pagySysNo,@Param("settleDate") String settleDate);
}