package com.scrcu.ebank.ebap.batch.bean.request;

import org.hibernate.validator.constraints.NotEmpty;

import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.exception.IfspValidException;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class QueryDailyBillRequest extends CommRegiRequest{
	
	@NotEmpty(message = "日期不能为空")
	private String qryDate;

	// 页码
	@NotEmpty(message="页码不能为空！！")
	private String pageNo;
	// 条数
	@NotEmpty(message="条数不能为空！！")
	private String pageSize;

	public String getPageNo() {
		return pageNo;
	}

	public void setPageNo(String pageNo) {
		this.pageNo = pageNo;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public String getQryDate() {
		return qryDate;
	}

	public void setQryDate(String qryDate) {
		this.qryDate = qryDate;
	}

	@Override
	public void valid() throws IfspValidException {
		// TODO Auto-generated method stub
		if (Integer.parseInt(getPageSize()) <= 0) {
			throw new IfspValidException(RespConstans.RESP_NONE_PAGE_SIZE.getCode(), RespConstans.RESP_NONE_PAGE_SIZE.getDesc());
		}
		if (Integer.parseInt(getPageNo()) <= 0) {
			throw new IfspValidException(RespConstans.RESP_NONE_PAGE_NO.getCode(), RespConstans.RESP_NONE_PAGE_NO.getDesc());
		}
	}

}
