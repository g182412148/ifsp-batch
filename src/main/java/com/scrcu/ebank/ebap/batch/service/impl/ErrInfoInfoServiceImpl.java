package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.id.IfspId;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.ErroProcStateDict;
import com.scrcu.ebank.ebap.batch.common.dict.RecoTxnTypeDict;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.service.ErrInfoService;
import com.scrcu.ebank.ebap.batch.service.PreClearingService;
import com.scrcu.ebank.ebap.batch.utils.FileUtil;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description:
 * @CopyRightInformation : 数云
 * @Prject: 数云PMS
 * @author: sun_b
 * @date: 2020/5/8
 */
@Service
@Slf4j
public class ErrInfoInfoServiceImpl implements ErrInfoService {
    @Resource
    private ErrInfoDao errInfoDao;
    @Resource
    private BthMerInAccDtlDao bthMerInAccDtlDao;
    @Resource
    private MchtContInfoDao mchtContInfoDao;
    @Resource
    private PaySubOrderInfoDao subOrderInfoDao;
    @Resource
    private PayOrderInfoDao orderInfoDao;
    @Resource
    private MchtOrgRelDao mchtOrgRelDao;
    @Resource
    private PreClearingService preClearingService;

    @Value("${errFileLocalPath}")
    String errFileLocalPath;//本地生成差错文件地址
    @Value("${UPLOAD_FILE_URL}")
    private String UPLOAD_FILE_URL;//文件服务器地址

    /**文件传输平台 目录*/
    private static String errfileBizTag = "/home/dfsrun/print/DataFile/NXPS";

