package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

public class PaySubOrderInfo extends CommonDTO {
    private String subOrderSsn;

    private String subOrderTm;

    private String orderSsn;

    private String subMchtId;

    private String reqSubOrderSsn;

    private String reqSubOrderTm;

    private String orderBidy;

    private String subOrderAmt;

    private String initLocalhostIp;

    private String updLocalhostIp;

    private String sysTraceId;

    private String crtTm;

    private String upTm;

    private String merName;

    private String merLevel;

    private String parentMerNo;

    private String merProType;

    private String merProName;

    private String payAmount;

    private String orderType;

    private String orderStatus;

    private String payStatus;

    private String fundChannel;

    private String oriMerSeqId;

    private String oriDetailsId;

    private String refundAmount;

    private String discountFlag;

    private String orderStatusUpdateTime;

    private String payStatusUpdateTime;

    private String detailsExtend1;

    private String detailsExtend5;

    private String openClientId;

    private String customerId;

    private String transFactTime;
    
    private String merFee;       //行内手续费
    
    private String branchFee;    //渠道手续费
    
    private String logisType;    //物流类型 01-统一物流，02-平台物流
    
    private String logisFee;       //物流费
    
    private String logisPartnerCode;  //物流合作方代码
    
    private String mchtCouponAmt;     //商户营销总金额
    private String  bankCouponAmt;    //行内营销总金额
    private String couopnDesc;        //红包明细信息，Json
    
    //结算相关
    private String settleStatus;      //结算状态
    private String noticeTime;        //通知结算日期
    private String autoSettleFlag;    //通知结算标志(1-无需通知结算自动结算，2-已通过接口通知)
    
    private String logisFeeAmt;             //物流手续费(物流费对应的手续费)
    
    private String mchtPayAmt;        //营销：商户出资

    private String bankPayAmt;        //营销：银行出资

    private String userorgPayAmt;      //营销：用户机构出资

    private String activeDesc;         //营销描述

    private String settleDate;         //结算日期

    private String brandFeeUnion;       //银联品牌服务费，不入库

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

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

	public String getLogisFeeAmt() {
		return logisFeeAmt;
	}

	public void setLogisFeeAmt(String logisFeeAmt) {
		this.logisFeeAmt = logisFeeAmt;
	}

	public String getSettleStatus() {
		return settleStatus;
	}

	public void setSettleStatus(String settleStatus) {
		this.settleStatus = settleStatus;
	}

	public String getNoticeTime() {
		return noticeTime;
	}

	public void setNoticeTime(String noticeTime) {
		this.noticeTime = noticeTime;
	}

	public String getAutoSettleFlag() {
		return autoSettleFlag;
	}

