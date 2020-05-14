package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.mapper.BthScheduleClusterMapper;
import com.scrcu.ebank.ebap.batch.dao.BthScheduleClusterDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author: ljy
 * @create: 2018-09-11 19:27
 */
@Repository
public class BthScheduleClusterDaoImpl extends BaseBatisDao implements BthScheduleClusterDao {
    private final Class<BthScheduleClusterMapper> bthScheduleClusterMapper = BthScheduleClusterMapper.class;

    @Override
    public int getTask(String taskId, Date date) {
        return getSqlSession().getMapper(bthScheduleClusterMapper).getTask(taskId,date);
    }

    @Override
    public void updateExecuteStat(String taskId, String serverIp, String executeStat, Date date) {
        getSqlSession().getMapper(bthScheduleClusterMapper).updateExecuteStat(taskId,serverIp,executeStat,date);
    }
}
