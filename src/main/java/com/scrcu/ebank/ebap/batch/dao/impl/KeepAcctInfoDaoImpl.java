package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.KeepAcctInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.KeepAcctInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.KeepAcctInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
/**
 *名称：<记账任务> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/7/27 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Repository("KeepAcctInfoDao")
public class KeepAcctInfoDaoImpl extends BaseBatisDao implements KeepAcctInfoDao {
	private final Class<KeepAcctInfoMapper> keepAcctInfoMapper=KeepAcctInfoMapper.class;

	@Override
	public List<KeepAcctInfo> selectByOrderSsn(String orderSsn) {
		return super.getSqlSession().getMapper(keepAcctInfoMapper).selectByOrderSsn(orderSsn);
	}

    @Override
    public List<KeepAcctInfo> querryAll() {
        return getSqlSession().getMapper(keepAcctInfoMapper).querryAll();
    }

}
