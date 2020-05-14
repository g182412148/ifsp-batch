package com.scrcu.ebank.ebap.batch.aspects;

import com.ruim.ifsp.utils.IfspUtil;
import com.ruim.ifsp.utils.beans.IfspBeanUtils;
import com.ruim.ifsp.utils.id.IfspId;
import com.scrcu.ebank.ebap.batch.bean.dto.BthBatchJobExecution;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.controller.*;
import com.scrcu.ebank.ebap.batch.dao.BthBatchJobExecutionDao;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.*;


@Component
@Aspect
@Slf4j
public class ControllerAspect {

    private static Set<String> exceptMethods = new HashSet<String>();
    private static Set<Class> exceptClazz = new HashSet<Class>();

    static {
        exceptMethods.add("003.QueryMerInAcc");
        exceptMethods.add("004.QueryMerInAccDtl");
        exceptMethods.add("006.QueryMerInAccPositive");
        exceptMethods.add("qryFileName");

        exceptClazz.add(AccountLiquidationQuery.class);
        exceptClazz.add(MchtPayStatisticesController.class);
        exceptClazz.add(MchtPayStatisticesController.class);
        exceptClazz.add(MchtTxnCountController.class);
        exceptClazz.add(KeepAccountjobController.class);
        exceptClazz.add(MchtAuthController.class);
    }

    @Resource
    private BthBatchJobExecutionDao bthBatchJobExecutionDao;   //批量执行表

    //controller执行时效POINTCUT
    private final String TIME_COUNT_POINT_CUT = "execution(* com.scrcu.ebank.ebap.batch.controller.*.*(..))";

    @Pointcut(TIME_COUNT_POINT_CUT)
    private void timeCountPointCut(){}

    @Value("${provider.timeout}")
    private Long timeout;

    @Around(value = "timeCountPointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable
    {
        //根据proceedingJoinPoint活动被拦截的信息
        Class<?> targetClass = proceedingJoinPoint.getTarget().getClass();
        Method targetMethod = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        Explain explainAnn = targetMethod.getAnnotation(Explain.class);
        SOA soaAnn = targetMethod.getAnnotation(SOA.class);
        String saoName = soaAnn.value();                      //服务名称
        if(exceptMethods.contains(saoName) || exceptClazz.contains(targetClass))
        {
            return  proceedingJoinPoint.proceed();
        }
        String saoNameDesc = saoName;
        if(explainAnn != null)
        {
            saoNameDesc = explainAnn.name();              //服务中\文描述
        }
        String batchNo = IfspId.getUUID(32);         //批次号
        String status = Constans.JOB_EXE_STATUS_EXECUTING;

        Map<String,Object> executionParams = new HashMap<String,Object>();
        //任务开始时间
        long startTime = System.currentTimeMillis();
//        System.out.println(">>>>>>>>>>>>>>>>>>startTime = " + startTime);
        log.info(">>>>>>>>>>>>>>>>>startTime【{}】" ,startTime);
        long endTime;
        //插入批量执行表，初始化状态为执行中
        executionParams.put("jobExeId",batchNo);
        executionParams.put("jobName",saoName);
        executionParams.put("jobDesc",saoNameDesc);
        executionParams.put("ipAddr", InetAddress.getLocalHost().getHostAddress());
        executionParams.put("startTime",new Date());
        executionParams.put("status",Constans.JOB_EXE_STATUS_EXECUTING);            //执行中
        executionParams.put("batchNo",batchNo);

        BthBatchJobExecution jobExecution = (BthBatchJobExecution)IfspBeanUtils.toBean(BthBatchJobExecution.class,executionParams);

        bthBatchJobExecutionDao.insertSelective(jobExecution);

        Object response;
        try {
            response =  proceedingJoinPoint.proceed();
        }
        catch (Exception e)
        {
            endTime = System.currentTimeMillis();
            //更新批量执行表状态为失败
            executionParams.put("status",Constans.JOB_EXE_STATUS_FAIL);
            log.error(">>>>>>>>>>exception occures:");
            //e.printStackTrace();
            log.error(ExceptionUtils.getFullStackTrace(e));  //解决e.printStackTrace()在日志文件中没打印的问题
            String description = "ERROR INFO : "+e.getMessage();

            log.error(description);

            if(description.length()>4000)
            {
                description = description.substring(0,1300);
            }
            executionParams.put("description",description);
            executionParams.put("endTime",new Date());
            bthBatchJobExecutionDao.update("updateJobExeStatusByExecuId",executionParams);

            response = new CommonResponse();
            ((CommonResponse)response).setRespCode(RespConstans.RESP_FAIL.getCode());
            ((CommonResponse)response).setRespMsg(RespConstans.RESP_FAIL.getDesc());

            return response;
        }

        if(!RespConstans.RESP_SUCCESS.getCode().equals(((CommonResponse)response).getRespCode()))
        {
            executionParams.put("status",Constans.JOB_EXE_STATUS_FAIL);         //任务执行失败
            String description = ((CommonResponse)response).getRespMsg();
            log.error(">>>>>>>>>>>>>>>>>error info :/r/n" + description);
            if(description.length()>4000)
            {
                description = description.substring(0,3999);
            }
            executionParams.put("description",description);
            executionParams.put("endTime",new Date());

            bthBatchJobExecutionDao.update("updateJobExeStatusByExecuId",executionParams);

            ((CommonResponse) response).setRespMsg(RespConstans.RESP_FAIL.getDesc());
            return  response;
        }

        //执行成功
        executionParams.put("status",Constans.JOB_EXE_STATUS_SUCCESS);
        executionParams.put("endTime",new Date());
        endTime = System.currentTimeMillis();
        if(endTime - startTime > timeout)
        {
            executionParams.put("status",Constans.JOB_EXE_STATUS_TIMEOUT);   //任务执行成功，但执行时间超时
            executionParams.put("description",Constans.JOB_EXE_DESC_TIMEOUT);
        }
        bthBatchJobExecutionDao.update("updateJobExeStatusByExecuId",executionParams);
        log.info(">>>>>>>>>>>>>>>>>endTime【{}】，耗时【{}】" ,endTime,endTime-startTime);

        return response;
    }
}