    @Override
    public CommonResponse getYestodayErrInfo() {
        CommonResponse response = new CommonResponse();

        //前一天
//        Date recoDate = DateUtil.parse(DateUtil.format(IfspDateTime.nextDateD(-1), "yyyyMMdd"),"yyyyMMdd");
        Date recoDate = DateUtil.parse("20191127","yyyyMMdd");

        List<ErrInfo> list = new ArrayList<ErrInfo>();


        list.addAll(errInfoDao.getAliErrInfo(recoDate));
        list.addAll(errInfoDao.getUnionErrInfo(recoDate));
        list.addAll(errInfoDao.getWxErrInfo(recoDate));
        if(IfspDataVerifyUtil.isEmptyList(list)){
            log.info("无差错数据需要处理");
            return response;
        }
        List<ErrInfo> errInfos = new ArrayList<ErrInfo>();

        for (ErrInfo info:list){
            try {
                //消费
                if (RecoTxnTypeDict.PAY.getCode().equals(info.getOrderTranType()) ) {
                    //本地成功  三方失败
                    if("00".equals(info.getLocalTxnState()) && !"00".equals(info.getOuterTxnState())) {
                        this.payLocalErrInfoList(info, errInfos);
                    }
                }
                //消费
                if (RecoTxnTypeDict.REFUND.getCode().equals(info.getOrderTranType())) {
                    // 本地失败  三方成功
                    if(!"00".equals(info.getLocalTxnState()) && "00".equals(info.getOuterTxnState())) {
                        this.refundOuterErrInfoList(info, errInfos);
                    }
                }
            }catch (Exception e){
                log.error("差错数据抽取 txnSsn["+info.getTxnSsn()+"] e.getMessage()");
            }
        }
        //插入差错详情表
        if(IfspDataVerifyUtil.isEmptyList(errInfos)){
            log.info("无差错数据需要处理");
            return response;
        }
        errInfoDao.insertErrInfoBatch(errInfos);

        return response;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
    public CommonResponse pushErrFile() {
        CommonResponse response = new CommonResponse();
        List<ErrInfo> list = errInfoDao.getErrFileInfo();

        List<String> msgList = this.buildErrFileMsg(list);
        if(IfspDataVerifyUtil.isEmptyList(msgList)){
            log.info("没有需要推送的差错信息");
            return response;
        }

        //生成差错文件
        String fileName = "TuneDtl_5_"+IfspDateTime.getYYYYMMDD()+"_SM.txt";
        FileUtil.createFile(msgList,errFileLocalPath,fileName,"UTF-8");

        //生成.ok文件
        FileUtil.createFile(new ArrayList<String>(),errFileLocalPath,fileName+".ok","UTF-8");
        //修改差错详情表差错状态为已推送
        errInfoDao.updateErrInfoBatch(list);

        //推送差错文件
        // TO DO errFilePath 配置
//        FileUploadResult result = FileLoadUtils.errFileUpload(UPLOAD_FILE_URL,errFileLocalPath+fileName,errfileBizTag);

        return response;
    }

    @Override
    public CommonResponse errAcc() {
        CommonResponse response = new CommonResponse();
        //获取需要记账的数据，写入记账表  记账状态为  err未勾连/非err文件  未记账/记账失败
        List<ErrInfo> list = new ArrayList<ErrInfo>();
        if(IfspDataVerifyUtil.isEmptyList(list)){
            log.info("没有差错数据需要记账");
            return response;
        }
        List<ErrMerInAcc> accList = new ArrayList<ErrMerInAcc>();
        String settlDate = IfspDateTime.getYYYYMMDD();
        for(ErrInfo info:list){

            ErrMerInAcc acc = new ErrMerInAcc();
            acc.setDateStlm(settlDate);
            acc.setTxnSsn(info.getId());
            if(!"99".equals(info.getPagyType()) //非err文件数据 且 差错状态=未推送
                    && ErroProcStateDict.PUSH.getCode().equals(info.getErrState())){

                acc.setInAcctAmt(fenToYuan2(info.getPayAmt()));
                acc.setOutAcctNo("99990151391020600005");//差错应收（省联社）
                acc.setOutAcctNoOrg("9999");

                acc.setInAcctNoOrg("9999");
                switch (info.getPagyType()){
                    case "00"://银联 应收款项
                        acc.setInAcctNo("99990154615000600000");
                        break;
                    case "10"://微信 应收款项
                        acc.setInAcctNo("99990151391020800003");
                        break;
                    case "20"://支付宝 应收款项
                        acc.setInAcctNo("99990151391020800004");
                        break;
                    default:
                        break;
                }

            }else if(false){////非err文件数据 且 差错状态=已推送 且 差错中心返回结果不为空

            }else if(false){//err未勾连

            }else{//未勾连

            }
            accList.add(acc);
        }
        //批量插入记账表
        return response;
    }


    /**
     * 组装查错文件信息
     * @param list
     * @return
     */
    private List<String> buildErrFileMsg(List<ErrInfo> list){
        List<String> msgList = new ArrayList<String>();
        if(IfspDataVerifyUtil.isEmptyList(list)){
            return msgList;
        }
        StringBuffer buffer = new StringBuffer();
        String amt = "";//5本金
        String fee = "";//7手续费
        String tlr_seqno = "";//8?行内流水（柜员流水或者TT流水）tlr_seqno、主键
        String ta_acc = "";//13交易账号
        String ta_amt = "";// 14-他行交易金额 ta_amt、
        String ta_fee = "";//15-机构收入手续费(分润手续费)
        String account_amt= "";//25记账金额
        String bankCouponAmt = "";//27推广费-营销金额
        for(ErrInfo info:list){
            info.setErrState(ErroProcStateDict.PUSH.getCode());//修改差错状态为已推送
            account_amt = this.fenToYuan2(info.getCoreAccAmt());
            //消费 本方多账
            if("10".equals(info.getErrChlnNo()) && "61".equals(info.getErrTranType())
                    && "2".equals(info.getChkRst())){
                //推广费 = 营销金额
                bankCouponAmt = this.fenToYuan2(info.getBankCouponAmt());
                if("1".equals(info.getErrType())){//延时
                    // 本金 = 支付金额+活动抵扣金额
                    amt = this.fenToYuan2(info.getPayAmt().add(amtTransfer(info.getBankCouponAmt())));
                }else {//实时 本金 = 支付金额+活动抵扣金额-手续费
                    amt = this.fenToYuan2(info.getPayAmt().add(amtTransfer(info.getBankCouponAmt()))
                            .subtract(amtTransfer(info.getInFeeAmt())));
                    ta_acc = info.getLiqAcctNo();
                }
            }
            //退货 本方少账
            else if("10".equals(info.getErrChlnNo()) && "63".equals(info.getErrTranType())
                    && "1".equals(info.getChkRst())){ //退款金额 = 支付金额
                if(IfspDataVerifyUtil.isEmpty(info.getErrType())){//实时
                    amt = fenToYuan2(info.getPayAmt());
                }
            }
            //未勾连
            else if(false){

            }
            buffer.substring(0,buffer.length());
            buffer.append(IfspDateTime.getYYYYMMDD(info.getOrderTm())).append("|")
                    .append(info.getErrChlnNo()).append("|").append(info.getTxnSsn()).append("|")//TxnSsn 需确认
                    .append(info.getErrTranType()).append("|").append(info.getErrAcctNo()).append("|")
                    .append(amt).append("|").append(fee).append("|").append(tlr_seqno).append("|")
                    .append(info.getOpOrgId()).append("|").append(info.getMchtOrgId()).append("|")
                    .append((new SimpleDateFormat("MMDDhhmmss")).format(info.getOrderTm())).append("|").
                    append(info.getChkRst()).append("|").append(ta_acc).append("|").append(ta_amt).append("|")
                    .append(ta_fee).append("|")
                    //字段16-17 传空
                    .append("||").append(info.getSettlAcctType()).append("|").append(info.getErrType()).append("|").append("|")//字段20传空
                    .append(info.getFeeredFlag()).append("|").append("||")//字段22-23传空
                    .append(info.getCoreAccSsn()).append("|").append(account_amt).append("|")
                    .append(IfspDateTime.getYYYYMMDD(info.getCoreAccTm())).append("|").append(bankCouponAmt).append("|");

            msgList.add(buffer.toString());
            buffer.delete(0,buffer.length());
            amt = "";
            fee = "";
            tlr_seqno = "";
            ta_acc = "";
            ta_amt = "";
            ta_fee = "";
            account_amt= "";
            bankCouponAmt = "";
        }
        return msgList;
    }


    /**
     * 消费 本地成功  三方失败
     * @param info
     * @return
     */
    private void payLocalErrInfoList(ErrInfo info,List<ErrInfo> list) throws Exception {

        //订单信息 支付金额 银行营销金额
        PayOrderInfo orderInfo = orderInfoDao.selectByPagyTxnSsn(info.getTxnSsn());

        String mchtId = "";
        if(null != orderInfo){
            BigDecimal bankCouponAmt = IfspDataVerifyUtil.isEmpty(orderInfo.getBankCouponAmt())?BigDecimal.ZERO:new BigDecimal(orderInfo.getBankCouponAmt());
            info.setBankCouponAmt(bankCouponAmt);//银行营销金额
            info.setPayAmt(new BigDecimal(orderInfo.getPayAmt()));//支付金额
            //子订单
            List<PaySubOrderInfo> subOrderList = subOrderInfoDao.selectByOrderNo(orderInfo.getOrderSsn());
            //计算手续费
            if(IfspDataVerifyUtil.isEmptyList(subOrderList)){
                mchtId = orderInfo.getMchtId();
                info.setInFeeAmt(preClearingService.calMerFee4$Order(orderInfo));
            }else {
                info.setInFeeAmt(BigDecimal.ZERO);
                for (PaySubOrderInfo subOrderInfo: subOrderList){
                    preClearingService.calcMerFee4SubOrder(orderInfo,subOrderInfo);
                    if(IfspDataVerifyUtil.isNotEmpty(subOrderInfo.getMerFee())) {
                        info.setInFeeAmt(info.getInFeeAmt().add(new BigDecimal(subOrderInfo.getMerFee())));
                    }
                }
            }
            info.setSettlAmt(info.getPayAmt().add(info.getBankCouponAmt()).subtract(info.getInFeeAmt()));
        }

        //是否结算
        info.setErrType(this.hasStled(orderInfo.getOrderSsn()));
        //商户相关信息
        this.getMchtInfo(info,mchtId);
        info.payInit();

        list.add(info);
    }

    /**
     * 消费 本地失败  三方成功
     * @param info
     * @return
     */
    private void refundOuterErrInfoList(ErrInfo info,List<ErrInfo> list) throws Exception{
        //原订单信息
        PayOrderInfo orderInfo = orderInfoDao.selectByPagyTxnSsn(info.getTxnSsn());//退款订单
        PayOrderInfo origOder = orderInfoDao.selectByPrimaryKey(orderInfo.getOrigOrderSsn());//原订单
        String mchtId = "";
        if(null != origOder){
            mchtId = orderInfo.getMchtId();
            BigDecimal bankCouponAmt = IfspDataVerifyUtil.isEmpty(orderInfo.getBankCouponAmt())?BigDecimal.ZERO:new BigDecimal(orderInfo.getBankCouponAmt());
            info.setBankCouponAmt(bankCouponAmt);//银行营销金额
            info.setPayAmt(new BigDecimal(orderInfo.getPayAmt()));//支付金额
        }

        this.getMchtInfo(info,mchtId);

        info.refundInit();
        list.add(info);
    }

    //获取商户信息 待清算账户，结算账户 收单机构号  运营机构号
    private void getMchtInfo(ErrInfo info,String mchtId) throws Exception{
        //合同信息 待清算账户，结算账户
        MchtContInfo cont = mchtContInfoDao.queryByMchtId(mchtId);
        if(null == cont){
            throw new Exception(mchtId+" = 商户合同信息为空");
        }
        info.initCont(cont);

        //收单机构号  运营机构号
        MchtOrgRel mchtorg = mchtOrgRelDao.selectByMchtIdType(mchtId,"01");//手工单机构
        MchtOrgRel opOrg = mchtOrgRelDao.selectByMchtIdType(mchtId,"02");//运营机构
        if(null != mchtorg){
            info.setMchtOrgId(mchtorg.getOrgId());
            info.setOpOrgId(mchtorg.getOrgId());
        }
        if(null != opOrg){
            info.setOpOrgId(opOrg.getOrgId());
        }
    }

    /**
     * 是否结算
     * @param orderSnn 订单号/子订单号
     * @return 1未计算 空结算
     */
    private String hasStled(String orderSnn) throws Exception{
        BthMerInAccDtl accDtl = bthMerInAccDtlDao.selectByTxnSeqId(orderSnn);
        if(null != accDtl
                && Constans.SETTLE_STATUS_SUCCESS_CLEARING.equals(accDtl.getStlStatus())){
            return "";
        }else {//未结算
            return "1";
        }
    }

    /**
     * 分转元保留两位小数
     * @param fen
     * @return
     */
    private String fenToYuan2(BigDecimal fen){
        if(null == fen){
            return "0.00";
        }
        return String.valueOf(fen.movePointLeft(2));
    }

    /**金额为空转为0*/
    private BigDecimal amtTransfer(BigDecimal amt){
        return null == amt?BigDecimal.ZERO:amt;
    }


    public static void main(String[] args) {
//        System.out.println(IfspId.getUUID32());
//        String s = "aa11bb22cc";
//        int a = s.indexOf("bb")+"bb".length();
//        System.out.println(s.substring(a,a+2));
    }
}
