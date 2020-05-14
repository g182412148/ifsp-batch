package com.scrcu.ebank.ebap.batch.bean.response;

import com.scrcu.ebank.ebap.common.beans.CommonResponse;
/**
 * 名称：〈第三方手续费对比Response〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月20日 <br>
 * 作者：lijingbo <br>
 * 说明：<br>
 */
public class FeeContrastResponse extends CommonResponse{
	private String resultFlag;//0对比成功1对比失败

	public String getResultFlag() {
		return resultFlag;
	}

	public void setResultFlag(String resultFlag) {
		this.resultFlag = resultFlag;
	}

}
