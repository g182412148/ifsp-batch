package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerDayAmtSum;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthMerDayAmtSumMapper;
import com.scrcu.ebank.ebap.batch.dao.BthMerDayAmtSumDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author: ljy
 * @create: 2018-09-09 16:08
 */
@Repository
public class BthMerDayAmtSumDaoImpl extends BaseBatisDao implements BthMerDayAmtSumDao {

    private final Class<BthMerDayAmtSumMapper>  bthMerDayAmtSumMapper= BthMerDayAmtSumMapper.class;

    @Override
    public List<BthMerDayAmtSum> selectByTmAmt(Map<String, Object> map) {
        return getSqlSession().getMapper(bthMerDayAmtSumMapper).selectByTmAmt(map);
    }

    @Override
    public int insert(BthMerDayAmtSum value) {
        return getSqlSession().getMapper(bthMerDayAmtSumMapper).insert(value);
    }
}
