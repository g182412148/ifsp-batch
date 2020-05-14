package com.scrcu.ebank.ebap.batch.bean.request;

import org.hibernate.validator.constraints.NotEmpty;

import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;

public class BillDownloadRequest extends CommonRequest{
	//通道服务码,请求系统编号,通道接入类型,通道机构接入编号,对账单日期,账单类型
	@NotEmpty(message = "通道系统编号不能为空")
	private String pagySysNo;//PAGY_SYS_NO
	@NotEmpty(message = "通道编号不能为空")
	private String pagyNo;//PAGY_NO
	@NotEmpty(message = "清算日期不能为空")
	private String settleDate;
	
	
	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}
	
	public String getPagyNo() {
		return pagyNo;
	}

	public void setPagyNo(String pagyNo) {
		this.pagyNo = pagyNo;
	}

	public String getPagySysNo() {
		return pagySysNo;
	}

	public void setPagySysNo(String pagySysNo) {
		this.pagySysNo = pagySysNo;
	}

	@Override
	public void valid() throws IfspValidException {
	}

}
