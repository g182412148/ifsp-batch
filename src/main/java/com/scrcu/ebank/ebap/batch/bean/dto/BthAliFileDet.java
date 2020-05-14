package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.math.BigDecimal;

public class BthAliFileDet extends CommonDTO {
    private String orderNo;

    private String aliOrderNo;

    private String orderTp;

    private String srcOrderNo;

    private String srcAliOrderNo;

    private String busiTp;

    private String prodNm;

    private String crtTm;

    private String endTm;

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

    private BigDecimal feeAmt;

    private BigDecimal netAmt;

    private String subMchId;

    private String txnTp;

    private String rmk;

    private String pagySysNo;

    private String pagyNo;

    private BigDecimal orderAmtTmp;

    private BigDecimal feeAmtTmp;

    private String chkDataDt;

    private String chkAcctSt;

    private String chkRst;

    private String lstUpdTm;
    
    private String dubiousFlag;// 用于还原可疑数据状态
    

    public String getDubiousFlag() {
		return dubiousFlag;
	}

	public void setDubiousFlag(String dubiousFlag) {
		this.dubiousFlag = dubiousFlag;
	}

	public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getAliOrderNo() {
        return aliOrderNo;
    }

    public void setAliOrderNo(String aliOrderNo) {
        this.aliOrderNo = aliOrderNo == null ? null : aliOrderNo.trim();
    }

    public String getOrderTp() {
        return orderTp;
    }

    public void setOrderTp(String orderTp) {
        this.orderTp = orderTp == null ? null : orderTp.trim();
    }

    public String getSrcOrderNo() {
        return srcOrderNo;
    }

    public void setSrcOrderNo(String srcOrderNo) {
        this.srcOrderNo = srcOrderNo == null ? null : srcOrderNo.trim();
    }

    public String getSrcAliOrderNo() {
        return srcAliOrderNo;
    }

    public void setSrcAliOrderNo(String srcAliOrderNo) {
        this.srcAliOrderNo = srcAliOrderNo == null ? null : srcAliOrderNo.trim();
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

    public String getCrtTm() {
        return crtTm;
    }

    public void setCrtTm(String crtTm) {
        this.crtTm = crtTm == null ? null : crtTm.trim();
    }

    public String getEndTm() {
        return endTm;
    }

    public void setEndTm(String endTm) {
        this.endTm = endTm == null ? null : endTm.trim();
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

    public String getPagySysNo() {
        return pagySysNo;
    }

    public void setPagySysNo(String pagySysNo) {
        this.pagySysNo = pagySysNo == null ? null : pagySysNo.trim();
    }

    public String getPagyNo() {
        return pagyNo;
    }

    public void setPagyNo(String pagyNo) {
        this.pagyNo = pagyNo == null ? null : pagyNo.trim();
    }

    public BigDecimal getOrderAmtTmp() {
        return orderAmtTmp;
    }

    public void setOrderAmtTmp(BigDecimal orderAmtTmp) {
        this.orderAmtTmp = orderAmtTmp;
    }

    public BigDecimal getFeeAmtTmp() {
        return feeAmtTmp;
    }

    public void setFeeAmtTmp(BigDecimal feeAmtTmp) {
        this.feeAmtTmp = feeAmtTmp;
    }

    public String getChkDataDt() {
        return chkDataDt;
    }

    public void setChkDataDt(String chkDataDt) {
        this.chkDataDt = chkDataDt == null ? null : chkDataDt.trim();
    }

    public String getChkAcctSt() {
        return chkAcctSt;
    }

    public void setChkAcctSt(String chkAcctSt) {
        this.chkAcctSt = chkAcctSt == null ? null : chkAcctSt.trim();
    }

    public String getChkRst() {
        return chkRst;
    }

    public void setChkRst(String chkRst) {
        this.chkRst = chkRst == null ? null : chkRst.trim();
    }

    public String getLstUpdTm() {
        return lstUpdTm;
    }

    public void setLstUpdTm(String lstUpdTm) {
        this.lstUpdTm = lstUpdTm == null ? null : lstUpdTm.trim();
    }
}