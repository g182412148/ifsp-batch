package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BthScheduleCluster;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface BthScheduleClusterMapper {
	
    int deleteByPrimaryKey(String taskId);

    int insert(BthScheduleCluster record);

    int insertSelective(BthScheduleCluster record);

    BthScheduleCluster selectByPrimaryKey(String taskId);

    int updateByPrimaryKeySelective(BthScheduleCluster record);

    int updateByPrimaryKey(BthScheduleCluster record);

    int getTask(@Param("taskId") String taskId,@Param("date") Date date);

    void updateExecuteStat(@Param("taskId") String taskId, @Param("serverIp") String serverIp, @Param("executeStat") String executeStat, @Param("date") Date date);
}