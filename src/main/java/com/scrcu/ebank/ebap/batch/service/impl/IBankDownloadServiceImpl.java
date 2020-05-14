package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.message.IfspStringUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.IbankBillOuter;
import com.scrcu.ebank.ebap.batch.bean.request.GetOrderInfoRequest;
import com.scrcu.ebank.ebap.batch.common.dict.RespEnum;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.IbankBillOutDao;
import com.scrcu.ebank.ebap.batch.service.BillsDownloadService;
import com.scrcu.ebank.ebap.batch.soaclient.KeepAccSoaService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ljy
 * @date 2019-05-09
 */
@Service("iBankDownloadSerice")
@Slf4j
public class IBankDownloadServiceImpl implements BillsDownloadService {


    /**
     * 批次插入的数量
     */
    @Value("${ibankBill.batchInsertCount}")
    private Integer batchInsertCount;

    /**
     * 工作线程数量
     */
    @Value("${ibankBill.threadCount}")
    private Integer threadCount;


    /**
     * 线程池
     */
    ExecutorService executor;

    /**
     * 本行对账文件下载目录
     */
    @Value("${debitLocalFileUrl}")
    private String debitLocalFileUrl;

    @Resource
    private KeepAccSoaService keepAccSoaService;

    @Resource
    private IbankBillOutDao ibankBillOutDao;



    @Override
    public CommonResponse billDownload(GetOrderInfoRequest request) {


        CommonResponse response = new CommonResponse() ;


        //获取的订单数据的交易日期
        Date txnDate = getRecoDate(request.getSettleDate());
        //计算对账日期 = 交易日期 + 1
        Date recoDate = new DateTime(txnDate).plusDays(1).toDate();


        //清除对账日下的本行文件数据
        clear(recoDate);

        // 组装核心对账单下载请求报文
        SoaParams params = initReqParams(request);
        // 调用核心接口
        SoaResults result = keepAccSoaService.debitBill(params);
        if (IfspDataVerifyUtil.isBlank(result)||!RespEnum.RESP_SUCCESS.getCode().equals(result.get("respCode"))){
            log.info("本行对账文件下载失败!!!");
            throw new IfspBizException(RespEnum.RESP_FAIL.getCode(),RespEnum.RESP_FAIL.getDesc());
        }

        // 下载文件目录(包含文件名)
        String localFilePath = debitLocalFileUrl+result.getDatas().get("docNm");


        // 检查文件是否已到本地共享目录
        if (!checkIBankBill(localFilePath+".ok")){
            response.setRespCode(RespEnum.RESP_FAIL.getCode());
            response.setRespMsg("本行对账文件下载失败!!!");
            return response;
        }
        log.info("下载文件完成");

        /**
         * 初始化线程池
         */
        initPool();

        try {
            initIBankList(recoDate, localFilePath);
        } finally {
            //销毁线程池
            destoryPool();
        }
        log.info("解析本行文件完成.");

        return response;
    }

    /**
     * 清除对账日下的本行文件数据
     * @param recoDate
     */
    private void clear(Date recoDate) {
        int clearCount = ibankBillOutDao.clear(recoDate);
        log.info("清除本行对账文件数据量:"+clearCount);
    }


    /**
     * 检查.ok文件是否已到本地共享目录
     * @param localFilePathOk
     * @return
     */
    private boolean checkIBankBill(String localFilePathOk) {
        // 尝试等核心文件 3次 , 每次 5分钟 , 等不到就报错
        int c = 3;
        do {
            if (!new File(localFilePathOk).exists()) {
                c--;
                log.info("===================== 本行对账ok文件不存在 ,等待5分钟后重试, 剩余检查次数[" + c + "] ================================");
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    log.error("睡眠异常...");
                }

            } else {
                log.info("=====================本行对账ok文件检查到存在 , 开始解析对账文件================================");
                return true;
            }
        } while (c > 0);

