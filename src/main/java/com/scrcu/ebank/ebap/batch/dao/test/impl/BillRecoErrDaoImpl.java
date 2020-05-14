package com.scrcu.ebank.ebap.batch.dao.test.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.BillRecoErr;
import com.scrcu.ebank.ebap.batch.bean.mapper.BillRecoErrMapper;
import com.scrcu.ebank.ebap.batch.dao.test.BillRecoErrDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public class BillRecoErrDaoImpl extends BaseBatisDao implements BillRecoErrDao {

	private final Class<BillRecoErrMapper> mapper = BillRecoErrMapper.class;

	@Override
	public int insert(BillRecoErr record) {
		return getSqlSession().getMapper(mapper).insert(record);
	}

	@Override
	public int insertBatch(List<BillRecoErr> recordList) {
		if(recordList == null || recordList.isEmpty()){
			return 0;
		}else {
			for(BillRecoErr record : recordList){
				getSqlSession().getMapper(mapper).insert(record);
			}
			return recordList.size();
		}
	}

	@Override
	public int clear(Date recoDate, String chnlNo) {
		return getSqlSession().getMapper(mapper).clear(recoDate, chnlNo);
	}

	@Override
	public int insertWxErrResult(Date recoDate) {
		return getSqlSession().getMapper(mapper).insertWxErrResult(recoDate);
	}

	@Override
	public int insertAliErrResult(Date recoDate) {
		return getSqlSession().getMapper(mapper).insertAliErrResult(recoDate);
	}

	@Override
	public int insertIBankErrResult(Date recoDate) {
		return getSqlSession().getMapper(mapper).insertIBankErrResult(recoDate);
	}

	@Override
	public int insertUnionQrcErrResult(Date recoDate) {
		return getSqlSession().getMapper(mapper).insertUnionQrcErrResult(recoDate);
	}

	@Override
	public int insertUnionAllErrResult(Date recoDate) {
		return getSqlSession().getMapper(mapper).insertUnionAllErrResult(recoDate);
	}

}
