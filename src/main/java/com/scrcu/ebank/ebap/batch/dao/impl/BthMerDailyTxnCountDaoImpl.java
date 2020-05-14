package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerDailyTxnCount;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerTxnCountInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.WeekDay;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthMerDailyTxnCountMapper;
import com.scrcu.ebank.ebap.batch.dao.BthMerDailyTxnCountDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author: ljy
 * @create: 2018-09-07 14:37
 */
@Repository
public class BthMerDailyTxnCountDaoImpl extends BaseBatisDao implements BthMerDailyTxnCountDao  {

    private final Class<BthMerDailyTxnCountMapper> bthMerDailyTxnCountMapper = BthMerDailyTxnCountMapper.class;

    @Override
    public void insert(BthMerDailyTxnCount bthMerDailyTxnCount) {
        getSqlSession().getMapper(bthMerDailyTxnCountMapper).insert(bthMerDailyTxnCount);
    }

    @Override
    public List<BthMerDailyTxnCount> selectByTmAmt(Map<String, Object> map) {
        return getSqlSession().getMapper(bthMerDailyTxnCountMapper).selectByTmAmt(map);
    }

	@Override
	public List<WeekDay> queryWeekCountAll(String mchtId, String startDate, String endDate,
			String wookDate) {
		return getSqlSession().getMapper(bthMerDailyTxnCountMapper).queryWeekCountAll(mchtId,startDate,endDate,wookDate);
	}
}
