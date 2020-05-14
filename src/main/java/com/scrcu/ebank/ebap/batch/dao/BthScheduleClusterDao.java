package com.scrcu.ebank.ebap.batch.dao;

import java.util.Date;

/**
 * @author ljy
 */
public interface BthScheduleClusterDao {
	
    /**
     * 获取记录数
     * @param taskId
     * @param date
     * @return
     */
    int getTask(String taskId, Date date);

    /**
     * 更新执行状态
     * @param taskId
     * @param serverIp
     * @param executeStat
     * @param date
     */
    void updateExecuteStat(String taskId, String serverIp, String executeStat, Date date);
}
