package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.math.BigDecimal;
import java.util.Date;

public class UnionBillOuter extends CommonDTO {
    private Date recoDate;

    private String txnSsn;

    private Date txnTime;

    private String txnType;

    private String txnState;

    private String recoState;

    private String proxyInsCode;

    private String sendInsCode;

    private String traceNum;

    private String transDate;

    private String acctNo;

    private BigDecimal transAmt;

    private String pagyNo;

    private String partPmsAmt;

    private String customerFee;

    private String msgType;

    private String transCode;

    private String merType;

    private String recCardTerminalCode;

    private String recCardCode;

    private String retrivalNo;

    private String sevCdtCode;

    private String authRespCode;

    private String recInsCode;

    private String orgTraceNum;

    private String transRespCode;

    private String sevInputWay;

    private BigDecimal hearGetFee;

    private BigDecimal hearPayFee;

    private BigDecimal routeSevFee;

    private String seReverseFlg;

    private String cardSeq;

    private String terLoadAbity;

    private String othMsg;

    private String transCard;

    private String periodSum;

    private String orderId;

    private String busPayMode;

    private String reserved;

    private String icCdtCode;

    private String orgTransDate;

    private String sndCardInsCode;

    private String transRegion;

    private String terminalType;

    private String eciFlag;

    private BigDecimal scdPlusFee;

    private String chkRst;

    private Date updDate;

    private String dubiousFlag;

    private BigDecimal brandFee;

    public UnionBillOuter() {
    }

    public UnionBillOuter(String txnSsn, String recoState) {
        this.txnSsn = txnSsn;
        this.recoState = recoState;
    }

    public UnionBillOuter(String txnSsn, String recoState, Date recoDate) {
        this.txnSsn = txnSsn;
        this.recoState = recoState;
        this.recoDate = recoDate;
    }

    public UnionBillOuter(String txnSsn, String recoState, Date recoDate, Date updDate) {
        this.txnSsn = txnSsn;
        this.recoState = recoState;
        this.recoDate = recoDate;
        this.updDate = updDate;
    }

    public UnionBillOuter(String txnSsn, String recoState, Date recoDate, Date updDate, String dubiousFlag) {
        this.txnSsn = txnSsn;
        this.recoState = recoState;
        this.recoDate = recoDate;
        this.updDate = updDate;
        this.dubiousFlag = dubiousFlag;
    }

    public Date getRecoDate() {
        return recoDate;
    }

    public void setRecoDate(Date recoDate) {
        this.recoDate = recoDate;
    }

    public String getTxnSsn() {
        return txnSsn;
    }

