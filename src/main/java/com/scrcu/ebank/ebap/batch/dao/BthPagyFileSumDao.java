package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.BthPagyFileSum;

public interface BthPagyFileSumDao {

	int deleteBypagyNoAndDate(String pagyNo, String settleDate);

	int insertSelectiveList(List<BthPagyFileSum> bthPagyFileSumList);

    void deleteByPagySysNoAndDate(String pagySysNo, String settleDate);
}
