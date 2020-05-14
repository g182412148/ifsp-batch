package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamAtAlipayTxnInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamAtAlipayTxnInfoVo;

public interface TpamAtAlipayTxnInfoMapper {
    int deleteByPrimaryKey(String pagyPayTxnSsn);

    int insert(TpamAtAlipayTxnInfo record);

    int insertSelective(TpamAtAlipayTxnInfo record);

    TpamAtAlipayTxnInfo selectByPrimaryKey(String pagyPayTxnSsn);

    int updateByPrimaryKeySelective(TpamAtAlipayTxnInfo record);

    int updateByPrimaryKey(TpamAtAlipayTxnInfo record);
    /**
     * 根据清算日期和状态查询TpamAtAlipayTxnInfo信息
     * @param settleDate
     * @return
     */
	List<TpamAtAlipayTxnInfoVo> selectByDateAndState(String settleDate);
}