        return false;

    }


    /**
     * 解析文件,组装到List对象
     * @param recoDate
     * @param localFilePath
     */
    private void initIBankList(Date recoDate, String localFilePath) {
        List<IbankBillOuter> ibankBillOuterList  = new ArrayList<>();

        //线程处理结果
        List<Future> futureList = new ArrayList<>();
//        ByteArrayInputStream in = null;
        log.info("------------ 解析业务明细文件(开始)------------------");
        //获取对账单文件流
        try (FileInputStream fis = new FileInputStream(localFilePath);
             InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
             BufferedReader br  = new BufferedReader(isr)){

                /**
                 * 按“|#|”截取,解析入库对账文件
                 */
                log.info("受理通道业务(下载对账单) 逻辑处理 ");
                while (br.ready()) {
                    String tmpLine = br.readLine();
                    if (IfspStringUtil.isBlank(tmpLine)) {
                        continue;
                    }
                    IbankBillOuter rec = new IbankBillOuter();
                    rec.setTxnSsn(tmpLine.split("\\|#\\|",-1)[2]);
                    rec.setRecoDate(recoDate);
                    rec.setTxnState("00");
                    rec.setTxnAmt(new BigDecimal(tmpLine.split("\\|#\\|",-1)[14]).movePointRight(2));
                    // 未对账状态
                    rec.setRecoState("0");
                    // 渠道号
                    rec.setChannelNo(tmpLine.split("\\|#\\|",-1)[0]);
                    // 渠道日期
                    rec.setChannelDate(tmpLine.split("\\|#\\|",-1)[1]);
                    // 渠道流水号
                    rec.setChannelSeq(tmpLine.split("\\|#\\|",-1)[2]);
                    // 交易日期
                    rec.setTxnDate(tmpLine.split("\\|#\\|",-1)[3]);
                    // 柜员流水号
                    rec.setTellerSeq(tmpLine.split("\\|#\\|",-1)[4]);
                    // 交易状态
                    rec.setTxnStatus(tmpLine.split("\\|#\\|",-1)[5]);
                    // 平台日期
                    rec.setPlatformDate(tmpLine.split("\\|#\\|",-1)[6]);
                    // 平台流水号
                    rec.setPlatformSeq(tmpLine.split("\\|#\\|",-1)[7]);
                    // 交易号
                    rec.setTxnCode(tmpLine.split("\\|#\\|",-1)[8]);
                    // 交易机构号
                    rec.setTxnOrg(tmpLine.split("\\|#\\|",-1)[9]);
                    // 交易柜员号
                    rec.setTxnTeller(tmpLine.split("\\|#\\|",-1)[10]);
                    // 币种
                    rec.setTxnCur(tmpLine.split("\\|#\\|",-1)[11]);
                    // 账号
                    rec.setPayAccount(tmpLine.split("\\|#\\|",-1)[12]);
                    // 对方账号
                    rec.setReceiveAccount(tmpLine.split("\\|#\\|",-1)[13]);
                    // 交易金额(元转分)
                    rec.setTxnAmount(new BigDecimal(tmpLine.split("\\|#\\|",-1)[14]).movePointRight(2));

                    ibankBillOuterList.add(rec);

                    if(ibankBillOuterList.size() == batchInsertCount){
                        futureList.add(executor.submit(new IbankHandler(ibankBillOuterList)));
                        //清理集合
                        ibankBillOuterList = new ArrayList<>();
                    }
                }

            } catch (IOException e) {
                log.error("下载对账单失败:", e);
                throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(), "下载对账单失败");
            }


        /*
         */
        if(ibankBillOuterList.size() > 0){
            futureList.add(executor.submit(new IbankHandler(ibankBillOuterList)));
        }

        /*
         * 获取明细处理结果
         */
        for (Future future : futureList) {
            try {
                future.get(10, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.error("子线程处理异常: ", e);
                //取消其他任务
                destoryPool();
                log.warn("其他子任务已取消.");
                //返回结果
                throw new IfspSystemException(SystemConfig.getSysErrorCode(), "子线程处理异常:" + e.getMessage());
            }
        }

    }


    /**
     * 组装本行本行请求参数
     * @param request
     * @return
     */
    private static SoaParams initReqParams(GetOrderInfoRequest request)
    {
        SoaParams params = new SoaParams();
        String timeStamp = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
        // 交易日期
        params.put("chkEntryDt", request.getSettleDate());
        // 对账分类编码
        params.put("chkEntryClsCd", "EBAP001");
        // 文件名
        params.put("docNm", "052" + timeStamp + "RPS001_check.txt");
        return params;
    }



    /**
     * 销毁线程池
     */
    private void destoryPool() {
        log.info("====销毁线程池(start)====");

        if (executor != null) {
            log.info("线程池为null, 无需清理");
            /*
             * 关闭线程池
             */
            try {
                executor.shutdown();
                if(!executor.awaitTermination(10, TimeUnit.SECONDS)){
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                System.out.println("awaitTermination interrupted: " + e);
                executor.shutdownNow();
            }
        }
        log.info("====销毁线程池(end)====");
    }

    /**
     * 初始化线程池
     */
    private void initPool() {
        destoryPool();
        /*
         * 构建
         */
        log.info("====初始化线程池(start)====");
        executor = Executors.newFixedThreadPool(threadCount, new ThreadFactory() {
            AtomicInteger atomic = new AtomicInteger();
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "ibankBillHander_" + this.atomic.getAndIncrement());
            }
        });
        log.info("====初始化线程池(end)====");

    }



    /**
     * 获取对账日期
     * @param dateStr
     * @return
     */
    private Date getRecoDate(String dateStr){
        if(StringUtils.isBlank(dateStr)){
            throw new IfspValidException(IfspValidException.getErrorCode(), "对账日期为空");
        }
        try{
            return IfspDateTime.getDateTime(dateStr, "yyyyMMdd").toDate(); //todo 改成常数
        }catch (Exception e){
            log.error("对账日期格式错误: ", e);
            throw new IfspValidException(IfspValidException.getErrorCode(), "对账日期格式错误");
        }
    }


    /**
     * 工作线程
     */
    class IbankHandler implements Callable<Integer>{

        private List<IbankBillOuter> outerList;

        private IbankHandler(List<IbankBillOuter> outerList){
            this.outerList = outerList;
        }



        @Override
        public Integer call() throws Exception {
            long sTime = System.currentTimeMillis();
            log.info("子线程处理开始时间[{}]ms",sTime);
            if(outerList == null || outerList.isEmpty()){
                log.warn("本行明细为空,无需插入");
                return 0;
            }else {
                int count = ibankBillOutDao.insertBatch(outerList);
                long eTime = System.currentTimeMillis();
                log.info("插入本行账单明细[{}-{}],数目[{}], 耗时[{}]ms", outerList.get(0).getTxnSsn(),
                        outerList.get(outerList.size() - 1).getTxnSsn(), count, (eTime - sTime));
                return count;
            }
        }
    }




}
