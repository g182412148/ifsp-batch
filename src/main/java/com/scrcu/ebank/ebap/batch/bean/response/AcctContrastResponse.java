package com.scrcu.ebank.ebap.batch.bean.response;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.scrcu.ebank.ebap.common.beans.CommonResponse;
/**
 * 名称：〈通道对账Response〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月20日 <br>
 * 作者：lijingbo <br>
 * 说明：<br>
 */
public class AcctContrastResponse extends CommonResponse {
    private List<Object> list;

	public List<Object> getList() {
		return list;
	}

	public void setList(List<Object> list) {
		this.list = list;
	}
	
}