    public void setTxnSsn(String txnSsn) {
        this.txnSsn = txnSsn == null ? null : txnSsn.trim();
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

    public String getRecoState() {
        return recoState;
    }

    public void setRecoState(String recoState) {
        this.recoState = recoState == null ? null : recoState.trim();
    }

    public String getProxyInsCode() {
        return proxyInsCode;
    }

    public void setProxyInsCode(String proxyInsCode) {
        this.proxyInsCode = proxyInsCode == null ? null : proxyInsCode.trim();
    }

    public String getSendInsCode() {
        return sendInsCode;
    }

    public void setSendInsCode(String sendInsCode) {
        this.sendInsCode = sendInsCode == null ? null : sendInsCode.trim();
    }

    public String getTraceNum() {
        return traceNum;
    }

    public void setTraceNum(String traceNum) {
        this.traceNum = traceNum == null ? null : traceNum.trim();
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate == null ? null : transDate.trim();
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo == null ? null : acctNo.trim();
    }

    public BigDecimal getTransAmt() {
        return transAmt;
    }

    public void setTransAmt(BigDecimal transAmt) {
        this.transAmt = transAmt;
    }

    public String getPagyNo() {
        return pagyNo;
    }

    public void setPagyNo(String pagyNo) {
        this.pagyNo = pagyNo == null ? null : pagyNo.trim();
    }

    public String getPartPmsAmt() {
        return partPmsAmt;
    }

    public void setPartPmsAmt(String partPmsAmt) {
        this.partPmsAmt = partPmsAmt == null ? null : partPmsAmt.trim();
    }

    public String getCustomerFee() {
        return customerFee;
    }

    public void setCustomerFee(String customerFee) {
        this.customerFee = customerFee == null ? null : customerFee.trim();
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType == null ? null : msgType.trim();
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode == null ? null : transCode.trim();
    }

    public String getMerType() {
        return merType;
    }

    public void setMerType(String merType) {
        this.merType = merType == null ? null : merType.trim();
    }

    public String getRecCardTerminalCode() {
        return recCardTerminalCode;
    }

    public void setRecCardTerminalCode(String recCardTerminalCode) {
        this.recCardTerminalCode = recCardTerminalCode == null ? null : recCardTerminalCode.trim();
    }

    public String getRecCardCode() {
        return recCardCode;
    }

    public void setRecCardCode(String recCardCode) {
        this.recCardCode = recCardCode == null ? null : recCardCode.trim();
    }

    public String getRetrivalNo() {
        return retrivalNo;
    }

    public void setRetrivalNo(String retrivalNo) {
        this.retrivalNo = retrivalNo == null ? null : retrivalNo.trim();
    }

    public String getSevCdtCode() {
        return sevCdtCode;
    }

    public void setSevCdtCode(String sevCdtCode) {
        this.sevCdtCode = sevCdtCode == null ? null : sevCdtCode.trim();
    }

    public String getAuthRespCode() {
        return authRespCode;
    }

    public void setAuthRespCode(String authRespCode) {
        this.authRespCode = authRespCode == null ? null : authRespCode.trim();
    }

    public String getRecInsCode() {
        return recInsCode;
    }

    public void setRecInsCode(String recInsCode) {
        this.recInsCode = recInsCode == null ? null : recInsCode.trim();
    }

    public String getOrgTraceNum() {
        return orgTraceNum;
    }

    public void setOrgTraceNum(String orgTraceNum) {
        this.orgTraceNum = orgTraceNum == null ? null : orgTraceNum.trim();
    }

    public String getTransRespCode() {
        return transRespCode;
    }

    public void setTransRespCode(String transRespCode) {
        this.transRespCode = transRespCode == null ? null : transRespCode.trim();
    }

    public String getSevInputWay() {
        return sevInputWay;
    }

    public void setSevInputWay(String sevInputWay) {
        this.sevInputWay = sevInputWay == null ? null : sevInputWay.trim();
    }

    public BigDecimal getHearGetFee() {
        return hearGetFee;
    }

    public void setHearGetFee(BigDecimal hearGetFee) {
        this.hearGetFee = hearGetFee;
    }

    public BigDecimal getHearPayFee() {
        return hearPayFee;
    }

    public void setHearPayFee(BigDecimal hearPayFee) {
        this.hearPayFee = hearPayFee;
    }

    public BigDecimal getRouteSevFee() {
        return routeSevFee;
    }

    public void setRouteSevFee(BigDecimal routeSevFee) {
        this.routeSevFee = routeSevFee;
    }

    public String getSeReverseFlg() {
        return seReverseFlg;
    }

    public void setSeReverseFlg(String seReverseFlg) {
        this.seReverseFlg = seReverseFlg == null ? null : seReverseFlg.trim();
    }

    public String getCardSeq() {
        return cardSeq;
    }

    public void setCardSeq(String cardSeq) {
        this.cardSeq = cardSeq == null ? null : cardSeq.trim();
    }

    public String getTerLoadAbity() {
        return terLoadAbity;
    }

    public void setTerLoadAbity(String terLoadAbity) {
        this.terLoadAbity = terLoadAbity == null ? null : terLoadAbity.trim();
    }

    public String getOthMsg() {
        return othMsg;
    }

    public void setOthMsg(String othMsg) {
        this.othMsg = othMsg == null ? null : othMsg.trim();
    }

    public String getTransCard() {
        return transCard;
    }

    public void setTransCard(String transCard) {
        this.transCard = transCard == null ? null : transCard.trim();
    }

    public String getPeriodSum() {
        return periodSum;
    }

    public void setPeriodSum(String periodSum) {
        this.periodSum = periodSum == null ? null : periodSum.trim();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public String getBusPayMode() {
        return busPayMode;
    }

    public void setBusPayMode(String busPayMode) {
        this.busPayMode = busPayMode == null ? null : busPayMode.trim();
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved == null ? null : reserved.trim();
    }

    public String getIcCdtCode() {
        return icCdtCode;
    }

    public void setIcCdtCode(String icCdtCode) {
        this.icCdtCode = icCdtCode == null ? null : icCdtCode.trim();
    }

    public String getOrgTransDate() {
        return orgTransDate;
    }

    public void setOrgTransDate(String orgTransDate) {
        this.orgTransDate = orgTransDate == null ? null : orgTransDate.trim();
    }

    public String getSndCardInsCode() {
        return sndCardInsCode;
    }

    public void setSndCardInsCode(String sndCardInsCode) {
        this.sndCardInsCode = sndCardInsCode == null ? null : sndCardInsCode.trim();
    }

    public String getTransRegion() {
        return transRegion;
    }

    public void setTransRegion(String transRegion) {
        this.transRegion = transRegion == null ? null : transRegion.trim();
    }

    public String getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType == null ? null : terminalType.trim();
    }

    public String getEciFlag() {
        return eciFlag;
    }

    public void setEciFlag(String eciFlag) {
        this.eciFlag = eciFlag == null ? null : eciFlag.trim();
    }

    public BigDecimal getScdPlusFee() {
        return scdPlusFee;
    }

    public void setScdPlusFee(BigDecimal scdPlusFee) {
        this.scdPlusFee = scdPlusFee;
    }

    public String getChkRst() {
        return chkRst;
    }

    public void setChkRst(String chkRst) {
        this.chkRst = chkRst == null ? null : chkRst.trim();
    }

    public Date getUpdDate() {
        return updDate;
    }

    public void setUpdDate(Date updDate) {
        this.updDate = updDate;
    }

    public String getDubiousFlag() {
        return dubiousFlag;
    }

    public void setDubiousFlag(String dubiousFlag) {
        this.dubiousFlag = dubiousFlag == null ? null : dubiousFlag.trim();
    }

    public BigDecimal getBrandFee() {
        return brandFee;
    }

    public void setBrandFee(BigDecimal brandFee) {
        this.brandFee = brandFee;
    }
}