package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.PagyTxnInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.PagyTxnInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.PagyTxnInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
/**
 *名称：<通道流水抽取Impl> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/21 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Repository("pagyTxnInfoDao")
public class PagyTxnInfoDaoImpl extends BaseBatisDao implements PagyTxnInfoDao {
	private final Class<PagyTxnInfoMapper> pagyTxnInfoMapper=PagyTxnInfoMapper.class;
	@Override
	public ArrayList<PagyTxnInfo> selectByDateAndState(String settleDate) {
		return super.getSqlSession().getMapper(pagyTxnInfoMapper).selectByDateAndState(settleDate);
	}
	@Override
	public PagyTxnInfo selectOne(String statement, Map<String, Object> params) {
		return  getSqlSession().selectOne(statement, params);
	}

}
