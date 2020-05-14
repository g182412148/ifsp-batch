package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.BthPagyFileSum;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthPagyFileSumMapper;
import com.scrcu.ebank.ebap.batch.dao.BthPagyFileSumDao;
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
@Repository("bthPagyFileSumDao")
public class BthPagyFileSumDaoImpl extends BaseBatisDao implements BthPagyFileSumDao {
	private final Class<BthPagyFileSumMapper> BthPagyFileSumMapper=BthPagyFileSumMapper.class;
	
	@Override
	public int deleteBypagyNoAndDate(String pagyNo, String settleDate) {
		return super.getSqlSession().getMapper(BthPagyFileSumMapper).deleteByPrimaryKey(pagyNo,settleDate);
	}

	@Override
	public int insertSelectiveList(List<BthPagyFileSum> bthPagyFileSumList) {
		for (BthPagyFileSum bthPagyFileSum : bthPagyFileSumList) {
			getSqlSession().getMapper(BthPagyFileSumMapper).insertSelective(bthPagyFileSum);
		}
		return 0;
	}

    @Override
    public void deleteByPagySysNoAndDate(String pagySysNo, String settleDate) {
        getSqlSession().getMapper(BthPagyFileSumMapper).deleteByPagySysNoAndDate(pagySysNo ,settleDate);
    }

}
