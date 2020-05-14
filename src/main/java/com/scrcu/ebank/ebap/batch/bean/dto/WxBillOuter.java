package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.DubisFlagDict;
import com.scrcu.ebank.ebap.batch.common.dict.RecoStatusDict;
import com.scrcu.ebank.ebap.batch.common.dict.RecoTxnStatusDict;
import com.scrcu.ebank.ebap.batch.common.dict.RecoTxnTypeDict;
import com.scrcu.ebank.ebap.batch.common.utils.ConstantUtil;
import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class WxBillOuter extends CommonDTO {
    private String txnSsn;

    private Date recoDate;

    private Date txnTime;

    private String txnType;

    private String txnState;

    private BigDecimal txnAmt;

    private String recoState;

    private String dubisFlag;

    private String appId;

    private String mchtId;

    private String bankMchtId;

    private String devIp;

    private String outerOrderId;

    private String mchtOrderId;

    private String userId;

    private String outerTxnType;

    private String outerTxnState;

    private String payBank;

    private String currType;

    private BigDecimal settlAmt;

    private BigDecimal coupAmt;

    private String outerRefudId;

    private String mchtRefudId;

    private BigDecimal refudAmt;

    private BigDecimal rechRefudAmt;

    private String refudType;

    private String refudState;

    private String goodsName;

    private String mchtData;

    private BigDecimal feeRate;

    private BigDecimal feeAmt;

    private BigDecimal orderAmt;

    private BigDecimal applyRefudAmt;

    private String feeReateDesc;

    public WxBillOuter() {
    }

    public WxBillOuter(String txnSsn, RecoStatusDict recoStateDict) {
        this.txnSsn = txnSsn;
        this.recoState = recoStateDict.getCode();
        //对账状态为可疑时, 设置可疑标志为1
        if(RecoStatusDict.DUBIOUS == recoStateDict){
            dubisFlag = DubisFlagDict.TRUE.getCode();
        }
    }

    /**
     * 根据对账文件内容, 解析为对账明细
     *
     * @param line
     */
    public WxBillOuter(Date recoDate, String[] line) {
        //对账日期
        this.recoDate = recoDate;
        /*
         *  原始信息
         */
        //交易时间
        this.txnTime = DateTimeFormat.forPattern(Constans.wxBillTxnTmFormat).parseDateTime(ConstantUtil.removeSplitSymbol(line[0])).toDate();
        this.appId = ConstantUtil.removeSplitSymbol(line[1]);
        this.mchtId = ConstantUtil.removeSplitSymbol(line[2]);
        //特约商户号
        this.bankMchtId = ConstantUtil.removeSplitSymbol(line[3]);
        this.devIp = ConstantUtil.removeSplitSymbol(line[4]);
        this.outerOrderId = ConstantUtil.removeSplitSymbol(line[5]);
        this.mchtOrderId = ConstantUtil.removeSplitSymbol(line[6]);
        this.userId = ConstantUtil.removeSplitSymbol(line[7]);
        this.outerTxnType = ConstantUtil.removeSplitSymbol(line[8]);
        this.outerTxnState = ConstantUtil.removeSplitSymbol(line[9]);
        this.payBank = ConstantUtil.removeSplitSymbol(line[10]);
        this.currType = ConstantUtil.removeSplitSymbol(line[11]);
        this.settlAmt = new BigDecimal(ConstantUtil.removeSplitSymbol(line[12])).movePointRight(2);
        this.coupAmt = new BigDecimal(ConstantUtil.removeSplitSymbol(line[13])).movePointRight(2);
        this.outerRefudId = ConstantUtil.removeSplitSymbol(line[14]);
        this.mchtRefudId = ConstantUtil.removeSplitSymbol(line[15]);
        this.refudAmt = new BigDecimal(ConstantUtil.removeSplitSymbol(line[16])).movePointRight(2);
        this.rechRefudAmt = new BigDecimal(ConstantUtil.removeSplitSymbol(line[17])).movePointRight(2);
        this.refudType = ConstantUtil.removeSplitSymbol(line[18]);
        this.refudState = ConstantUtil.removeSplitSymbol(line[19]);
        this.goodsName = ConstantUtil.removeSplitSymbol(line[20]);
        this.mchtData = ConstantUtil.removeSplitSymbol(line[21]);
        this.feeAmt = new BigDecimal(ConstantUtil.removeSplitSymbol(line[22])).movePointRight(2).abs();
        this.feeRate = new BigDecimal(ConstantUtil.removeSplitSymbol(line[23]));
        this.orderAmt = new BigDecimal(ConstantUtil.removeSplitSymbol(line[24])).movePointRight(2);
        this.applyRefudAmt = new BigDecimal(ConstantUtil.removeSplitSymbol(line[25])).movePointRight(2);
        this.feeReateDesc = ConstantUtil.removeSplitSymbol(line[26]);
        /*
         * 对账字段
         */
        //交易类型
        RecoTxnTypeDict recoTxnType = RecoTxnTypeDict.wxType2RecoType(this.outerTxnState);
        this.txnType = recoTxnType.getCode();
        //交易状态
        RecoTxnStatusDict recoTxnStatus = RecoTxnStatusDict.wxType2RecoType(this.outerTxnState, this.refudState);
        this.txnState = recoTxnStatus.getCode();
        //对账状态
        this.recoState = RecoStatusDict.READY.getCode();
        /*
         *      支付交易:
         *          使用账单中的订单金额对账;
         *          使用账单中的商户单号对账;
         *      退款/撤销交易:
         *          使用账单中的退款金额对账;
         *          使用账单中的
         */
        if (recoTxnType == RecoTxnTypeDict.PAY) {
            this.txnSsn = this.mchtOrderId;
            this.txnAmt = this.orderAmt;
        } else {
            this.txnSsn = this.mchtRefudId;
            this.txnAmt = this.refudAmt;
            //撤销交易无需参与对账
            if (recoTxnType == RecoTxnTypeDict.REVOKE) {
                this.recoState = RecoStatusDict.SKIP.getCode();
            }
        }
    }

    public String getTxnSsn() {
        return txnSsn;
    }

    public void setTxnSsn(String txnSsn) {
        this.txnSsn = txnSsn == null ? null : txnSsn.trim();
    }

    public Date getRecoDate() {
        return recoDate;
    }

    public void setRecoDate(Date recoDate) {
        this.recoDate = recoDate;
    }

    public Date getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(Date txnTime) {
        this.txnTime = txnTime;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType == null ? null : txnType.trim();
    }

    public String getTxnState() {
        return txnState;
    }

    public void setTxnState(String txnState) {
        this.txnState = txnState == null ? null : txnState.trim();
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getRecoState() {
        return recoState;
    }

    public void setRecoState(String recoState) {
        this.recoState = recoState == null ? null : recoState.trim();
    }

    public String getDubisFlag() {
        return dubisFlag;
    }

    public void setDubisFlag(String dubisFlag) {
        this.dubisFlag = dubisFlag == null ? null : dubisFlag.trim();
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    public String getMchtId() {
        return mchtId;
    }

    public void setMchtId(String mchtId) {
        this.mchtId = mchtId == null ? null : mchtId.trim();
    }

    public String getBankMchtId() {
        return bankMchtId;
    }

    public void setBankMchtId(String bankMchtId) {
        this.bankMchtId = bankMchtId == null ? null : bankMchtId.trim();
    }

    public String getDevIp() {
        return devIp;
    }

    public void setDevIp(String devIp) {
        this.devIp = devIp == null ? null : devIp.trim();
    }

    public String getOuterOrderId() {
        return outerOrderId;
    }

    public void setOuterOrderId(String outerOrderId) {
        this.outerOrderId = outerOrderId == null ? null : outerOrderId.trim();
    }

    public String getMchtOrderId() {
        return mchtOrderId;
    }

    public void setMchtOrderId(String mchtOrderId) {
        this.mchtOrderId = mchtOrderId == null ? null : mchtOrderId.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getOuterTxnType() {
        return outerTxnType;
    }

    public void setOuterTxnType(String outerTxnType) {
        this.outerTxnType = outerTxnType == null ? null : outerTxnType.trim();
    }

    public String getOuterTxnState() {
        return outerTxnState;
    }

    public void setOuterTxnState(String outerTxnState) {
        this.outerTxnState = outerTxnState == null ? null : outerTxnState.trim();
    }

    public String getPayBank() {
        return payBank;
    }

    public void setPayBank(String payBank) {
        this.payBank = payBank == null ? null : payBank.trim();
    }

    public String getCurrType() {
        return currType;
    }

    public void setCurrType(String currType) {
        this.currType = currType == null ? null : currType.trim();
    }

    public BigDecimal getSettlAmt() {
        return settlAmt;
    }

    public void setSettlAmt(BigDecimal settlAmt) {
        this.settlAmt = settlAmt;
    }

    public BigDecimal getCoupAmt() {
        return coupAmt;
    }

    public void setCoupAmt(BigDecimal coupAmt) {
        this.coupAmt = coupAmt;
    }

    public String getOuterRefudId() {
        return outerRefudId;
    }

    public void setOuterRefudId(String outerRefudId) {
        this.outerRefudId = outerRefudId == null ? null : outerRefudId.trim();
    }

    public String getMchtRefudId() {
        return mchtRefudId;
    }

    public void setMchtRefudId(String mchtRefudId) {
        this.mchtRefudId = mchtRefudId == null ? null : mchtRefudId.trim();
    }

    public BigDecimal getRefudAmt() {
        return refudAmt;
    }

    public void setRefudAmt(BigDecimal refudAmt) {
        this.refudAmt = refudAmt;
    }

    public BigDecimal getRechRefudAmt() {
        return rechRefudAmt;
    }

    public void setRechRefudAmt(BigDecimal rechRefudAmt) {
        this.rechRefudAmt = rechRefudAmt;
    }

    public String getRefudType() {
        return refudType;
    }

    public void setRefudType(String refudType) {
        this.refudType = refudType == null ? null : refudType.trim();
    }

    public String getRefudState() {
        return refudState;
    }

    public void setRefudState(String refudState) {
        this.refudState = refudState == null ? null : refudState.trim();
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName == null ? null : goodsName.trim();
    }

    public String getMchtData() {
        return mchtData;
    }

    public void setMchtData(String mchtData) {
        this.mchtData = mchtData == null ? null : mchtData.trim();
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    public BigDecimal getFeeAmt() {
        return feeAmt;
    }

    public void setFeeAmt(BigDecimal feeAmt) {
        this.feeAmt = feeAmt;
    }

    public BigDecimal getOrderAmt() {
        return orderAmt;
    }

    public void setOrderAmt(BigDecimal orderAmt) {
        this.orderAmt = orderAmt;
    }

    public BigDecimal getApplyRefudAmt() {
        return applyRefudAmt;
    }

    public void setApplyRefudAmt(BigDecimal applyRefudAmt) {
        this.applyRefudAmt = applyRefudAmt;
    }

    public String getFeeReateDesc() {
        return feeReateDesc;
    }

    public void setFeeReateDesc(String feeReateDesc) {
        this.feeReateDesc = feeReateDesc == null ? null : feeReateDesc.trim();
    }
}