	public void setAutoSettleFlag(String autoSettleFlag) {
		this.autoSettleFlag = autoSettleFlag;
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

	public String getLogisPartnerCode() {
		return logisPartnerCode;
	}

	public void setLogisPartnerCode(String logisPartnerCode) {
		this.logisPartnerCode = logisPartnerCode;
	}

	public String getLogisType() {
		return logisType;
	}

	public void setLogisType(String logisType) {
		this.logisType = logisType;
	}

	public String getLogisFee() {
		return logisFee;
	}

	public void setLogisFee(String logisFee) {
		this.logisFee = logisFee;
	}

	public String getMerFee() {
		return merFee;
	}

	public void setMerFee(String merFee) {
		this.merFee = merFee;
	}

	public String getBranchFee() {
		return branchFee;
	}

	public void setBranchFee(String branchFee) {
		this.branchFee = branchFee;
	}

	public String getSubOrderSsn() {
        return subOrderSsn;
    }

    public void setSubOrderSsn(String subOrderSsn) {
        this.subOrderSsn = subOrderSsn == null ? null : subOrderSsn.trim();
    }

    public String getSubOrderTm() {
        return subOrderTm;
    }

    public void setSubOrderTm(String subOrderTm) {
        this.subOrderTm = subOrderTm == null ? null : subOrderTm.trim();
    }

    public String getOrderSsn() {
        return orderSsn;
    }

    public void setOrderSsn(String orderSsn) {
        this.orderSsn = orderSsn == null ? null : orderSsn.trim();
    }

    public String getSubMchtId() {
        return subMchtId;
    }

    public void setSubMchtId(String subMchtId) {
        this.subMchtId = subMchtId == null ? null : subMchtId.trim();
    }

    public String getReqSubOrderSsn() {
        return reqSubOrderSsn;
    }

    public void setReqSubOrderSsn(String reqSubOrderSsn) {
        this.reqSubOrderSsn = reqSubOrderSsn == null ? null : reqSubOrderSsn.trim();
    }

    public String getReqSubOrderTm() {
        return reqSubOrderTm;
    }

    public void setReqSubOrderTm(String reqSubOrderTm) {
        this.reqSubOrderTm = reqSubOrderTm == null ? null : reqSubOrderTm.trim();
    }

    public String getOrderBidy() {
        return orderBidy;
    }

    public void setOrderBidy(String orderBidy) {
        this.orderBidy = orderBidy == null ? null : orderBidy.trim();
    }

    public String getSubOrderAmt() {
        return subOrderAmt;
    }

    public void setSubOrderAmt(String subOrderAmt) {
        this.subOrderAmt = subOrderAmt == null ? null : subOrderAmt.trim();
    }

    public String getInitLocalhostIp() {
        return initLocalhostIp;
    }

    public void setInitLocalhostIp(String initLocalhostIp) {
        this.initLocalhostIp = initLocalhostIp == null ? null : initLocalhostIp.trim();
    }

    public String getUpdLocalhostIp() {
        return updLocalhostIp;
    }

    public void setUpdLocalhostIp(String updLocalhostIp) {
        this.updLocalhostIp = updLocalhostIp == null ? null : updLocalhostIp.trim();
    }

    public String getSysTraceId() {
        return sysTraceId;
    }

    public void setSysTraceId(String sysTraceId) {
        this.sysTraceId = sysTraceId == null ? null : sysTraceId.trim();
    }

    public String getCrtTm() {
        return crtTm;
    }

    public void setCrtTm(String crtTm) {
        this.crtTm = crtTm == null ? null : crtTm.trim();
    }

    public String getUpTm() {
        return upTm;
    }

    public void setUpTm(String upTm) {
        this.upTm = upTm == null ? null : upTm.trim();
    }

    public String getMerName() {
        return merName;
    }

    public void setMerName(String merName) {
        this.merName = merName == null ? null : merName.trim();
    }

    public String getMerLevel() {
        return merLevel;
    }

    public void setMerLevel(String merLevel) {
        this.merLevel = merLevel == null ? null : merLevel.trim();
    }

    public String getParentMerNo() {
        return parentMerNo;
    }

    public void setParentMerNo(String parentMerNo) {
        this.parentMerNo = parentMerNo == null ? null : parentMerNo.trim();
    }

    public String getMerProType() {
        return merProType;
    }

    public void setMerProType(String merProType) {
        this.merProType = merProType == null ? null : merProType.trim();
    }

    public String getMerProName() {
        return merProName;
    }

    public void setMerProName(String merProName) {
        this.merProName = merProName == null ? null : merProName.trim();
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount == null ? null : payAmount.trim();
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType == null ? null : orderType.trim();
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus == null ? null : orderStatus.trim();
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus == null ? null : payStatus.trim();
    }

    public String getFundChannel() {
        return fundChannel;
    }

    public void setFundChannel(String fundChannel) {
        this.fundChannel = fundChannel == null ? null : fundChannel.trim();
    }

    public String getOriMerSeqId() {
        return oriMerSeqId;
    }

    public void setOriMerSeqId(String oriMerSeqId) {
        this.oriMerSeqId = oriMerSeqId == null ? null : oriMerSeqId.trim();
    }

    public String getOriDetailsId() {
        return oriDetailsId;
    }

    public void setOriDetailsId(String oriDetailsId) {
        this.oriDetailsId = oriDetailsId == null ? null : oriDetailsId.trim();
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount) {
        this.refundAmount = refundAmount == null ? null : refundAmount.trim();
    }

    public String getDiscountFlag() {
        return discountFlag;
    }

    public void setDiscountFlag(String discountFlag) {
        this.discountFlag = discountFlag == null ? null : discountFlag.trim();
    }

    public String getOrderStatusUpdateTime() {
        return orderStatusUpdateTime;
    }

    public void setOrderStatusUpdateTime(String orderStatusUpdateTime) {
        this.orderStatusUpdateTime = orderStatusUpdateTime == null ? null : orderStatusUpdateTime.trim();
    }

    public String getPayStatusUpdateTime() {
        return payStatusUpdateTime;
    }

    public void setPayStatusUpdateTime(String payStatusUpdateTime) {
        this.payStatusUpdateTime = payStatusUpdateTime == null ? null : payStatusUpdateTime.trim();
    }

    public String getDetailsExtend1() {
        return detailsExtend1;
    }

    public void setDetailsExtend1(String detailsExtend1) {
        this.detailsExtend1 = detailsExtend1 == null ? null : detailsExtend1.trim();
    }

    public String getDetailsExtend5() {
        return detailsExtend5;
    }

    public void setDetailsExtend5(String detailsExtend5) {
        this.detailsExtend5 = detailsExtend5 == null ? null : detailsExtend5.trim();
    }

    public String getOpenClientId() {
        return openClientId;
    }

    public void setOpenClientId(String openClientId) {
        this.openClientId = openClientId == null ? null : openClientId.trim();
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId == null ? null : customerId.trim();
    }

    public String getTransFactTime() {
        return transFactTime;
    }

    public void setTransFactTime(String transFactTime) {
        this.transFactTime = transFactTime == null ? null : transFactTime.trim();
    }

    public String getBrandFeeUnion() {
        return brandFeeUnion;
    }

    public void setBrandFeeUnion(String brandFeeUnion) {
        this.brandFeeUnion = brandFeeUnion;
    }
}