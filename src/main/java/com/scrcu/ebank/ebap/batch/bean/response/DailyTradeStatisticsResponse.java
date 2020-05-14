package com.scrcu.ebank.ebap.batch.bean.response;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.DailyStatis;

/**
 * 名称：〈分店交易日统计Response〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 说明：<br>
 */
public class DailyTradeStatisticsResponse extends CommRegiResponse {
	
	private String subMchtName;
	
	private List<DailyStatis> dailyStatis;

	public String getSubMchtName() {
		return subMchtName;
	}

	public void setSubMchtName(String subMchtName) {
		this.subMchtName = subMchtName;
	}

	public List<DailyStatis> getDailyStatis() {
		return dailyStatis;
	}

	public void setDailyStatis(List<DailyStatis> dailyStatis) {
		this.dailyStatis = dailyStatis;
	}


	
	
	
}
