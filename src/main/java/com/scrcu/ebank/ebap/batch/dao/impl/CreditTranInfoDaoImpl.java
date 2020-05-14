package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.CreditTranInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.CreditTranInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.CreditTranInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
/**
 *名称：<通道流水抽取Impl> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Repository("CreditTranInfoDao")
public class CreditTranInfoDaoImpl extends BaseBatisDao implements CreditTranInfoDao {
	private final Class<CreditTranInfoMapper> creditTranInfoMapper=CreditTranInfoMapper.class;
	
	@Override
	public int deleteByDate(String settleDate) {
		return super.getSqlSession().getMapper(creditTranInfoMapper).deleteByDate(settleDate);
	}

	@Override
	public int insertSelectiveList(List<CreditTranInfo> creditTranInfoList) {
		for (CreditTranInfo creditTranInfo : creditTranInfoList) {
			getSqlSession().getMapper(creditTranInfoMapper).insertSelective(creditTranInfo);
		}
		return 0;
	}

}
