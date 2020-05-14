/*
*===============================================================================================
* Copyright (C), 2018-2018
* Package: com.ruim.ifsp.paypagydatabase.bean.mapper
* FileName: TpamWechatTxnInfoMapper.java
* Author: zhaodengke
* Date: 2018-05-14 15:51:59
*===============================================================================================
*/
package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamAtWechatTxnInfoVo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamWechatDcTxnInfo;

public interface TpamWechatDcTxnInfoMapper {
    int deleteByPrimaryKey(String pagyPayTxnSsn);

    int insert(TpamWechatDcTxnInfo record);

    int insertSelective(TpamWechatDcTxnInfo record);

    TpamWechatDcTxnInfo selectByPrimaryKey(String pagyPayTxnSsn);

    int updateByPrimaryKeySelective(TpamWechatDcTxnInfo record);

    int updateByPrimaryKey(TpamWechatDcTxnInfo record);
    /**
     * 根据清算日期和状态查询TpamAtWechatTxnInfo信息
     * @param settleDate
     * @return
     */
	List<TpamAtWechatTxnInfoVo> selectByDateAndState(String settleDate);
}