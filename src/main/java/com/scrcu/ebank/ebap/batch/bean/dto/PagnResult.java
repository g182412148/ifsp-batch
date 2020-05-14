package com.scrcu.ebank.ebap.batch.bean.dto;
/*
* Copyright (C), 2012-2013, 上海睿民互联网科技有限公司
* FileName: PagnResult.java
* Author: chenshuqin
* Date: 2018-6-5 下午04:46:23
* Description: 分页结果
* History: //修改记录
* <author> <time> <version> <desc>
*  chenshuqin 2018-06-05 16:46:23 0.1
*/
public class PagnResult {
	// 记录总数
	private long recordTotal;
	// 页码
	private int pageNo;
	// 页数
	private int pageCount;

	public long getRecordTotal() {
		return recordTotal;
	}

	public void setRecordTotal(long recordTotal) {
		this.recordTotal = recordTotal;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

}
