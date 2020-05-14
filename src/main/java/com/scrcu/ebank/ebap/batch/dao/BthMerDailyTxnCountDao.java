package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerDailyTxnCount;
import com.scrcu.ebank.ebap.batch.bean.dto.WeekDay;

/**
 * @author ljy
 */
public interface BthMerDailyTxnCountDao {

    void insert(BthMerDailyTxnCount bthMerDailyTxnCount);

    List<BthMerDailyTxnCount> selectByTmAmt(Map<String,Object> map);

	List<WeekDay> queryWeekCountAll(String mchtId, String startDate, String endDate, String wookDate);
}
