package com.scrcu.ebank.ebap.batch.bean.dto;

import java.util.Date;

public class BthMerInAccDtl extends BthMerInAccDtlKey {
    private String chlMerName;

    private String chlSubMerId;

    private String chlSubMerName;

    private String txnAmt;

    private String setlAmt;

    private String setlFeeAmt;

    private String setlFeeAmtDis;

    private String setlAcctNo;

    private String setlAcctName;

    private String setlAcctType;

    private String setlAcctInstitute2;

    private String setlAcctInstituteName2;

    private String agentId;

    private String agentName;

    private String agentSetlAcctNo;

    private String agentSetlAcctName;

    private String agentSetlFeeAmt;

    private String tramFeeAmt;

    private String localFeeAmt;

    private String pagyNo;

    private String outAcctNo;

    private String localFeeAcct;

    private String agFee;

    private String inAcctStat;
    
    private String orderType;   //订单类型 01-支付 02-退款
    
    private String stlStatus;  //结算状态
    
    private String refundAccType;  //退款账户类型
    
    private String stlType;  //01-结算给本商户 02-结算给上级商户 03-先结算给上级商户再结算给本商户
    
    private String subStlDate;  //分店商户结算日期()
    
    private String commissionAmt; //佣金
    
    private String bankCouponAmt;  //银行营销总金额
    
    private String bankHbAmt;      //红包总金额(平台红包+机构红包)
    
    private String logisFee;      //物流费用
    
    private String logisType;     //物流类型01-统一物流；02-平台物流
    
    private String logisPartnerCode;   //物流合作方代码
    
    private String pointDedcutAmt; //积分抵扣金额
    
    private String brandFee;       //品牌服务费
    
    private String openBrc;        //支付账户开户机构(本行支付时使用)
    
    private String txnType;       //交易类型-同步订单表txnTypeNo
    
    private String fundChannel;    //接入渠道
    
    private String updateDate;
    
    private String subStlStatus;   //二级商户结算状态
    
    private String createDate;     //创建时间
    
    private String subOrderTime;            //创建时间
    private String subOrderSsn;             //子订单号
    private String orderSsn;                //订单号
    private String reqSubOrderSsn;          //商城请求订单号
    private String payStatusUpdateTime;     //支付完成时间
    private String cur;                     //币种
    private String rowCount;                //行数统计-生成文件时使用
    
    private String payChannel;
    
    private String platHbAmt;
    
    private String chnlFeeFlag;             //是否渠道手续费
    
    private String logisFeeAmt;             //物流手续费(物流费对应的手续费)
    
    
    private Date txnTm;                  //订单时间
    
    private String mchtPayAmt;        //营销：商户出资

    private String bankPayAmt;        //营销：银行出资

    private String userorgPayAmt;      //营销：用户机构出资

    private String activeDesc;         //营销描述
     
    /**
     * 批次号
     */
    private String batchNo;
    
    
	public String getMchtPayAmt() {
		return mchtPayAmt;
	}

	public void setMchtPayAmt(String mchtPayAmt) {
		this.mchtPayAmt = mchtPayAmt;
	}

	public String getBankPayAmt() {
		return bankPayAmt;
	}

	public void setBankPayAmt(String bankPayAmt) {
		this.bankPayAmt = bankPayAmt;
	}

	public String getUserorgPayAmt() {
		return userorgPayAmt;
	}

	public void setUserorgPayAmt(String userorgPayAmt) {
		this.userorgPayAmt = userorgPayAmt;
	}

	public String getActiveDesc() {
		return activeDesc;
	}

	public void setActiveDesc(String activeDesc) {
		this.activeDesc = activeDesc;
	}

	public Date getTxnTm() {
		return txnTm;
	}

	public void setTxnTm(Date txnTm) {
		this.txnTm = txnTm;
	}

	public String getLogisFeeAmt() {
		return logisFeeAmt;
	}

	public void setLogisFeeAmt(String logisFeeAmt) {
		this.logisFeeAmt = logisFeeAmt;
	}

