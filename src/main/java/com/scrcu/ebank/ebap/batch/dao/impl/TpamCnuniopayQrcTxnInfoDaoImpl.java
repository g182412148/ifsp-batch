package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamCnuniopayQrcTxnInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.TpamUpacpTxnInfoVo;
import com.scrcu.ebank.ebap.batch.bean.mapper.TpamUpacpTxnInfoMapper;
import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamCnuniopayQrcTxnInfoVo;
import com.scrcu.ebank.ebap.batch.bean.mapper.TpamCnuniopayQrcTxnInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.TpamCnuniopayQrcTxnInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
/**
 *名称：<银联通道流水抽取Impl> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Repository("tpamCnuniopayQrcTxnInfoDao")
public class TpamCnuniopayQrcTxnInfoDaoImpl extends BaseBatisDao implements TpamCnuniopayQrcTxnInfoDao {
	private final Class<TpamCnuniopayQrcTxnInfoMapper> tpamCnuniopayQrcTxnInfoMapper = TpamCnuniopayQrcTxnInfoMapper.class;

	private final Class<TpamUpacpTxnInfoMapper> tpamUpacpTxnInfoMapperClass = TpamUpacpTxnInfoMapper.class;
	@Override
	public List<TpamCnuniopayQrcTxnInfoVo> selectByDateAndState(String settleDate) {
		return super.getSqlSession().getMapper(tpamCnuniopayQrcTxnInfoMapper).selectByDateAndState(settleDate);
	}

    @Override
    public List<TpamUpacpTxnInfoVo> scanTpamUpacpTxnInfo(String settleDate) {
        return getSqlSession().getMapper(tpamUpacpTxnInfoMapperClass).scanTpamUpacpTxnInfo(settleDate);
    }

	@Override
	public TpamCnuniopayQrcTxnInfo selectByPrimaryKey(String pagyPayTxnSsn) {
		return getSqlSession().getMapper(tpamCnuniopayQrcTxnInfoMapper).selectByPrimaryKey(pagyPayTxnSsn);
	}

}
