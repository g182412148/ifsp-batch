package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface KeepAccInfoMapper {
    int deleteByPrimaryKey(String coreSsn);

    int insert(KeepAccInfo record);

    int insertSelective(KeepAccInfo record);

    KeepAccInfo selectByPrimaryKey(String coreSsn);

    int updateByPrimaryKeySelective(KeepAccInfo record);

    int updateByPrimaryKeySelectiveState(KeepAccInfo record);

    int updateByPrimaryKey(KeepAccInfo record);

    List<KeepAccInfo> selectByState(String state);

    void updateByState(@Param("coreSsn") String coreSsn, @Param("state") String state,@Param("registerIp") String registerIp);

    List<KeepAccInfo> selectByOrderSsn(String orderSsn);

    List<String> selectByStateIsSync(Map<String,Object> parameter);

    int insertIgnoreExist(KeepAccInfo keepAccInfo);

    int recovery(@Param("settleDate")String settleDate);

    int recoveryDubious(@Param("settleDate")String settleDateDubious);

    int updateByPrimaryKeyAndSt(KeepAccInfo record);

    List<KeepAccInfo> queryByOrderSsnAndTm(@Param("orderSsn")String orderSsn,@Param("orderTm") String orderTm);

    List<KeepAccInfo> queryResvRec(@Param("count") Integer count, @Param("retryCount")Integer retryCount);

    int updateRetryCount(@Param("coreSsn") String coreSsn, @Param("state")String state, @Param("respCode")String respCode,@Param("respMsg") String respMsg);

    List<KeepAccInfo> queryByOrderSsnTmAndVerNoSucc(@Param("orderSsn")String orderSsn,@Param("orderTm") String orderTm,@Param("verNo")String verNo);

    String queryByOrigCoreSsn(@Param("origCoreSsn")String origCoreSsn);

    List<KeepAccInfo> queryByOrderSsnAndTmAndVerNo(@Param("orderSsn")String orderSsn,@Param("orderTm") String orderTm, @Param("verNo") String verNo);

    List<KeepAccInfo> queryByOrderSsnTmAndMaxVerNoSucc(@Param("orderSsn")String orderSsn,@Param("orderTm") String orderTm);

    KeepAccInfo selectByOrderSsnT0(String orderSsn);
}