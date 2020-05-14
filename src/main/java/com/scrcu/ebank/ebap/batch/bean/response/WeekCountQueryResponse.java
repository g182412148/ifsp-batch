package com.scrcu.ebank.ebap.batch.bean.response;

import java.util.List;

import com.scrcu.ebank.ebap.batch.bean.dto.WeekDay;

/**
 * 名称：每周自然日交易金额走势Response〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 说明：<br>
 */
public class WeekCountQueryResponse extends CommRegiResponse {
	
	private List<WeekDay> weekDays;

	public List<WeekDay> getWeekDays() {
		return weekDays;
	}

	public void setWeekDays(List<WeekDay> weekDays) {
		this.weekDays = weekDays;
	}
	
}
