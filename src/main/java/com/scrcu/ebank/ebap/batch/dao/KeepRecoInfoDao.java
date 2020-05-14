package com.scrcu.ebank.ebap.batch.dao;/**
 * Created by Administrator on 2019-05-19.
 */

import com.scrcu.ebank.ebap.batch.bean.dto.KeepRecoInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.UnionBillOuter;

import java.util.Date;
import java.util.List;

/**
 * 名称：〈名称〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019-05-19 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
public interface KeepRecoInfoDao {

    /**
     * 恢复指定对账日期的数据
     * @param recoDate
     */
    int recovery(Date recoDate);
    /**
     * 恢复指定对账日期的数据
     * @param recoDate
     */
    int recoveryDubious(Date recoDate);

    /**
     * 更新
     * @param updBean
     * @return
     */
    int updateById(KeepRecoInfo updBean);

    /**
     * 根据流水号查询
     * @param txnSsn
     * @return
     */
    KeepRecoInfo queryByIdOfDate(String txnSsn, Date recoDate, Date doubtDate);

    /**
     * 查询指定对账日期的未对账数据
     * @param recoDate
     */
    List<KeepRecoInfo> queryNotReco(Date recoDate);

    /**
     * 查询指定日期中需要处理的数据量
     * @param recoDate 对账日期
     * @return
     */
    int count(Date recoDate);

    /**
     * 批量更新
     * @param recordList
     * @return
     */
    int updBatch(List<KeepRecoInfo> recordList);

    /**
     * 分页查询询指定日期的数据
     * @param recoDate
     * @param minIndex
     * @param maxIndex
     * @return
     */
    List<KeepRecoInfo> queryByRange(Date recoDate, int minIndex, int maxIndex);

    /**
     * 清空指定对账日期的数据
     * @return
     */
    int clear(Date recoDate);

    /**
     * 从记账流水表复制
     * @param startDate
     * @param endDate
     */
    int copy(Date startDate, Date endDate);
}
