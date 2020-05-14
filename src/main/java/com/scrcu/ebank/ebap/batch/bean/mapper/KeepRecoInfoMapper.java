package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.KeepRecoInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.UnionBillLocal;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface KeepRecoInfoMapper {
    int deleteByPrimaryKey(String txnSsn);

    int insert(KeepRecoInfo record);

    int insertSelective(KeepRecoInfo record);

    KeepRecoInfo selectByPrimaryKey(String txnSsn);

    int updateByPrimaryKeySelective(KeepRecoInfo record);

    int updateByPrimaryKey(KeepRecoInfo record);

    /**
     * 恢复指定对账日期的数据
     * @param recoDate
     * @return
     */
    int recovery(@Param("recoDate") Date recoDate);

    /**
     * 恢复指定对账日期的数据
     * @param recoDate
     * @return
     */
    int recoveryDubious(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);


    KeepRecoInfo queryByIdOfDate(@Param("txnSsn")String txnSsn,@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    /**
     * 查询指定日期的未对账数据
     * @param recoDate 对账日期
     * @param dubiousDate 可疑日期
     * @return
     */
    List<KeepRecoInfo> queryNotReco(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    /**
     * 清空指定对账日期的数据
     * @param recoDate
     * @return
     */
    int clear(@Param("recoDate") Date recoDate);

    /**
     * 表复制
     * @param startDate
     * @param endDate
     * @return
     */
    int copy(@Param("startDate")Date startDate, @Param("endDate")Date endDate);

    int count(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate);

    /**
     * 分页查询
     * @param recoDate
     * @return
     */
    List<KeepRecoInfo> queryByRange(@Param("recoDate") Date recoDate, @Param("dubiousDate") Date dubiousDate, @Param("minIndex") int minIndex, @Param("maxIndex") int maxIndex);


}