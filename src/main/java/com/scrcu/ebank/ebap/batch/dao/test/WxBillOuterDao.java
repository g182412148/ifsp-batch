package com.scrcu.ebank.ebap.batch.dao.test;

import com.scrcu.ebank.ebap.batch.bean.dto.WxBillOuter;

import java.util.Date;
import java.util.List;

/**
 * 微信第三方账单DAO
 */
public interface WxBillOuterDao {

	/**
     * 批量插入
	 * @param recordList
     * @return
     */
	int insertBatch(List<WxBillOuter> recordList);

	/**
	 * 根据流水号查询
	 * @param txnSsn
	 * @return
	 */
    WxBillOuter queryByIdAndDate(String txnSsn, Date recoDate, Date dubiousDate);

	/**
	 * 根据流水号查询
	 * @param txnSsn
	 * @return
	 */
	WxBillOuter queryById(String txnSsn);

	/**
	 * 更新
	 * @param updBean
	 * @return
	 */
	int updateById(WxBillOuter updBean);

	/**
	 * 查询指定对账日期的未对账数据
	 * @param recoDate
	 */
	List<WxBillOuter> queryNotReco(Date recoDate);

	/**
	 * 恢复指定对账日期的数据
	 * @param recoDate
	 */
	int recovery(Date recoDate);

	/**
	 * 恢复前一天可疑的数据
	 * @param dubiousDate
	 */
	int recoveryDubious(Date dubiousDate);

	/**
	 * 清空指定对账日期的数据
	 * @param date
	 * @return
	 */
	int clear(Date date);

    /**
     * 查询对账日期下交易状态为撤销的,将其原交易的对账状态更新为无需参与对账
     * @param recoDate
     * @return
     */
    int updateSrcByRevoked(Date recoDate);

	/**
	 * 根据微信对账结果更新三方对账状态
	 * @param recoDate
	 * @return
	 */
	int updateByResult(Date recoDate);
}
