package com.scrcu.ebank.ebap.batch.bean.request;

import org.hibernate.validator.constraints.NotEmpty;

import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;
/**
 * 名称：〈通道对账Request〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月20日 <br>
 * 作者：lijingbo <br>
 * 说明：<br>
 */
public class AcctContrastRequest extends CommonRequest {
	@NotEmpty(message = "通道系统编号不能为空")
	private String pagySysNo;//PAGY_SYS_NO
	
	@NotEmpty(message = "清算日期不能为空")
	private String settleDate;


	public String getPagySysNo() {
		return pagySysNo;
	}


	public void setPagySysNo(String pagySysNo) {
		this.pagySysNo = pagySysNo;
	}


	public String getSettleDate() {
		return settleDate;
	}


	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}


	@Override
	public void valid() throws IfspValidException {
		// TODO Auto-generated method stub

	}

}
