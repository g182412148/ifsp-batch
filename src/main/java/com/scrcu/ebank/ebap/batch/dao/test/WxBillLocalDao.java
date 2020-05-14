package com.scrcu.ebank.ebap.batch.dao.test;

import com.scrcu.ebank.ebap.batch.bean.dto.WxBillLocal;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 微信本地账单DAO
 */
public interface WxBillLocalDao {

	/**
	 * 查询指定日期中需要处理的数据量
	 * @param recoDate 对账日期
	 * @return
	 */
	int count(Date recoDate);

	/**
	 * 批量插入
	 * @param recordList
	 * @return
	 */
	int insertBatch(List<WxBillLocal> recordList);

	/**
	 * 分页查询询指定日期的数据
	 * @param recoDate
	 * @param minIndex
	 * @param maxIndex
	 * @return
	 */
	List<WxBillLocal> queryByRange(Date recoDate, int minIndex, int maxIndex);

	/**
	 * 更新
	 * @param updBean
	 * @return
	 */
	int updateById(WxBillLocal updBean);

	/**
	 * 恢复指定对账日期的数据
	 * @param recoDate
	 */
	int recovery(Date recoDate);

	/**
	 * 恢复前日可疑明细
	 * @param dubiousDate
	 * @return
	 */
	int recoveryDubious(Date dubiousDate);

	/**
	 * 清空指定对账日期的数据
	 * @return
	 */
	int clear(Date recoDate);

	/**
	 * 从交易流水表复制
	 * @param startDate
	 * @param endDate
	 */
    int copy(Date startDate, Date endDate);


    List<WxBillLocal> selectList(String statement, Map<String,Object> map);

	/**
	 * 根据微信对账结果更新本地对账状态
	 * @param recoDate
	 * @return
	 */
	int updateByResult(Date recoDate);
}
