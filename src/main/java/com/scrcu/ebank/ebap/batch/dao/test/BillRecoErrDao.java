package com.scrcu.ebank.ebap.batch.dao.test;

import com.scrcu.ebank.ebap.batch.bean.dto.BillRecoErr;

import java.util.Date;
import java.util.List;

/**
 * 对账差错表
 */
public interface BillRecoErrDao {

	/**
	 * 插入
	 * @param record
	 * @return
	 */
	int insert(BillRecoErr record);

	/**
	 * 批量插入
	 * @param recordList
	 * @return
	 */
	int insertBatch(List<BillRecoErr> recordList);

	/**
	 * 清楚指定日期 和 渠道的差错数据
	 * @param recoDate
	 */
    int clear(Date recoDate, String chnlNo);

	/**
	 * 根据微信对账结果插入差错记录
	 * @param recoDate
	 * @return
	 */
	int insertWxErrResult(Date recoDate);

	/**
	 * 根据支付宝对账结果插入差错记录
	 * @param recoDate
	 * @return
	 */
	int insertAliErrResult(Date recoDate);

	/**
	 * 根据本行对账结果插入差错记录
	 * @param recoDate
	 * @return
	 */
	int insertIBankErrResult(Date recoDate);

	/**
	 * 根据银联二维码对账结果插入差错记录
	 * @param recoDate
	 * @return
	 */
	int insertUnionQrcErrResult(Date recoDate);

	/**
	 * 根据银联全渠道对账结果插入差错记录
	 * @param recoDate
	 * @return
	 */
	int insertUnionAllErrResult(Date recoDate);
}
