package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamAtWechatTxnInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamAtWechatTxnInfoVo;

public interface TpamAtWechatTxnInfoMapper {
    int deleteByPrimaryKey(String pagyPayTxnSsn);

    int insert(TpamAtWechatTxnInfo record);

    int insertSelective(TpamAtWechatTxnInfo record);

    TpamAtWechatTxnInfo selectByPrimaryKey(String pagyPayTxnSsn);

    int updateByPrimaryKeySelective(TpamAtWechatTxnInfo record);

    int updateByPrimaryKey(TpamAtWechatTxnInfo record);
    /**
     * 根据清算日期和状态查询TpamAtWechatTxnInfo信息
     * @param settleDate
     * @return
     */
	List<TpamAtWechatTxnInfoVo> selectByDateAndState(String settleDate);
}