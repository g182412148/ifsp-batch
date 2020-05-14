package com.scrcu.ebank.ebap.batch.bean.dto;

import java.util.Date;

public class PayOrderInfo {
    private String orderSsn;

    private Date orderTm;

    private String origOrderSsn;

    private Date origOrderTm;

    private String reqOrderSsn;

    private Date reqOrderTm;

    private String txnAppId;

    private String txnOpenId;

    private String mchtUserId;

    private String prodId;

    private String txnTypeNo;

    private String chlNo;

    private String orderType;

    private String userReqOrderSsn;

    private Date userReqOrderTm;

    private String userId;

    private String conid;

    private String lmtFlag;

    private String userSecrtyLevel;

    private String userCertLevel;

    private String behaviorCode;

    private String openBrc;

    private String userCertResultCode;

    private String userCertResultMsg;

    private String chnlIdPara;

    private String samllAmtFlag;

    private String acctTypeId;

    private String acctSubTypeId;

    private String acctClass;

    private String tokenPayInfo;

    private String acctNo;

    private String acctCustomerInfo;

    private String qrcPayerInfo;

    private String ctAcctNo;

    private String ctAcctName;

    private String platMchtId;

    private String platMchtNm;

    private String mchtId;

    private String mchtNm;

    private String orderBody;

    private String subOrderFlag;

    private String tplFlag;

    private String orderProductFlag;

    private String orderExpTm;

    private String frontEndUrl;

    private String backEndUrl;

    private String txnAttach;

    private String txnAmt;

    private String mchtCouponAmt;

    private String bankCouponAmt;

    private String couopnDesc;

    private String payAmt;

    private String txnRfdFlag;

    private String txnRfdAmtSum;

    private String txnRfdCountSum;

    private String orderCcyType;

    private String pagyTxnSsn;

    private Date pagyTxnTm;

    private String unTxnNo;

    private String tpamOrderSsn;

    private Date tpamOrderTm;

    private String pagyRespCode;

    private String pagyRespMsg;

    private String pagyTxnType;

    private String pagyProdCode;

    private String orderState;

    private String initLocalhostIp;

    private String updLocalhostIp;

    private String sysTraceId;

    private String fundChannel;

    private String olOrderType;

    private String finishBackNotify;

    private String backNotifyTimes;

    private Date lastNotifyTime;

    private Date crtTm;

    private Date upTm;

    private String misc1;

    private String misc2;

    private String misc3;

    private String misc4;
    
    private String refundAcctType;     //订单退款账户类型
    private String mchtSettleType;     //商户结算周期 0-实时结算；1-非实时结算 0表示已经进行实时结算
    
    private String acptChlNo;          //被扫渠道号
    
    private String mchtIncentiveAmt;//商户奖励金

    private String keepAcctFlag; //记账标志

    public String getAcptChlNo() {
		return acptChlNo;
	}

	public void setAcptChlNo(String acptChlNo) {
		this.acptChlNo = acptChlNo;
	}

	public String getRefundAcctType() {
		return refundAcctType;
	}

	public void setRefundAcctType(String refundAcctType) {
		this.refundAcctType = refundAcctType;
	}

	public String getMchtSettleType() {
		return mchtSettleType;
	}

	public void setMchtSettleType(String mchtSettleType) {
		this.mchtSettleType = mchtSettleType;
	}

	public String getOrderSsn() {
        return orderSsn;
    }

    public void setOrderSsn(String orderSsn) {
        this.orderSsn = orderSsn;
    }

    public Date getOrderTm() {
        return orderTm;
    }

    public void setOrderTm(Date orderTm) {
        this.orderTm = orderTm;
    }

    public String getOrigOrderSsn() {
        return origOrderSsn;
    }

    public void setOrigOrderSsn(String origOrderSsn) {
        this.origOrderSsn = origOrderSsn;
    }

    public Date getOrigOrderTm() {
        return origOrderTm;
    }

    public void setOrigOrderTm(Date origOrderTm) {
        this.origOrderTm = origOrderTm;
    }

    public String getReqOrderSsn() {
        return reqOrderSsn;
    }

    public void setReqOrderSsn(String reqOrderSsn) {
        this.reqOrderSsn = reqOrderSsn;
    }

    public Date getReqOrderTm() {
        return reqOrderTm;
    }

