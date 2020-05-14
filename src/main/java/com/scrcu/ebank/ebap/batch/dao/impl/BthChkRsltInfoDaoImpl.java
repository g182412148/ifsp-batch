package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.BthChkRsltInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthChkRsltInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.BthChkRsltInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
/**
 *名称：<通道流水抽取Impl> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Repository("BthChkRsltInfoDao")
public class BthChkRsltInfoDaoImpl extends BaseBatisDao implements BthChkRsltInfoDao {
	private final Class<BthChkRsltInfoMapper> bthChkRsltInfoMapper=BthChkRsltInfoMapper.class;
	
	@Override
	public List<BthChkRsltInfo> queryByPagySysNoAndDate(String pagySysNo, String settleDate) {
		return super.getSqlSession().getMapper(bthChkRsltInfoMapper).queryByPagySysNoAndDate(pagySysNo,settleDate);
	}

    @Override
    public List<BthChkRsltInfo> queryByStlmDate(String settleDate) {
        return getSqlSession().getMapper(bthChkRsltInfoMapper).queryByStlmDate(settleDate);
    }

    @Override
    public BthChkRsltInfo selectByPrimaryKey(String pagyTxnSsn) {
        return getSqlSession().getMapper(bthChkRsltInfoMapper).selectByPrimaryKey(pagyTxnSsn);
    }
    
    @Override
	public List<BthChkRsltInfo> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}
	
	@Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public BthChkRsltInfo selectOne(String statement, Map<String, Object> parameter) {
		return  getSqlSession().selectOne(statement, parameter);
	}
    
	@Override
	public int deleteByStlmDateAndPagySysNo(String settleDate, String pagySysNo) {
		return super.getSqlSession().getMapper(bthChkRsltInfoMapper).deleteByStlmDateAndPagySysNo(pagySysNo,settleDate);
	}

	@Override
	public int insert(BthChkRsltInfo record) {
		return super.getSqlSession().getMapper(bthChkRsltInfoMapper).insertSelective(record);
	}

	@Override
	public int update(String statement, Map<String, Object> parameter) {
		return getSqlSession().update(statement, parameter);
	}

	@Override
	public int deleteAliByStlmDateAndPagySysNo(String settleDate, String pagySysNo) {
		return super.getSqlSession().getMapper(bthChkRsltInfoMapper).deleteAliByStlmDateAndPagySysNo(pagySysNo,settleDate);
	}

	@Override
	public int updateByPrimaryKeySelective(BthChkRsltInfo bthChkRsltInfo) {
		return super.getSqlSession().getMapper(bthChkRsltInfoMapper).updateByPrimaryKeySelective(bthChkRsltInfo);
	}

    @Override
    public void deleteByPrimaryKey(String pagyPayTxnSsn) {
        getSqlSession().getMapper(bthChkRsltInfoMapper).deleteByPrimaryKey(pagyPayTxnSsn);
    }

	@Override
	public int delete(String statement, Map<String, Object> parameter) {
		return getSqlSession().delete(statement, parameter);
	}

	@Override
	public int insertBatch(List<BthChkRsltInfo> recordList) {
		if(recordList == null || recordList.isEmpty()){
			return 0;
		}
		return getSqlSession().getMapper(bthChkRsltInfoMapper).insertBatch(recordList);
	}

	@Override
	public int updateByOrderSsnSt(BthChkRsltInfo bthChkRsltInfo) {
		return getSqlSession().getMapper(bthChkRsltInfoMapper).updateByOrderSsnSt(bthChkRsltInfo);
	}

	@Override
	public List<BthChkRsltInfo> selectListByStlSt(String chkSuccDt, String stlmSt) {
		return getSqlSession().getMapper(bthChkRsltInfoMapper).selectListByStlSt(chkSuccDt, stlmSt);
	}

	@Override
	public List<BthChkRsltInfo> selectChkSuccOrderByDateByRange(String chkSuccDt, int minIndex, int maxIndex) {
		return getSqlSession().getMapper(bthChkRsltInfoMapper).selectChkSuccOrderByDateByRange(chkSuccDt, minIndex, maxIndex);
	}

	@Override
	public List<BthChkRsltInfo> selectChkSuccOrderByDateByRangeTemp(String chkSuccDt, int minIndex, int maxIndex) {
		return getSqlSession().getMapper(bthChkRsltInfoMapper).selectChkSuccOrderByDateByRangeTemp(chkSuccDt, minIndex, maxIndex);
	}

	@Override
	public int insertWxChkResult(Date recoDate) {
		return getSqlSession().getMapper(bthChkRsltInfoMapper).insertWxChkResult(recoDate);
	}

	@Override
	public int insertAliChkResult(Date recoDate) {
		return getSqlSession().getMapper(bthChkRsltInfoMapper).insertAliChkResult(recoDate);
	}

	@Override
	public int insertIBankChkResult(Date recoDate) {
		return getSqlSession().getMapper(bthChkRsltInfoMapper).insertIBankChkResult(recoDate);
	}

	@Override
	public int insertUnionQrcChkResult(Date recoDate) {
		return getSqlSession().getMapper(bthChkRsltInfoMapper).insertUnionQrcChkResult(recoDate);
	}

	@Override
	public int insertUnionAllChkResult(Date recoDate) {
		return getSqlSession().getMapper(bthChkRsltInfoMapper).insertUnionAllChkResult(recoDate);
	}

}
