package com.scrcu.ebank.ebap.batch.bean.request;

import org.hibernate.validator.constraints.NotEmpty;

import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;

public class KeepAccountRequest extends CommonRequest{

	@NotEmpty(message = "日期不能为空")
	private String settleDate;
	@NotEmpty(message = "记账金额不能为空")
	private String transAmt;
	@NotEmpty(message = "转入账户不能为空")
	private String inAccNo;
	@NotEmpty(message = "转出账户不能为空")
	private String outAccNo;
	@NotEmpty(message = "交易类型不能为空")
	private String transType;
	@NotEmpty(message = "订单号不能为空")
	private String orderSsn;
	@NotEmpty(message = "订单时间不能为空")
	private String orderTm;
	@NotEmpty(message = "转入方名称不能为空")
	private String inAccName;
	@NotEmpty(message = "转出方名称不能为空")
	private String outAccName;
	@NotEmpty(message = "通道系统编号不能为空")
	private String pagySysNo;
	@NotEmpty(message = "交易描述不能为空")
	private String txnDesc;


	public String getTxnDesc() {
		return txnDesc;
	}

	public void setTxnDesc(String txnDesc) {
		this.txnDesc = txnDesc;
	}

	public String getOrderTm() {
		return orderTm;
	}

	public void setOrderTm(String orderTm) {
		this.orderTm = orderTm;
	}

	public String getPagySysNo() {
		return pagySysNo;
	}

	public void setPagySysNo(String pagySysNo) {
		this.pagySysNo = pagySysNo;
	}

	public String getInAccName() {
		return inAccName;
	}

	public void setInAccName(String inAccName) {
		this.inAccName = inAccName;
	}

	public String getOutAccName() {
		return outAccName;
	}

	public void setOutAccName(String outAccName) {
		this.outAccName = outAccName;
	}

	public String getOrderSsn() {
		return orderSsn;
	}

	public void setOrderSsn(String orderSsn) {
		this.orderSsn = orderSsn;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getTransAmt() {
		return transAmt;
	}

	public void setTransAmt(String transAmt) {
		this.transAmt = transAmt;
	}

	public String getInAccNo() {
		return inAccNo;
	}

	public void setInAccNo(String inAccNo) {
		this.inAccNo = inAccNo;
	}

	public String getOutAccNo() {
		return outAccNo;
	}

	public void setOutAccNo(String outAccNo) {
		this.outAccNo = outAccNo;
	}

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	@Override
	public void valid() throws IfspValidException {
	}
}
