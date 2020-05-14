package com.scrcu.ebank.ebap.batch.bean.response;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.PagyTxnInfo;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
/**
 * 名称：〈通道流水抽取Response〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月20日 <br>
 * 作者：lijingbo <br>
 * 说明：<br>
 */
public class SynchronizeDataResponse extends CommonResponse{
	private List<Object> objectList;

	public List<Object> getObjectList() {
		return objectList;
	}

	public void setObjectList(List<Object> objectList) {
		this.objectList = objectList;
	}

	
}
