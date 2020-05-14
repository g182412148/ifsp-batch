package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccInfo;


public interface KeepAccInfoDao {

	int insert(KeepAccInfo keepAccInfo);

	KeepAccInfo selectByPrimaryKey(String string);

	int update(KeepAccInfo keepAccInfoVo);

    int updateByPrimaryKeySelective(KeepAccInfo keepAccInfoVo);

    int updateByPrimaryKeySelectiveState(KeepAccInfo keepAccInfoVo);

    List<KeepAccInfo> selectByState(String state);

	List<KeepAccInfo> selectList(String string, Map<String, Object> params);
	
	Integer count(String statement, Map<String, Object> parameter);

    void updateByState(String coreSsn, String state, String registerIp);

    int update(String statement,Map<String,Object> parameter);

    List<KeepAccInfo> selectByOrderSsn(String orderSsn);

    List<String> selectByStateIsSync(Map<String, Object> parameter);

    int insertIgnoreExist(KeepAccInfo keepAccInfo);

    int recovery(String settleDate);

    int recoveryDubious(String settleDateDubious);

    int updateByPrimaryKeyAndSt(KeepAccInfo keepAccInfoVo);

    List<KeepAccInfo> queryByOrderSsnAndTm(String orderSsn, String orderTm);

    List<KeepAccInfo> queryResvRec(Integer count, Integer retryCount);

    int updateRetryCount(String pagyPayTxnSsn, String state, String respCode, String respMsg);

    List<KeepAccInfo> queryByOrderSsnTmAndVerNoSucc(String orderSsn, String orderTm, String verNo);

    String queryByOrigCoreSsn(String origCoreSsn);

    List<KeepAccInfo> queryByOrderSsnAndTmAndVerNo(String orderSsn, String orderTm, String verNo);

    List<KeepAccInfo> queryByOrderSsnTmAndMaxVerNoSucc(String orderSsn, String orderTm);

    KeepAccInfo selectByOrderSsnT0(String string);
}
