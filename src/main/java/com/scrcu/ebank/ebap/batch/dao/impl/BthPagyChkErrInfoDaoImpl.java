package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;

import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.BthPagyChkErrInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthPagyChkErrInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.BthPagyChkErrInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
/**
 *名称：<差错表查询Impl> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/30 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Slf4j
@Repository("BthPagyChkErrInfoDao")
public class BthPagyChkErrInfoDaoImpl extends BaseBatisDao implements BthPagyChkErrInfoDao {
	private Class<BthPagyChkErrInfoMapper> bthPagyChkErrInfoMapper=BthPagyChkErrInfoMapper.class;
	
	@Override
	public List<BthPagyChkErrInfo> selectByStateAndDate(String settleDate, String state,String pagySysNo) {
		return super.getSqlSession().getMapper(bthPagyChkErrInfoMapper).selectByStateAndDate(settleDate,state,pagySysNo);
	}

	@Override
	public int deleteByPrimaryKey(String chkErrSsn) {
		return super.getSqlSession().getMapper(bthPagyChkErrInfoMapper).deleteByPrimaryKey(chkErrSsn);
	}

	@Override
	public int deleteByStlmDateAndPagySysNo(String settleDate, String pagySysNo) {
		return super.getSqlSession().getMapper(bthPagyChkErrInfoMapper).deleteByStlmDateAndPagySysNo(settleDate,pagySysNo);
	}

	@Override
	public int insert(BthPagyChkErrInfo bthBalErrRecord) {
		return super.getSqlSession().getMapper(bthPagyChkErrInfoMapper).insertSelective(bthBalErrRecord);
	}

    @Override
    public void deleteByChkErrDtAndErrTp04(String settleDate, String errTp04) {
        try {
            super.getSqlSession().getMapper(bthPagyChkErrInfoMapper).deleteByChkErrDtAndErrTp04(settleDate,errTp04);
        } catch (Exception e) {
            log.error("根据差错时间与差错类型删除差错表失败!!!",e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"根据差错时间与差错类型删除差错表失败");
        }
    }

	@Override
	public List<BthPagyChkErrInfo> selectList(String statement, Map<String, Object> params) {
		return getSqlSession().selectList(statement, params);
	}


	@Override
	public void updByChkErrSsn(String chkErrSsn) {
		getSqlSession().getMapper(bthPagyChkErrInfoMapper).updByChkErrSsn(chkErrSsn);
	}

	@Override
	public List<BthPagyChkErrInfo> selectByAcctInFlagAndDate(String settleDate) {
		return super.getSqlSession().getMapper(bthPagyChkErrInfoMapper).selectByAcctInFlagAndDate(settleDate);
	}

	@Override
	public int updateByPrimaryKeySelective(BthPagyChkErrInfo record) {
		return super.getSqlSession().getMapper(bthPagyChkErrInfoMapper).updateByPrimaryKeySelective(record);
	}

	@Override
	public List<BthPagyChkErrInfo> selectByFileInFlagAndAcctInFlag() {
		return super.getSqlSession().getMapper(bthPagyChkErrInfoMapper).selectByFileInFlagAndAcctInFlag();
	}


}
