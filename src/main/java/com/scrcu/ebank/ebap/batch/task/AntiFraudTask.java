package com.scrcu.ebank.ebap.batch.task;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAcc;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDao;
import com.scrcu.ebank.ebap.batch.soaclient.AntiFraudSoaService;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author ljy
 * @date 2018-12-25 09:26
 */
@Slf4j
public class AntiFraudTask implements Runnable {

    /**
     * 汇总表实体
     */
    private List<BthMerInAcc> bthMerInAccList;

    /**
     * 反欺诈服务
     */
    private AntiFraudSoaService antiFraudSoaService ;

    /**
     * 商户汇总表数据访问对象
      */
    private BthMerInAccDao bthMerInAccDao;


    /**
     * 线程计数器
     */
    private CountDownLatch cdl;



    public AntiFraudTask(List<BthMerInAcc> bthMerInAccList, CountDownLatch cdl, BthMerInAccDao bthMerInAccDao, AntiFraudSoaService antiFraudSoaService){
        this.bthMerInAccList = bthMerInAccList ;
        this.cdl = cdl;
        this.bthMerInAccDao = bthMerInAccDao ;
        this.antiFraudSoaService = antiFraudSoaService ;
    }



    @Override
    public void run() {

        for (BthMerInAcc bthMerInAcc : bthMerInAccList) {
            SoaParams reqParam = getCommnRequestAntiFraud(bthMerInAcc);
            SoaResults results = antiFraudSoaService.dataVisor(reqParam);

            /*
             * respCode	respMsg
             * 0000	通过
             * 1001	加强认证
             * 1002	拒绝
             * 1006	报文要素缺失
             * 1035	该交易类型不需要上送反欺诈
             * 9999	调用失败
             *
             * 仅处理返回码0000 ,1001 , 1002 , 且仅当1002时控制不让该条记录入账
             *
             */
            if ("1002".equals(results.getRespCode())) {
                log.info("===================>>调用反欺诈成功, 更新入账汇总表<<===========================");
                // 明确拒绝 , 该笔流水不入账
                Map<String,Object> map = new HashMap<>(6);

                map.put("txnSsn",bthMerInAcc.getTxnSsn());
                map.put("antiFraudRespSsn",results.get("respSsn"));
                map.put("antiFraudFinalDecision",results.get("finalDecision"));
                map.put("antiFraudFinalDealType",results.get("finalDealType"));
                map.put("antiFraudData",results.toString());
                // 控制入账的标志  仅当1时拒绝入账
                map.put("antiFraudFinalControl","1");
                // 反欺诈上送时间
                map.put("antiFraudSendTime",IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
                // 最后更新时间
                map.put("updateTime",IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));



                bthMerInAccDao.update("updateByFantiInfo",map);


            } else if ("0000".equals(results.getRespCode()) ||"1001".equals(results.getRespCode()) ){

                log.info("===================>>调用反欺诈成功, 更新入账汇总表<<===========================");

                Map<String,Object> map = new HashMap<>(6);

                map.put("txnSsn",bthMerInAcc.getTxnSsn());
                map.put("antiFraudRespSsn",results.get("respSsn"));
                map.put("antiFraudFinalDecision",results.get("finalDecision"));
                map.put("antiFraudFinalDealType",results.get("finalDealType"));
                map.put("antiFraudData",results.toString());
                // 控制入账的标志  仅当1时拒绝入账
                map.put("antiFraudFinalControl",null);
                // 反欺诈上送时间
                map.put("antiFraudSendTime",IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));
                // 最后更新时间
                map.put("updateTime",IfspDateTime.parseDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYYMMDDHHMMSS));

                bthMerInAccDao.update("updateByFantiInfo",map);

            }else {
                log.error("===================>>调用反欺诈失败<<===========================");

            }

        }


        cdl.countDown();

    }




    /**
     * 上送反欺诈报文
     * @param bthMerInAcc 商户入账汇总实体
     * @return paramMap
     */
    private static SoaParams getCommnRequestAntiFraud(BthMerInAcc bthMerInAcc) {

        SoaParams paramMap = new SoaParams();

        // 传商户号
        paramMap.put("accountLogin", bthMerInAcc.getChlMerId());
        // 传商户号
        paramMap.put("merchantNo", bthMerInAcc.getChlMerId());

        // 入账金额
        paramMap.put("tradeAmount",bthMerInAcc.getInAcctAmt());

        // 交易笔数
        paramMap.put("tradeCnt",bthMerInAcc.getTxnCount());

        // 交易类型
        paramMap.put("visorTxnType","trade_mkcol");

        // 清算时间
        paramMap.put("liqdDate",IfspDateTime.getYYYY_MM_DD());

        return paramMap;

    }


}
