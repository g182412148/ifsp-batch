package com.scrcu.ebank.ebap.batch.service.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.BackupTableConfig;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.dao.CleanDataDao;
import com.scrcu.ebank.ebap.batch.service.CleanDataService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CleanDataServiceImpl implements CleanDataService {

    @Resource
    private CleanDataDao cleanDataDao;

    @Override
    public CommonResponse cleanData() {
        CommonResponse commonResponse = new CommonResponse();
        // 查询参数
        Map map = new HashMap();

        // 获取所有要进行增量清理的表
        List<BackupTableConfig> configList = cleanDataDao.queryConfigList(map);

        try {
            if (!configList.isEmpty() && configList.size() > 0) {

                for (BackupTableConfig config : configList) {
                    long beginTime = System.currentTimeMillis();
                    log.info("开始执行清理程序-->"+config.getTableName());

                    HashMap tableMap = new HashMap();
                    tableMap.put("tableName",config.getTableName());
                    cleanDataDao.cleanData(tableMap);
                    long endTime = System.currentTimeMillis();
                    log.info("清理程序执行完毕-->"+config.getTableName()+"，耗时："+(endTime-beginTime)+"ms");
                }

            }else{
                log.info("没有需要清理的表！！！");
            }

        }catch(Exception e){
            e.printStackTrace();
            commonResponse.setRespCode(RespConstans.RESP_FAIL.getCode());
            commonResponse.setRespMsg(RespConstans.RESP_FAIL.getDesc());
        }

        return commonResponse;
    }

}
