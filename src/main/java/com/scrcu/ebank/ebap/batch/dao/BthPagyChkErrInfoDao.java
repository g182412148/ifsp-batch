package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.BthPagyChkErrInfo;

public interface BthPagyChkErrInfoDao {

	List<BthPagyChkErrInfo> selectByStateAndDate(String settleDate, String state, String pagySysNo);

	int deleteByPrimaryKey(String chkErrSsn);

	int deleteByStlmDateAndPagySysNo(String settleDate, String pagySysNo);

	int insert(BthPagyChkErrInfo bthBalErrRecord);

    void deleteByChkErrDtAndErrTp04(String settleDate, String errTp04);

    void updByChkErrSsn(String chkErrSsn);

	List<BthPagyChkErrInfo> selectList(String statement, Map<String,Object> map);

    List<BthPagyChkErrInfo> selectByAcctInFlagAndDate(String settleDate);

    int updateByPrimaryKeySelective(BthPagyChkErrInfo record);

    List<BthPagyChkErrInfo> selectByFileInFlagAndAcctInFlag();

}
