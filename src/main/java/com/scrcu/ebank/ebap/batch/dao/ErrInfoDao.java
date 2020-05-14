package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.ErrInfo;

import java.util.Date;
import java.util.List;

public interface ErrInfoDao {

    /**
     *  获取微信差错详情
     * @param recoDate
     * @return
     */
    List<ErrInfo> getWxErrInfo(Date recoDate);

    /**
     *  获取支付宝差错详情
     * @param recoDate
     * @return
     */
    List<ErrInfo> getAliErrInfo(Date recoDate);

    /**
     *  获取银联差错详情
     * @param recoDate
     * @return
     */
    List<ErrInfo> getUnionErrInfo(Date recoDate);

    /**
     * 获取需要推送的差错数据
     * @return
     */
    List<ErrInfo> getErrFileInfo();

    int insertErrInfoBatch(List<ErrInfo> list);

    /**
     * 批量修改差错详情
     * @param list
     * @return
     */
    int updateErrInfoBatch(List<ErrInfo> list);


}
