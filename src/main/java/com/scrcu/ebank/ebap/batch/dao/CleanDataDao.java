package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.BackupTableConfig;

import java.util.List;
import java.util.Map;

public interface CleanDataDao {
    void cleanData(Map map);
    List<BackupTableConfig> queryConfigList(Map<String, Object> map);
    void close();
}
