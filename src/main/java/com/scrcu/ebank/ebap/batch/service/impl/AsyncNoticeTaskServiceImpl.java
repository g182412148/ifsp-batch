package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DubboServiceUtil;
import com.scrcu.ebank.ebap.batch.dao.KeepAccInfoDao;
import com.scrcu.ebank.ebap.batch.service.AsyncNoticeTaskService;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import com.scrcu.ebank.ebap.framework.utils.IfspSpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author: ljy
 * @create: 2018-09-04 22:57
 */
@Slf4j
@Service
public class AsyncNoticeTaskServiceImpl implements AsyncNoticeTaskService {

    @Resource
    private KeepAccInfoDao keepAccInfoDao;

    @Override
    public void asyncNotice() {

        // 筛选出全部成功的且是异步记账的记录来通知
        Map<String , Object> map = new HashMap<>(2);
        map.put("state",Constans.KEEP_ACCOUNT_STAT_SUCCESS);
        map.put("noticeStat",Constans.ASYNC_NOTICE_PRE);
        map.put("isSync",Constans.KEEP_ACCT_FLAG_ASYNC);
        List<KeepAccInfo> list = keepAccInfoDao.selectList("keep_selectGroupByOrderSsn", map);

        Iterator<KeepAccInfo> iterator = list.iterator();
        while (iterator.hasNext()){
            KeepAccInfo next = iterator.next();
            // 事务控制 : 通知记账结果
            AsyncNoticeTaskService asyncNoticeTask = (AsyncNoticeTaskService)IfspSpringContextUtils.getInstance().getBean("asyncNoticeTaskServiceImpl");
            asyncNoticeTask.lockByOrderSsn(next.getOrderSsn());

        }
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    @Override
    public void lockByOrderSsn(String orderSsn){
        Map<String , Object> map = new HashMap<>(2);
        map.put("orderSsn",orderSsn);
        map.put("isSync",Constans.KEEP_ACCT_FLAG_ASYNC);
        // 查询锁表
        List<KeepAccInfo> list = keepAccInfoDao.selectList("keep_selectByOrderSsn", map);

        // 通知一次即可
        KeepAccInfo keepAccInfo = list.get(0);
        // 已经通知过 或者通知地址为空 则不处理
        if (Constans.ASYNC_NOTICE_SUCC.equals(keepAccInfo.getNoticeStat())|| IfspDataVerifyUtil.isBlank(keepAccInfo.getAsyncLoc())){
            return;
        }else {
            // 调用通知接口
            Map<String , Object> paramMap = new HashMap<>(2);
            paramMap.put("orderSsn",orderSsn);
            paramMap.put("keepAcctStatus",Constans.KEEP_ACCOUNT_STAT_SUCCESS);
            Map respMap = null;
            try {
                respMap = DubboServiceUtil.invokeDubboService(paramMap, keepAccInfo.getAsyncLoc().substring(8));
            } catch (Exception e) {
                log.error("订单号[{}]异步通知记账结果失败 ,通知地址:"+keepAccInfo.getAsyncLoc(),orderSsn);
                return;
            }
            log.info("~~~~~~~~~~~~~~~返回码: ["+ respMap.get("respCode")+"}~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            log.info("~~~~~~~~~~~~~~~返回信息: ["+ respMap.get("respMsg")+"}~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

            if (RespConstans.RESP_SUCCESS.getCode().equals(respMap.get("respCode"))){
                log.info("=========================>> 通知记账结果成功, 更新记账表 <<================================");
                // 更新状态
                Map<String , Object> map2 = new HashMap<>(3);
                map2.put("orderSsn",orderSsn);
                map2.put("noticeStat",Constans.ASYNC_NOTICE_SUCC);
                map2.put("isSync",Constans.KEEP_ACCT_FLAG_ASYNC);
                keepAccInfoDao.update("keep_updateByOrderSsn",map2);
            }else {
                log.info("=========================>> 通知记账结果失败 <<================================");
            }

        }

    }



}