    public void setReqOrderTm(Date reqOrderTm) {
        this.reqOrderTm = reqOrderTm;
    }

    public String getTxnAppId() {
        return txnAppId;
    }

    public void setTxnAppId(String txnAppId) {
        this.txnAppId = txnAppId;
    }

    public String getTxnOpenId() {
        return txnOpenId;
    }

    public void setTxnOpenId(String txnOpenId) {
        this.txnOpenId = txnOpenId;
    }

    public String getMchtUserId() {
        return mchtUserId;
    }

    public void setMchtUserId(String mchtUserId) {
        this.mchtUserId = mchtUserId;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getTxnTypeNo() {
        return txnTypeNo;
    }

    public void setTxnTypeNo(String txnTypeNo) {
        this.txnTypeNo = txnTypeNo;
    }

    public String getChlNo() {
        return chlNo;
    }

    public void setChlNo(String chlNo) {
        this.chlNo = chlNo;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getUserReqOrderSsn() {
        return userReqOrderSsn;
    }

    public void setUserReqOrderSsn(String userReqOrderSsn) {
        this.userReqOrderSsn = userReqOrderSsn;
    }

    public Date getUserReqOrderTm() {
        return userReqOrderTm;
    }

    public void setUserReqOrderTm(Date userReqOrderTm) {
        this.userReqOrderTm = userReqOrderTm;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConid() {
        return conid;
    }

    public void setConid(String conid) {
        this.conid = conid;
    }

    public String getLmtFlag() {
        return lmtFlag;
    }

    public void setLmtFlag(String lmtFlag) {
        this.lmtFlag = lmtFlag;
    }

    public String getUserSecrtyLevel() {
        return userSecrtyLevel;
    }

    public void setUserSecrtyLevel(String userSecrtyLevel) {
        this.userSecrtyLevel = userSecrtyLevel;
    }

    public String getUserCertLevel() {
        return userCertLevel;
    }

    public void setUserCertLevel(String userCertLevel) {
        this.userCertLevel = userCertLevel;
    }

    public String getBehaviorCode() {
        return behaviorCode;
    }

    public void setBehaviorCode(String behaviorCode) {
        this.behaviorCode = behaviorCode;
    }

    public String getOpenBrc() {
        return openBrc;
    }

    public void setOpenBrc(String openBrc) {
        this.openBrc = openBrc;
    }

    public String getUserCertResultCode() {
        return userCertResultCode;
    }

    public void setUserCertResultCode(String userCertResultCode) {
        this.userCertResultCode = userCertResultCode;
    }

    public String getUserCertResultMsg() {
        return userCertResultMsg;
    }

    public void setUserCertResultMsg(String userCertResultMsg) {
        this.userCertResultMsg = userCertResultMsg;
    }

    public String getChnlIdPara() {
        return chnlIdPara;
    }

    public void setChnlIdPara(String chnlIdPara) {
        this.chnlIdPara = chnlIdPara;
    }

    public String getSamllAmtFlag() {
        return samllAmtFlag;
    }

    public void setSamllAmtFlag(String samllAmtFlag) {
        this.samllAmtFlag = samllAmtFlag;
    }

    public String getAcctTypeId() {
        return acctTypeId;
    }

    public void setAcctTypeId(String acctTypeId) {
        this.acctTypeId = acctTypeId;
    }

    public String getAcctSubTypeId() {
        return acctSubTypeId;
    }

    public void setAcctSubTypeId(String acctSubTypeId) {
        this.acctSubTypeId = acctSubTypeId;
    }

    public String getAcctClass() {
        return acctClass;
    }

    public void setAcctClass(String acctClass) {
        this.acctClass = acctClass;
    }

    public String getTokenPayInfo() {
        return tokenPayInfo;
    }

    public void setTokenPayInfo(String tokenPayInfo) {
        this.tokenPayInfo = tokenPayInfo;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getAcctCustomerInfo() {
        return acctCustomerInfo;
    }

    public void setAcctCustomerInfo(String acctCustomerInfo) {
        this.acctCustomerInfo = acctCustomerInfo;
    }

    public String getQrcPayerInfo() {
        return qrcPayerInfo;
    }

    public void setQrcPayerInfo(String qrcPayerInfo) {
        this.qrcPayerInfo = qrcPayerInfo;
    }

    public String getCtAcctNo() {
        return ctAcctNo;
    }

    public void setCtAcctNo(String ctAcctNo) {
        this.ctAcctNo = ctAcctNo;
    }

    public String getCtAcctName() {
        return ctAcctName;
    }

    public void setCtAcctName(String ctAcctName) {
        this.ctAcctName = ctAcctName;
    }

    public String getPlatMchtId() {
        return platMchtId;
    }

    public void setPlatMchtId(String platMchtId) {
        this.platMchtId = platMchtId;
    }

    public String getPlatMchtNm() {
        return platMchtNm;
    }

    public void setPlatMchtNm(String platMchtNm) {
        this.platMchtNm = platMchtNm;
    }

    public String getMchtId() {
        return mchtId;
    }

    public void setMchtId(String mchtId) {
        this.mchtId = mchtId;
    }

    public String getMchtNm() {
        return mchtNm;
    }

    public void setMchtNm(String mchtNm) {
        this.mchtNm = mchtNm;
    }

    public String getOrderBody() {
        return orderBody;
    }

    public void setOrderBody(String orderBody) {
        this.orderBody = orderBody;
    }

    public String getSubOrderFlag() {
        return subOrderFlag;
    }

    public void setSubOrderFlag(String subOrderFlag) {
        this.subOrderFlag = subOrderFlag;
    }

    public String getTplFlag() {
        return tplFlag;
    }

    public void setTplFlag(String tplFlag) {
        this.tplFlag = tplFlag;
    }

    public String getOrderProductFlag() {
        return orderProductFlag;
    }

    public void setOrderProductFlag(String orderProductFlag) {
        this.orderProductFlag = orderProductFlag;
    }

    public String getOrderExpTm() {
        return orderExpTm;
    }

    public void setOrderExpTm(String orderExpTm) {
        this.orderExpTm = orderExpTm;
    }

    public String getFrontEndUrl() {
        return frontEndUrl;
    }

    public void setFrontEndUrl(String frontEndUrl) {
        this.frontEndUrl = frontEndUrl;
    }

    public String getBackEndUrl() {
        return backEndUrl;
    }

    public void setBackEndUrl(String backEndUrl) {
        this.backEndUrl = backEndUrl;
    }

    public String getTxnAttach() {
        return txnAttach;
    }

    public void setTxnAttach(String txnAttach) {
        this.txnAttach = txnAttach;
    }

    public String getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getMchtCouponAmt() {
        return mchtCouponAmt;
    }

    public void setMchtCouponAmt(String mchtCouponAmt) {
        this.mchtCouponAmt = mchtCouponAmt;
    }

    public String getBankCouponAmt() {
        return bankCouponAmt;
    }

    public void setBankCouponAmt(String bankCouponAmt) {
        this.bankCouponAmt = bankCouponAmt;
    }

    public String getCouopnDesc() {
        return couopnDesc;
    }

    public void setCouopnDesc(String couopnDesc) {
        this.couopnDesc = couopnDesc;
    }

    public String getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(String payAmt) {
        this.payAmt = payAmt;
    }

    public String getTxnRfdFlag() {
        return txnRfdFlag;
    }

    public void setTxnRfdFlag(String txnRfdFlag) {
        this.txnRfdFlag = txnRfdFlag;
    }

    public String getTxnRfdAmtSum() {
        return txnRfdAmtSum;
    }

    public void setTxnRfdAmtSum(String txnRfdAmtSum) {
        this.txnRfdAmtSum = txnRfdAmtSum;
    }

    public String getTxnRfdCountSum() {
        return txnRfdCountSum;
    }

    public void setTxnRfdCountSum(String txnRfdCountSum) {
        this.txnRfdCountSum = txnRfdCountSum;
    }

    public String getOrderCcyType() {
        return orderCcyType;
    }

    public void setOrderCcyType(String orderCcyType) {
        this.orderCcyType = orderCcyType;
    }

    public String getPagyTxnSsn() {
        return pagyTxnSsn;
    }

    public void setPagyTxnSsn(String pagyTxnSsn) {
        this.pagyTxnSsn = pagyTxnSsn;
    }

    public Date getPagyTxnTm() {
        return pagyTxnTm;
    }

    public void setPagyTxnTm(Date pagyTxnTm) {
        this.pagyTxnTm = pagyTxnTm;
    }

    public String getUnTxnNo() {
        return unTxnNo;
    }

    public void setUnTxnNo(String unTxnNo) {
        this.unTxnNo = unTxnNo;
    }

    public String getTpamOrderSsn() {
        return tpamOrderSsn;
    }

    public void setTpamOrderSsn(String tpamOrderSsn) {
        this.tpamOrderSsn = tpamOrderSsn;
    }

    public Date getTpamOrderTm() {
        return tpamOrderTm;
    }

    public void setTpamOrderTm(Date tpamOrderTm) {
        this.tpamOrderTm = tpamOrderTm;
    }

    public String getPagyRespCode() {
        return pagyRespCode;
    }

    public void setPagyRespCode(String pagyRespCode) {
        this.pagyRespCode = pagyRespCode;
    }

    public String getPagyRespMsg() {
        return pagyRespMsg;
    }

    public void setPagyRespMsg(String pagyRespMsg) {
        this.pagyRespMsg = pagyRespMsg;
    }

    public String getPagyTxnType() {
        return pagyTxnType;
    }

    public void setPagyTxnType(String pagyTxnType) {
        this.pagyTxnType = pagyTxnType;
    }

    public String getPagyProdCode() {
        return pagyProdCode;
    }

    public void setPagyProdCode(String pagyProdCode) {
        this.pagyProdCode = pagyProdCode;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getInitLocalhostIp() {
        return initLocalhostIp;
    }

    public void setInitLocalhostIp(String initLocalhostIp) {
        this.initLocalhostIp = initLocalhostIp;
    }

    public String getUpdLocalhostIp() {
        return updLocalhostIp;
    }

    public void setUpdLocalhostIp(String updLocalhostIp) {
        this.updLocalhostIp = updLocalhostIp;
    }

    public String getSysTraceId() {
        return sysTraceId;
    }

    public void setSysTraceId(String sysTraceId) {
        this.sysTraceId = sysTraceId;
    }

    public String getFundChannel() {
        return fundChannel;
    }

    public void setFundChannel(String fundChannel) {
        this.fundChannel = fundChannel;
    }

    public String getOlOrderType() {
        return olOrderType;
    }

    public void setOlOrderType(String olOrderType) {
        this.olOrderType = olOrderType;
    }

    public String getFinishBackNotify() {
        return finishBackNotify;
    }

    public void setFinishBackNotify(String finishBackNotify) {
        this.finishBackNotify = finishBackNotify;
    }

    public String getBackNotifyTimes() {
        return backNotifyTimes;
    }

    public void setBackNotifyTimes(String backNotifyTimes) {
        this.backNotifyTimes = backNotifyTimes;
    }

    public Date getLastNotifyTime() {
        return lastNotifyTime;
    }

    public void setLastNotifyTime(Date lastNotifyTime) {
        this.lastNotifyTime = lastNotifyTime;
    }

    public Date getCrtTm() {
        return crtTm;
    }

    public void setCrtTm(Date crtTm) {
        this.crtTm = crtTm;
    }

    public Date getUpTm() {
        return upTm;
    }

    public void setUpTm(Date upTm) {
        this.upTm = upTm;
    }

    public String getMisc1() {
        return misc1;
    }

    public void setMisc1(String misc1) {
        this.misc1 = misc1;
    }

    public String getMisc2() {
        return misc2;
    }

    public void setMisc2(String misc2) {
        this.misc2 = misc2;
    }

    public String getMisc3() {
        return misc3;
    }

    public void setMisc3(String misc3) {
        this.misc3 = misc3;
    }

    public String getMisc4() {
        return misc4;
    }

    public void setMisc4(String misc4) {
        this.misc4 = misc4;
    }

	public String getMchtIncentiveAmt() {
		return mchtIncentiveAmt;
	}

	public void setMchtIncentiveAmt(String mchtIncentiveAmt) {
		this.mchtIncentiveAmt = mchtIncentiveAmt;
	}

    public String getKeepAcctFlag() {
        return keepAcctFlag;
    }

    public void setKeepAcctFlag(String keepAcctFlag) {
        this.keepAcctFlag = keepAcctFlag == null ? null : keepAcctFlag.trim();
    }
}