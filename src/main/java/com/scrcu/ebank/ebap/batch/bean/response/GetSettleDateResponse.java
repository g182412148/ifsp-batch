package com.scrcu.ebank.ebap.batch.bean.response;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.scrcu.ebank.ebap.batch.bean.vo.DictVo;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

/**
 * 名称：〈数据字典响应报文〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月12日 <br>
 * 作者：qzhang <br>
 * 说明：<br>
 */
public class GetSettleDateResponse extends CommonResponse {

    @NotEmpty(message = "字典信息列表不能为空")
    private String date;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

    
}
