package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.scrcu.ebank.ebap.batch.bean.dto.BthPagyChkErrInfo;

public interface BthPagyChkErrInfoMapper {
    int deleteByPrimaryKey(String chkErrSsn);

    int insert(BthPagyChkErrInfo record);

    int insertSelective(BthPagyChkErrInfo record);

    BthPagyChkErrInfo selectByPrimaryKey(String chkErrSsn);

    int updateByPrimaryKeySelective(BthPagyChkErrInfo record);

    int updateByPrimaryKey(BthPagyChkErrInfo record);

	void deletePagyChkErrInfoBySettleDateAndErrTp(@Param("settleDate")String settleDate, @Param("errTp")String errTp);
	/**
	 * 根据时间状态查询差错信息
	 * @param settleDate
	 * @param state
	 * @param pagySysNo 
	 * @return
	 */
	List<BthPagyChkErrInfo> selectByStateAndDate(@Param("settleDate")String settleDate, @Param("state")String state, @Param("pagySysNo")String pagySysNo);
	/**
	 * 根据日期和通道系统编号删除信息
	 * @param settleDate
	 * @param pagySysNo
	 * @return
	 */
	int deleteByStlmDateAndPagySysNo(@Param("settleDate")String settleDate, @Param("pagySysNo")String pagySysNo);

    /**
     * 根据差错时间与差错类型删除差错表失败
     * @param settleDate
     * @param errTp04
     */
    void deleteByChkErrDtAndErrTp04(@Param("settleDate")String settleDate, @Param("errTp04")String errTp04);

    /**
     * 根据新建时间入账状态查询差错信息
     */
    List<BthPagyChkErrInfo> selectByAcctInFlagAndDate(String settleDateStart);

    /**
     * 根据是否已生成文件状态查询差错信息
     */
    List<BthPagyChkErrInfo> selectByFileInFlagAndAcctInFlag();


	/**
	 * 根据差错流水更新差错表
	 * @param chkErrSsn
	 */
	void updByChkErrSsn(String chkErrSsn);

    /**
     * 根据日期,类型与通道系统编号删除差错
     * @param chkErrDt
     * @param errTp
     * @param pagySysNo
     * @return
     */
    int deleteByDateErrTpAndPagySysNo(@Param("chkErrDt")Date chkErrDt, @Param("errTp")String errTp, @Param("pagySysNo")String pagySysNo);
}