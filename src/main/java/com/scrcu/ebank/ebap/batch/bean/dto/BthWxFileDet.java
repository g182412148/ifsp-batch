package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.math.BigDecimal;

public class BthWxFileDet extends CommonDTO {
    private String orderNo;

    private String wxOrderNo;

    private String orderTp;

    private String srcOrderNo;

    private String srcWxOrderNo;

    private String txnTm;

    private String appId;

    private String mchId;

    private String subMchId;

    private String deviceInf;

    private String openId;

    private String tradeTp;

    private String tradeSt;

    private String bankTp;

    private String currency;

    private BigDecimal settOrderAmt;

    private BigDecimal couponAmt;

    private BigDecimal refundAmt;

    private BigDecimal couponRefAmt;

    private String refundTp;

    private String refundSt;

    private String prodNm;

    private String prodAttach;

    private BigDecimal feeAmt;

    private BigDecimal feeRate;

    private BigDecimal orderAmt;

    private BigDecimal apprRefundAmt;

    private String feeRmk;

    private String pagySysNo;

    private String pagyNo;

    private String chkDataDt;

    private String chkAcctSt;

    private String chkRst;

    private String lstUpdTm;
    
    private String dubiousFlag;// 用于还原可疑数据状态
    
    private String srcType;     //订单来源：01-间连，02-直连
    
    private String orderNoWx;     //微信订单号
    
    private String orderNoMer;    //商户订单号
    
    private String feeRateStr;    //解析费率零时使用
    
    private String refundBillMer;   //商户退款订单号
    
    private String refundBillWx;   //商户退款订单号
    
    
    
    public String getRefundBillMer() {
		return refundBillMer;
	}

	public void setRefundBillMer(String refundBillMer) {
		this.refundBillMer = refundBillMer;
	}

	public String getRefundBillWx() {
		return refundBillWx;
	}

	public void setRefundBillWx(String refundBillWx) {
		this.refundBillWx = refundBillWx;
	}

	public String getFeeRateStr() {
		return feeRateStr;
	}

	public void setFeeRateStr(String feeRateStr) {
		this.feeRateStr = feeRateStr;
	}

	public String getOrderNoWx() {
		return orderNoWx;
	}

	public void setOrderNoWx(String orderNoWx) {
		this.orderNoWx = orderNoWx;
	}

	public String getOrderNoMer() {
		return orderNoMer;
	}

	public void setOrderNoMer(String orderNoMer) {
		this.orderNoMer = orderNoMer;
	}

	public String getSrcType() {
		return srcType;
	}

	public void setSrcType(String srcType) {
		this.srcType = srcType;
	}

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

    public String getWxOrderNo() {
        return wxOrderNo;
    }

    public void setWxOrderNo(String wxOrderNo) {
        this.wxOrderNo = wxOrderNo == null ? null : wxOrderNo.trim();
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

    public String getSrcWxOrderNo() {
        return srcWxOrderNo;
    }

    public void setSrcWxOrderNo(String srcWxOrderNo) {
        this.srcWxOrderNo = srcWxOrderNo == null ? null : srcWxOrderNo.trim();
    }

    public String getTxnTm() {
        return txnTm;
    }

    public void setTxnTm(String txnTm) {
        this.txnTm = txnTm == null ? null : txnTm.trim();
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId == null ? null : mchId.trim();
    }

    public String getSubMchId() {
        return subMchId;
    }

    public void setSubMchId(String subMchId) {
        this.subMchId = subMchId == null ? null : subMchId.trim();
    }

    public String getDeviceInf() {
        return deviceInf;
    }

    public void setDeviceInf(String deviceInf) {
        this.deviceInf = deviceInf == null ? null : deviceInf.trim();
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId == null ? null : openId.trim();
    }

    public String getTradeTp() {
        return tradeTp;
    }

    public void setTradeTp(String tradeTp) {
        this.tradeTp = tradeTp == null ? null : tradeTp.trim();
    }

    public String getTradeSt() {
        return tradeSt;
    }

    public void setTradeSt(String tradeSt) {
        this.tradeSt = tradeSt == null ? null : tradeSt.trim();
    }

    public String getBankTp() {
        return bankTp;
    }

    public void setBankTp(String bankTp) {
        this.bankTp = bankTp == null ? null : bankTp.trim();
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency == null ? null : currency.trim();
    }

    public BigDecimal getSettOrderAmt() {
        return settOrderAmt;
    }

    public void setSettOrderAmt(BigDecimal settOrderAmt) {
        this.settOrderAmt = settOrderAmt;
    }

    public BigDecimal getCouponAmt() {
        return couponAmt;
    }

    public void setCouponAmt(BigDecimal couponAmt) {
        this.couponAmt = couponAmt;
    }

    public BigDecimal getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(BigDecimal refundAmt) {
        this.refundAmt = refundAmt;
    }

    public BigDecimal getCouponRefAmt() {
        return couponRefAmt;
    }

    public void setCouponRefAmt(BigDecimal couponRefAmt) {
        this.couponRefAmt = couponRefAmt;
    }

    public String getRefundTp() {
        return refundTp;
    }

    public void setRefundTp(String refundTp) {
        this.refundTp = refundTp == null ? null : refundTp.trim();
    }

    public String getRefundSt() {
        return refundSt;
    }

    public void setRefundSt(String refundSt) {
        this.refundSt = refundSt == null ? null : refundSt.trim();
    }

    public String getProdNm() {
        return prodNm;
    }

    public void setProdNm(String prodNm) {
        this.prodNm = prodNm == null ? null : prodNm.trim();
    }

    public String getProdAttach() {
        return prodAttach;
    }

    public void setProdAttach(String prodAttach) {
        this.prodAttach = prodAttach == null ? null : prodAttach.trim();
    }

    public BigDecimal getFeeAmt() {
        return feeAmt;
    }

    public void setFeeAmt(BigDecimal feeAmt) {
        this.feeAmt = feeAmt;
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    public BigDecimal getOrderAmt() {
        return orderAmt;
    }

    public void setOrderAmt(BigDecimal orderAmt) {
        this.orderAmt = orderAmt;
    }

    public BigDecimal getApprRefundAmt() {
        return apprRefundAmt;
    }

    public void setApprRefundAmt(BigDecimal apprRefundAmt) {
        this.apprRefundAmt = apprRefundAmt;
    }

    public String getFeeRmk() {
        return feeRmk;
    }

    public void setFeeRmk(String feeRmk) {
        this.feeRmk = feeRmk == null ? null : feeRmk.trim();
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