package com.scrcu.ebank.ebap.batch.dao;


import com.scrcu.ebank.ebap.batch.bean.dto.BthMchtListTempInfo;

import java.util.List;

public interface BthMchtListTempInfoDao {

	int insert(BthMchtListTempInfo bthMchtListTempInfo);

	int update(BthMchtListTempInfo bthMchtListTempInfo);

	int countByChkDate(String chkDate);


	int truncateTable();

	int pullMchtInfo(String chkDate);

	List<BthMchtListTempInfo> queryByRange(int minIndex, int maxIndex);
}
