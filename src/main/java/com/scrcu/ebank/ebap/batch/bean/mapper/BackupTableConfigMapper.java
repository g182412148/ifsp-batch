package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BackupTableConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BackupTableConfigMapper {
    int deleteByPrimaryKey(@Param("tableName") String tableName, @Param("origTableOwner") String origTableOwner);

    int insert(BackupTableConfig record);

    int insertSelective(BackupTableConfig record);

    List<BackupTableConfig> selectByPrimaryKey(Map<String, Object> map);

    int updateByPrimaryKeySelective(BackupTableConfig record);

    int updateByPrimaryKey(BackupTableConfig record);

    void procMain(Map map);
}