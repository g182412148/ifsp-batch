package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamIbankTxnInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamIbankTxnInfoVo;

public interface TpamIbankTxnInfoMapper {
    int deleteByPrimaryKey(String pagyPayTxnSsn);

    int insert(TpamIbankTxnInfo record);

    int insertSelective(TpamIbankTxnInfo record);

    TpamIbankTxnInfo selectByPrimaryKey(String pagyPayTxnSsn);

    int updateByPrimaryKeySelective(TpamIbankTxnInfo record);

    int updateByPrimaryKey(TpamIbankTxnInfo record);
    /**
     * 根据清算日期和状态查询TpamIbankTxnInfo信息
     * @param settleDate
     * @return
     */
	List<TpamIbankTxnInfoVo> selectByDateAndState(String settleDate);
}