package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.BthPagyChkInf;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthPagyChkInfMapper;
import com.scrcu.ebank.ebap.batch.dao.BthPagyChkInfDao;
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
@Repository("bthPagyChkInfDao")
public class BthPagyChkInfDaoImpl extends BaseBatisDao implements BthPagyChkInfDao {
    private Class<BthPagyChkInfMapper> bthPagyChkInfMapper=BthPagyChkInfMapper.class;
	
	@Override
	public int deleteBypagyNoAndDate(String pagyNo, Date settleDate) {
		return super.getSqlSession().getMapper(bthPagyChkInfMapper).deleteByPrimaryKey(pagyNo,settleDate);
	}

	@Override
	public int updateByPrimaryKeySelective(BthPagyChkInf bthPagyChkInf) {
		return super.getSqlSession().getMapper(bthPagyChkInfMapper).updateByPrimaryKeySelective(bthPagyChkInf);
	}

	@Override
	public int insertSelective(BthPagyChkInf bthPagyChkInf) {
		return super.getSqlSession().getMapper(bthPagyChkInfMapper).insertSelective(bthPagyChkInf);
	}

    @Override
    public List<BthPagyChkInf> selectByPagyNoAndDate(Map<String, Object> mapParam) {
        return getSqlSession().getMapper(bthPagyChkInfMapper).selectByPagyNoAndDate(mapParam);
    }

}
