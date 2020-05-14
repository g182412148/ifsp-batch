package com.scrcu.ebank.ebap.batch.dao;/**
 * Created by Administrator on 2019-05-10.
 */


import com.scrcu.ebank.ebap.batch.bean.dto.UnionBillLocal;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 名称：〈名称〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019-05-10 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
public interface UnionBillLocalDao {
    /**
     * 查询指定日期中需要处理的数据量
     * @param recoDate 对账日期
     * @return
     */
    int count(Date recoDate, String pagyNo);

    /**
     * 批量插入
     * @param recordList
     * @return
     */
    int insertBatch(List<UnionBillLocal> recordList);

    /**
     * 分页查询询指定日期的数据
     * @param recoDate
     * @param minIndex
     * @param maxIndex
     * @return
     */
    List<UnionBillLocal> queryByRange(Date recoDate, int minIndex, int maxIndex, String pagyNo);

    /**
     * 更新
     * @param updBean
     * @return
     */
    int updateById(UnionBillLocal updBean);

    /**
     * 恢复指定对账日期的数据
     * @param recoDate
     */
    int recovery(Date recoDate, String pagyNo);

    /**
     * 恢复指定对账日期的数据
     * @param recoDate
     */
    int recoveryDubious(Date recoDate, String pagyNo);

    /**
     * 清空指定对账日期的数据
     * @return
     */
    int clear(Date recoDate, String pagyNo);

    /**
     * 从二维码交易流水表复制
     * @param startDate
     * @param endDate
     */
    int copyQrc(Date startDate, Date endDate);

    /**
     * 从全渠道交易流水表复制
     * @param startDate
     * @param endDate
     */
    int copyOnlin(Date startDate, Date endDate);

    List<UnionBillLocal> selectList(String statement, Map<String,Object> map);

    /**
     * 根据银联二维码对账结果更新本地对账状态
     * @param recoDate
     * @return
     */
    int updateByUnionQrcResult(Date recoDate);

    /**
     * 根据银联全渠道对账结果更新本地对账状态
     * @param recoDate
     * @return
     */
    int updateByUnionAllResult(Date recoDate);
}
