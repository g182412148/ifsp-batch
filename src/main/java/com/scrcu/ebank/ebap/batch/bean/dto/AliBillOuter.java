package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.DubisFlagDict;
import com.scrcu.ebank.ebap.batch.common.dict.RecoStatusDict;
import com.scrcu.ebank.ebap.batch.common.dict.RecoTxnStatusDict;
import com.scrcu.ebank.ebap.batch.common.dict.RecoTxnTypeDict;
import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class AliBillOuter extends CommonDTO {
    private String txnSsn;

    private Date recoDate;

    private Date txnTime;

    private String txnType;

    private String txnState;

    private BigDecimal txnAmt;

    private BigDecimal txnFeeAmt;

    private String recoState;

    private Date updDate;

    private String aliOrderNo;

    private String orderNo;

    private String busiTp;

    private String prodNm;

    private Date crtTm;

    private Date endTm;

    private String storeId;

    private String storeNm;

    private String operatorId;

    private String terminalId;

    private String buyerLoginId;

    private BigDecimal orderAmt;

    private BigDecimal recepitAmt;

    private BigDecimal aliRedAmt;

    private BigDecimal pointAmt;

    private BigDecimal aliDctAmt;

    private BigDecimal mchDctAmt;

    private BigDecimal voucherAmt;

    private String voucherNm;

    private BigDecimal mchRedAmt;

    private BigDecimal pccPayAmt;

    private String refundOrderNo;

    private BigDecimal feeAmt;

    private BigDecimal netAmt;

    private String subMchId;

    private String txnTp;

    private String rmk;

    private String dubisFlag;

    public AliBillOuter(){

    }

    public AliBillOuter(String txnSsn, RecoStatusDict recoStateDict, Date updDate){
        this.txnSsn = txnSsn;
        this.recoState = recoStateDict.getCode();
        this.updDate = updDate;
        //对账状态为可疑时, 设置可疑标志为1
        if(RecoStatusDict.DUBIOUS == recoStateDict){
            dubisFlag = DubisFlagDict.TRUE.getCode();
        }
    }


    public AliBillOuter(Date recoDate, String[] line){
        /**
         * 文件内容
         */
        //银联交易号
        this.aliOrderNo = line[0].trim();
        //商户订单号
        this.orderNo = line[1].trim();
        //业务类型
        this.busiTp = line[2];
        //商品名称
        this.prodNm = line[3];
        //创建时间
        this.crtTm = DateTimeFormat.forPattern(Constans.aliBillTxnTmFormat).parseDateTime(line[4]).toDate();
        //完成时间
        this.endTm = DateTimeFormat.forPattern(Constans.aliBillTxnTmFormat).parseDateTime(line[5]).toDate();
        //门店编号
        this.storeId = line[6];
        //门店名称
        this.storeNm = line[7];
        //操作员
        this.operatorId = line[8];
        //终端号
        this.terminalId = line[9];
        //对方账户
        this.buyerLoginId = line[10];
        //订单金额(文件是元 ,这里存分)
        this.orderAmt = new BigDecimal(line[11]).movePointRight(2).abs();
        //商家实收(文件是元 ,这里存分)
        this.recepitAmt = new BigDecimal(line[12]).movePointRight(2).abs();
        //支付宝红包(文件是元 ,这里存分)
        this.aliRedAmt = new BigDecimal(line[13]).movePointRight(2).abs();
        //集分宝(文件是元 ,这里存分)
        this.pointAmt = new BigDecimal(line[14]).movePointRight(2).abs();
        //支付宝优惠(文件是元 ,这里存分)
        this.aliDctAmt = new BigDecimal(line[15]).movePointRight(2).abs();
        //商家优惠(文件是元 ,这里存分)
        this.mchDctAmt = new BigDecimal(line[16]).movePointRight(2).abs();
        //券核销金额(文件是元 ,这里存分)
        this.voucherAmt = new BigDecimal(line[17]).movePointRight(2).abs();
        //券名称
        this.voucherNm = line[18];
        //商家红包消费金额(文件是元 ,这里存分)
        this.mchRedAmt = new BigDecimal(line[19]).movePointRight(2).abs();
        //卡消费金额(文件是元 ,这里存分)
        this.pccPayAmt = new BigDecimal(line[20]).movePointRight(2).abs();
        //退款批次号
        this.refundOrderNo = line[21].trim();
        //服务费(文件是元 ,这里存分)
        this.feeAmt = new BigDecimal(line[22]).movePointRight(2).abs();
        //实收净额(文件是元 ,这里存分)
        this.netAmt = new BigDecimal(line[23]).movePointRight(2).abs();
        //商户识别号
        this.subMchId = line[24];
        //交易方式
        this.txnTp = line[25];
        //备注
        this.rmk = line[26];


        /**
         * 对账字段
         */
        //对账日期
        this.recoDate = recoDate;
        //交易时间
        this.txnTime = this.crtTm;
        //交易类型
        RecoTxnTypeDict recoTxnType = RecoTxnTypeDict.aliType2RecoType(this.busiTp);
        this.txnType = recoTxnType.getCode();
        //交易状态
        this.txnState = RecoTxnStatusDict.SUCCESS.getCode();
        //交易金额
        this.txnAmt = this.orderAmt;
        //三方手续费
        this.txnFeeAmt = this.feeAmt;
        //对账状态
        this.recoState = RecoStatusDict.READY.getCode();

        /*
         *      支付交易:
         *          使用账单中的商户订单号对账;
         *      退款/撤销交易:
         *          使用账单中的退款批次号对账;
         */
        if (recoTxnType == RecoTxnTypeDict.PAY) {
            this.txnSsn = this.orderNo;
        } else {
            this.txnSsn = this.refundOrderNo;
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

    public BigDecimal getTxnFeeAmt() {
        return txnFeeAmt;
    }

    public void setTxnFeeAmt(BigDecimal txnFeeAmt) {
        this.txnFeeAmt = txnFeeAmt;
    }

    public String getRecoState() {
        return recoState;
    }

    public void setRecoState(String recoState) {
        this.recoState = recoState == null ? null : recoState.trim();
    }

    public Date getUpdDate() {
        return updDate;
    }

    public void setUpdDate(Date updDate) {
        this.updDate = updDate;
    }

    public String getAliOrderNo() {
        return aliOrderNo;
    }

    public void setAliOrderNo(String aliOrderNo) {
        this.aliOrderNo = aliOrderNo == null ? null : aliOrderNo.trim();
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getBusiTp() {
        return busiTp;
    }

    public void setBusiTp(String busiTp) {
        this.busiTp = busiTp == null ? null : busiTp.trim();
    }

    public String getProdNm() {
        return prodNm;
    }

    public void setProdNm(String prodNm) {
        this.prodNm = prodNm == null ? null : prodNm.trim();
    }

    public Date getCrtTm() {
        return crtTm;
    }

    public void setCrtTm(Date crtTm) {
        this.crtTm = crtTm;
    }

    public Date getEndTm() {
        return endTm;
    }

    public void setEndTm(Date endTm) {
        this.endTm = endTm;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId == null ? null : storeId.trim();
    }

    public String getStoreNm() {
        return storeNm;
    }

    public void setStoreNm(String storeNm) {
        this.storeNm = storeNm == null ? null : storeNm.trim();
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId == null ? null : operatorId.trim();
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId == null ? null : terminalId.trim();
    }

    public String getBuyerLoginId() {
        return buyerLoginId;
    }

    public void setBuyerLoginId(String buyerLoginId) {
        this.buyerLoginId = buyerLoginId == null ? null : buyerLoginId.trim();
    }

    public BigDecimal getOrderAmt() {
        return orderAmt;
    }

    public void setOrderAmt(BigDecimal orderAmt) {
        this.orderAmt = orderAmt;
    }

    public BigDecimal getRecepitAmt() {
        return recepitAmt;
    }

    public void setRecepitAmt(BigDecimal recepitAmt) {
        this.recepitAmt = recepitAmt;
    }

    public BigDecimal getAliRedAmt() {
        return aliRedAmt;
    }

    public void setAliRedAmt(BigDecimal aliRedAmt) {
        this.aliRedAmt = aliRedAmt;
    }

    public BigDecimal getPointAmt() {
        return pointAmt;
    }

    public void setPointAmt(BigDecimal pointAmt) {
        this.pointAmt = pointAmt;
    }

    public BigDecimal getAliDctAmt() {
        return aliDctAmt;
    }

    public void setAliDctAmt(BigDecimal aliDctAmt) {
        this.aliDctAmt = aliDctAmt;
    }

    public BigDecimal getMchDctAmt() {
        return mchDctAmt;
    }

    public void setMchDctAmt(BigDecimal mchDctAmt) {
        this.mchDctAmt = mchDctAmt;
    }

    public BigDecimal getVoucherAmt() {
        return voucherAmt;
    }

    public void setVoucherAmt(BigDecimal voucherAmt) {
        this.voucherAmt = voucherAmt;
    }

    public String getVoucherNm() {
        return voucherNm;
    }

    public void setVoucherNm(String voucherNm) {
        this.voucherNm = voucherNm == null ? null : voucherNm.trim();
    }

    public BigDecimal getMchRedAmt() {
        return mchRedAmt;
    }

    public void setMchRedAmt(BigDecimal mchRedAmt) {
        this.mchRedAmt = mchRedAmt;
    }

    public BigDecimal getPccPayAmt() {
        return pccPayAmt;
    }

    public void setPccPayAmt(BigDecimal pccPayAmt) {
        this.pccPayAmt = pccPayAmt;
    }

    public String getRefundOrderNo() {
        return refundOrderNo;
    }

    public void setRefundOrderNo(String refundOrderNo) {
        this.refundOrderNo = refundOrderNo == null ? null : refundOrderNo.trim();
    }

    public BigDecimal getFeeAmt() {
        return feeAmt;
    }

    public void setFeeAmt(BigDecimal feeAmt) {
        this.feeAmt = feeAmt;
    }

    public BigDecimal getNetAmt() {
        return netAmt;
    }

    public void setNetAmt(BigDecimal netAmt) {
        this.netAmt = netAmt;
    }

    public String getSubMchId() {
        return subMchId;
    }

    public void setSubMchId(String subMchId) {
        this.subMchId = subMchId == null ? null : subMchId.trim();
    }

    public String getTxnTp() {
        return txnTp;
    }

    public void setTxnTp(String txnTp) {
        this.txnTp = txnTp == null ? null : txnTp.trim();
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk == null ? null : rmk.trim();
    }

    public String getDubisFlag() {
        return dubisFlag;
    }

    public void setDubisFlag(String dubisFlag) {
        this.dubisFlag = dubisFlag == null ? null : dubisFlag.trim();
    }
}