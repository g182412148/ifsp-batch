package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.UnionBillOuter;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.dict.RecoStatusDict;
import com.scrcu.ebank.ebap.batch.common.dict.RecoTxnTypeDict;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.UnionBillOuterDao;
import com.scrcu.ebank.ebap.batch.service.UnionChkHandleService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @ClassName UnionChkHandleServiceImpl
 * @Description 银联对账文件处理(冲正/冲正撤销/被冲正交易)
 * @Author NiklausZhu
 * @Date 2019/12/3 10:55
 **/
@Service
@Slf4j
public class UnionChkHandleServiceImpl implements UnionChkHandleService {

    @Resource
    private UnionBillOuterDao unionBillOuterDao;

    @Override
    public CommonResponse unionChkHandle(BatchRequest request) throws Exception {
        long startTime=System.currentTimeMillis();
        Date recoDate = DateUtil.getAfterDate(request.getSettleDate(),1);
        log.info("对账日期: " + DateUtil.toDateString(recoDate));
        CommonResponse response = new CommonResponse();
        List<UnionBillOuter> billList = unionBillOuterDao.queryBill(recoDate, RecoTxnTypeDict.REVOKE.getCode());
        log.info("==================查询银联对账文件中存在的冲正交易共有【{}】条,耗时【{}】",billList.size(),System.currentTimeMillis()-startTime);
        if(IfspDataVerifyUtil.isNotEmptyList(billList)){
            for(UnionBillOuter bill:billList){
                //1、查询该笔冲正交易的原交易
                UnionBillOuter unionBillOuter = unionBillOuterDao.queryOrigBill(recoDate,bill.getOrgTraceNum(),bill.getOrgTransDate());
                log.info("==================查询流水号TXN_SSN为【{}】的冲正交易的原交易,耗时【{}】",bill.getTxnSsn(),System.currentTimeMillis()-startTime);
                if(IfspDataVerifyUtil.isNotBlank(unionBillOuter)){
                    //2、更新该笔被冲正交易对账状态为无需对账
                    /**
                     * Note:
                     * 2.1、若银联将这笔交易通知到我们，则这笔交易在我们系统中会以本地单边记入差错
                     * 2.2、若银联未将这笔交易通知到我们，则这笔交易只会保存在银联对账文件和银联对账文件明细表中(可能大表清理时会清理掉)
                     * 2.3、无论银联通知与否，该笔交易将不再进入银联补单程序行列
                     */
                    log.info("==================查询流水号TXN_SSN为【{}】的被冲正交易",unionBillOuter.getTxnSsn());
                    unionBillOuter.setRecoState(RecoStatusDict.SKIP.getCode());//该笔原交易对账状态为无需对账
                    unionBillOuter.setUpdDate(new Date());
                    unionBillOuterDao.updateById(unionBillOuter);
                    log.info("==================更新流水号TXN_SSN为【{}】的被冲正交易,耗时【{}】",unionBillOuter.getTxnSsn(),System.currentTimeMillis()-startTime);
                }else{
                    log.error("==================查询流水号TXN_SSN为【{}】的冲正交易的无原交易,耗时【{}】",bill.getTxnSsn(),System.currentTimeMillis()-startTime);
                    throw new Exception("查询流水号TXN_SSN为【"+bill.getTxnSsn()+"】无原交易");
                }

            }
        }
        log.info("==================银联通道对账明细处理，对账日期【{}】，总耗时【{}】",DateUtil.toDateString(recoDate),System.currentTimeMillis()-startTime);
        return response;
    }
}