	public String getChnlFeeFlag() {
		return chnlFeeFlag;
	}

	public void setChnlFeeFlag(String chnlFeeFlag) {
		this.chnlFeeFlag = chnlFeeFlag;
	}

	public String getPlatHbAmt() {
		return platHbAmt;
	}

	public void setPlatHbAmt(String platHbAmt) {
		this.platHbAmt = platHbAmt;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public String getRowCount() {
		return rowCount;
	}

	public void setRowCount(String rowCount) {
		this.rowCount = rowCount;
	}

	public String getCur() {
		return cur;
	}

	public void setCur(String cur) {
		this.cur = cur;
	}

	public String getSubOrderTime() {
		return subOrderTime;
	}

	public void setSubOrderTime(String subOrderTime) {
		this.subOrderTime = subOrderTime;
	}

	public String getSubOrderSsn() {
		return subOrderSsn;
	}

	public void setSubOrderSsn(String subOrderSsn) {
		this.subOrderSsn = subOrderSsn;
	}

	public String getOrderSsn() {
		return orderSsn;
	}

	public void setOrderSsn(String orderSsn) {
		this.orderSsn = orderSsn;
	}

	public String getReqSubOrderSsn() {
		return reqSubOrderSsn;
	}

	public void setReqSubOrderSsn(String reqSubOrderSsn) {
		this.reqSubOrderSsn = reqSubOrderSsn;
	}

	public String getPayStatusUpdateTime() {
		return payStatusUpdateTime;
	}

	public void setPayStatusUpdateTime(String payStatusUpdateTime) {
		this.payStatusUpdateTime = payStatusUpdateTime;
	}

	public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getFundChannel() {
		return fundChannel;
	}

	public void setFundChannel(String fundChannel) {
		this.fundChannel = fundChannel;
	}

	public String getBrandFee() {
		return brandFee;
	}

	public void setBrandFee(String brandFee) {
		this.brandFee = brandFee;
	}

	public String getOpenBrc() {
		return openBrc;
	}

	public void setOpenBrc(String openBrc) {
		this.openBrc = openBrc;
	}

	public String getCommissionAmt() {
		return commissionAmt;
	}

	public void setCommissionAmt(String commissionAmt) {
		this.commissionAmt = commissionAmt;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getRefundAccType() {
		return refundAccType;
	}

	public void setRefundAccType(String refundAccType) {
		this.refundAccType = refundAccType;
	}

	public String getStlStatus() {
		return stlStatus;
	}

	public void setStlStatus(String stlStatus) {
		this.stlStatus = stlStatus;
	}

	public String getChlMerName() {
        return chlMerName;
    }

    public void setChlMerName(String chlMerName) {
        this.chlMerName = chlMerName;
    }

    public String getChlSubMerId() {
        return chlSubMerId;
    }

    public void setChlSubMerId(String chlSubMerId) {
        this.chlSubMerId = chlSubMerId;
    }

    public String getChlSubMerName() {
        return chlSubMerName;
    }

    public void setChlSubMerName(String chlSubMerName) {
        this.chlSubMerName = chlSubMerName;
    }

    public String getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getSetlAmt() {
        return setlAmt;
    }

    public void setSetlAmt(String setlAmt) {
        this.setlAmt = setlAmt;
    }

    public String getSetlFeeAmt() {
        return setlFeeAmt;
    }

    public void setSetlFeeAmt(String setlFeeAmt) {
        this.setlFeeAmt = setlFeeAmt;
    }

    public String getSetlFeeAmtDis() {
        return setlFeeAmtDis;
    }

    public void setSetlFeeAmtDis(String setlFeeAmtDis) {
        this.setlFeeAmtDis = setlFeeAmtDis;
    }

    public String getSetlAcctNo() {
        return setlAcctNo;
    }

    public void setSetlAcctNo(String setlAcctNo) {
        this.setlAcctNo = setlAcctNo;
    }

    public String getSetlAcctName() {
        return setlAcctName;
    }

    public void setSetlAcctName(String setlAcctName) {
        this.setlAcctName = setlAcctName;
    }

    public String getSetlAcctType() {
        return setlAcctType;
    }

    public void setSetlAcctType(String setlAcctType) {
        this.setlAcctType = setlAcctType;
    }

    public String getSetlAcctInstitute2() {
        return setlAcctInstitute2;
    }

    public void setSetlAcctInstitute2(String setlAcctInstitute2) {
        this.setlAcctInstitute2 = setlAcctInstitute2;
    }

    public String getSetlAcctInstituteName2() {
        return setlAcctInstituteName2;
    }

    public void setSetlAcctInstituteName2(String setlAcctInstituteName2) {
        this.setlAcctInstituteName2 = setlAcctInstituteName2;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentSetlAcctNo() {
        return agentSetlAcctNo;
    }

    public void setAgentSetlAcctNo(String agentSetlAcctNo) {
        this.agentSetlAcctNo = agentSetlAcctNo;
    }

    public String getAgentSetlAcctName() {
        return agentSetlAcctName;
    }

    public void setAgentSetlAcctName(String agentSetlAcctName) {
        this.agentSetlAcctName = agentSetlAcctName;
    }

    public String getAgentSetlFeeAmt() {
        return agentSetlFeeAmt;
    }

    public void setAgentSetlFeeAmt(String agentSetlFeeAmt) {
        this.agentSetlFeeAmt = agentSetlFeeAmt;
    }

    public String getTramFeeAmt() {
        return tramFeeAmt;
    }

    public void setTramFeeAmt(String tramFeeAmt) {
        this.tramFeeAmt = tramFeeAmt;
    }

    public String getLocalFeeAmt() {
        return localFeeAmt;
    }

    public void setLocalFeeAmt(String localFeeAmt) {
        this.localFeeAmt = localFeeAmt;
    }

    public String getPagyNo() {
        return pagyNo;
    }

    public void setPagyNo(String pagyNo) {
        this.pagyNo = pagyNo;
    }

    public String getOutAcctNo() {
        return outAcctNo;
    }

    public void setOutAcctNo(String outAcctNo) {
        this.outAcctNo = outAcctNo;
    }

    public String getLocalFeeAcct() {
        return localFeeAcct;
    }

    public void setLocalFeeAcct(String localFeeAcct) {
        this.localFeeAcct = localFeeAcct;
    }

    public String getAgFee() {
        return agFee;
    }

    public void setAgFee(String agFee) {
        this.agFee = agFee;
    }

    public String getInAcctStat() {
        return inAcctStat;
    }

    public void setInAcctStat(String inAcctStat) {
        this.inAcctStat = inAcctStat;
    }

	public String getStlType() {
		return stlType;
	}

	public void setStlType(String stlType) {
		this.stlType = stlType;
	}

	public String getSubStlDate() {
		return subStlDate;
	}

	public void setSubStlDate(String subStlDate) {
		this.subStlDate = subStlDate;
	}

	public String getBankCouponAmt() {
		return bankCouponAmt;
	}

	public void setBankCouponAmt(String bankCouponAmt) {
		this.bankCouponAmt = bankCouponAmt;
	}

	public String getBankHbAmt() {
		return bankHbAmt;
	}

	public void setBankHbAmt(String bankHbAmt) {
		this.bankHbAmt = bankHbAmt;
	}

	public String getLogisFee() {
		return logisFee;
	}

	public void setLogisFee(String logisFee) {
		this.logisFee = logisFee;
	}

	public String getLogisType() {
		return logisType;
	}

	public void setLogisType(String logisType) {
		this.logisType = logisType;
	}

	public String getPointDedcutAmt() {
		return pointDedcutAmt;
	}

	public void setPointDedcutAmt(String pointDedcutAmt) {
		this.pointDedcutAmt = pointDedcutAmt;
	}

	public String getLogisPartnerCode() {
		return logisPartnerCode;
	}

	public void setLogisPartnerCode(String logisPartnerCode) {
		this.logisPartnerCode = logisPartnerCode;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public String getSubStlStatus() {
		return subStlStatus;
	}

	public void setSubStlStatus(String subStlStatus) {
		this.subStlStatus = subStlStatus;
	}

	

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return "BthMerInAccDtl{" +
				"chlMerName='" + chlMerName + '\'' +
				", chlSubMerId='" + chlSubMerId + '\'' +
				", chlSubMerName='" + chlSubMerName + '\'' +
				", txnAmt='" + txnAmt + '\'' +
				", setlAmt='" + setlAmt + '\'' +
				", setlFeeAmt='" + setlFeeAmt + '\'' +
				", setlFeeAmtDis='" + setlFeeAmtDis + '\'' +
				", setlAcctNo='" + setlAcctNo + '\'' +
				", setlAcctName='" + setlAcctName + '\'' +
				", setlAcctType='" + setlAcctType + '\'' +
				", setlAcctInstitute2='" + setlAcctInstitute2 + '\'' +
				", setlAcctInstituteName2='" + setlAcctInstituteName2 + '\'' +
				", agentId='" + agentId + '\'' +
				", agentName='" + agentName + '\'' +
				", agentSetlAcctNo='" + agentSetlAcctNo + '\'' +
				", agentSetlAcctName='" + agentSetlAcctName + '\'' +
				", agentSetlFeeAmt='" + agentSetlFeeAmt + '\'' +
				", tramFeeAmt='" + tramFeeAmt + '\'' +
				", localFeeAmt='" + localFeeAmt + '\'' +
				", pagyNo='" + pagyNo + '\'' +
				", outAcctNo='" + outAcctNo + '\'' +
				", localFeeAcct='" + localFeeAcct + '\'' +
				", agFee='" + agFee + '\'' +
				", inAcctStat='" + inAcctStat + '\'' +
				", orderType='" + orderType + '\'' +
				", stlStatus='" + stlStatus + '\'' +
				", refundAccType='" + refundAccType + '\'' +
				", stlType='" + stlType + '\'' +
				", subStlDate='" + subStlDate + '\'' +
				", commissionAmt='" + commissionAmt + '\'' +
				", bankCouponAmt='" + bankCouponAmt + '\'' +
				", bankHbAmt='" + bankHbAmt + '\'' +
				", logisFee='" + logisFee + '\'' +
				", logisType='" + logisType + '\'' +
				", logisPartnerCode='" + logisPartnerCode + '\'' +
				", pointDedcutAmt='" + pointDedcutAmt + '\'' +
				", brandFee='" + brandFee + '\'' +
				", openBrc='" + openBrc + '\'' +
				", txnType='" + txnType + '\'' +
				", fundChannel='" + fundChannel + '\'' +
				", updateDate='" + updateDate + '\'' +
				", subStlStatus='" + subStlStatus + '\'' +
				", createDate='" + createDate + '\'' +
				", subOrderTime='" + subOrderTime + '\'' +
				", subOrderSsn='" + subOrderSsn + '\'' +
				", orderSsn='" + orderSsn + '\'' +
				", reqSubOrderSsn='" + reqSubOrderSsn + '\'' +
				", payStatusUpdateTime='" + payStatusUpdateTime + '\'' +
				", cur='" + cur + '\'' +
				", rowCount='" + rowCount + '\'' +
				", payChannel='" + payChannel + '\'' +
				", platHbAmt='" + platHbAmt + '\'' +
				", chnlFeeFlag='" + chnlFeeFlag + '\'' +
				", logisFeeAmt='" + logisFeeAmt + '\'' +
				", txnTm=" + txnTm +
				", mchtPayAmt='" + mchtPayAmt + '\'' +
				", bankPayAmt='" + bankPayAmt + '\'' +
				", userorgPayAmt='" + userorgPayAmt + '\'' +
				", activeDesc='" + activeDesc + '\'' +
				", batchNo='" + batchNo + '\'' +
				super.toString()+
				'}';
	}
}