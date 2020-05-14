package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.BackupTableConfig;
import com.scrcu.ebank.ebap.batch.bean.mapper.BackupTableConfigMapper;
import com.scrcu.ebank.ebap.batch.dao.CleanDataDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class CleanDataDaoImpl extends BaseBatisDao implements CleanDataDao {
    private final Class<BackupTableConfigMapper> mapper = BackupTableConfigMapper.class;
    @Override
    public void cleanData(Map map) {
        getSqlSession().getMapper(mapper).procMain(map);
    }

    @Override
    public List<BackupTableConfig> queryConfigList(Map<String, Object> map) {
        return getSqlSession().getMapper(mapper).selectByPrimaryKey(map);
    }

    @Override
    public void close() {
        try {
            getSqlSession().close();
            getSqlSession().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
