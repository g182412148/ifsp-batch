package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.BthBatchAccountFile;
import com.scrcu.ebank.ebap.batch.bean.dto.BthEnterAccountResult;

import java.util.List;
import java.util.Map;

public interface BthEnterAccountResultDao
{

	public int insertBatch(List<BthEnterAccountResult> records);

	public void clear();
}
