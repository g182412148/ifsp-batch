package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.KeepAcctInfo;

public interface KeepAcctInfoDao {

	List<KeepAcctInfo> selectByOrderSsn(String orderSsn);

    List<KeepAcctInfo> querryAll();
}
