package com.scrcu.ebank.ebap.batch.listener;

import com.scrcu.ebank.ebap.batch.task.RevAccTaskExecutor;
import com.scrcu.ebank.ebap.common.msg.IfspFreeMarkerUtils;
import com.scrcu.ebank.ebap.exception.template.IfspMessageCoreUtil;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class BatchListener {


    public void init() throws Exception {
        //加载message信息
        IfspFreeMarkerUtils.ftlIds=IfspMessageCoreUtil.getMessage("ftlIds");
        //初始化加载freemarker模板
        IfspFreeMarkerUtils.initFile();
        // 冲正定时任务
        Thread thread2 = new Thread(new RevAccTaskExecutor());
        thread2.start();
    }

    public void destroy() throws Exception {
        log.info("本行服务停止关闭线程[============BEGIN==============]");
        // 卸载freemarker模板缓存
        IfspFreeMarkerUtils.destory();
        log.info("本行服务停止关闭线程[============END==============]");
    }
}
