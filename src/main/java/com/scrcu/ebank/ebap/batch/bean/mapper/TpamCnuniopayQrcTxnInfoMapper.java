package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamCnuniopayQrcTxnInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamCnuniopayQrcTxnInfoVo;

public interface TpamCnuniopayQrcTxnInfoMapper {
    int deleteByPrimaryKey(String pagyPayTxnSsn);

    int insert(TpamCnuniopayQrcTxnInfo record);

    int insertSelective(TpamCnuniopayQrcTxnInfo record);

    TpamCnuniopayQrcTxnInfo selectByPrimaryKey(String pagyPayTxnSsn);

    int updateByPrimaryKeySelective(TpamCnuniopayQrcTxnInfo record);

    int updateByPrimaryKey(TpamCnuniopayQrcTxnInfo record);
    /**
     * 根据清算日期和状态查询TpamCnuniopayQrcTxnInfo
     * @param settleDate
     * @return
     */
	List<TpamCnuniopayQrcTxnInfoVo> selectByDateAndState(String settleDate);
}