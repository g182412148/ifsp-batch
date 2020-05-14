package com.scrcu.ebank.ebap.batch.bean.dto;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

/*
* Copyright (C), 2012-2013, 上海睿民互联网科技有限公司
* FileName: PagnParam.java
* Author: chenshuqin
* Date: 2018-6-5 下午04:46:23
* Description: 分页参数类
* History: //修改记录
* <author> <time> <version> <desc>
*  chenshuqin 2018-06-05 16:46:23 0.1
*/
public class PagnParam implements Serializable {
	// 页码
	@NotBlank(message="页码不能为空！！")
	private int pageNo;
	// 条数
	@NotBlank(message="条数不能为空！！")
	private int pageSize;

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}
