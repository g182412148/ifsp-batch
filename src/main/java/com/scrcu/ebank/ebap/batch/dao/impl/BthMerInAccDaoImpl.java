package com.scrcu.ebank.ebap.batch.dao.impl;

import com.github.pagehelper.PageHelper;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAcc;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccSumInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccSumInfoExtend;
import com.scrcu.ebank.ebap.batch.bean.dto.PagnParam;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccVo;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthMerInAccMapper;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class BthMerInAccDaoImpl extends BaseBatisDao implements BthMerInAccDao {
    private final Class<BthMerInAccMapper> bthMerInAccMapperMap = BthMerInAccMapper.class;


	@Override
	public BthMerInAcc queryMerInAccByMchtAndInAcctDate(String mchtId, String qryDate) {
		return getSqlSession().getMapper(bthMerInAccMapperMap).queryMerInAccByMchtAndInAcctDate(mchtId,qryDate);
	}

    @Override
    public List<BthMerInAcc> selectByInAcctType(String inAcctType) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).selectByInAcctType(inAcctType);

    }


    @Override
    public List<BthMerInAcc> selectByInAcctTypeFlag(String inAcctType) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).selectByInAcctTypeFlag(inAcctType);
    }

    @Override
    public void deleteByStlmDate(String stlmDate) {
        getSqlSession().getMapper(bthMerInAccMapperMap).deleteByStlmDate(stlmDate);
    }

    @Override
    public int insert(BthMerInAcc bthMerInAcc) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).insert(bthMerInAcc);
    }

	@Override
	public BthMerInAcc queryByTxnSSn(String reserved) {
		return getSqlSession().getMapper(bthMerInAccMapperMap).queryByTxnSSn(reserved);
	}

	@Override
	public int updateByPrimaryKeySelective(BthMerInAcc bthMerInAcc) {
		return getSqlSession().getMapper(bthMerInAccMapperMap).updateByPrimaryKeySelective(bthMerInAcc);
	}

    @Override
    public List<BthMerInAcc> selectByInAcctType2(String inAcctType1, String inAcctType2) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).selectByInAcctType2( inAcctType1,  inAcctType2);
    }

    @Override
    public void updateHandleState(String dateStlm, String txnSsn, String handleState, String handleMark) {
        getSqlSession().getMapper(bthMerInAccMapperMap).updateHandleState( dateStlm, txnSsn,handleState,handleMark);
    }

    @Override
    public List<BthMerInAccSumInfo> selectMerInAccSumInfo(Map<String, Object> params, PagnParam pagnParams) {
        PageHelper.startPage(pagnParams.getPageNo(), pagnParams.getPageSize());
        return getSqlSession().getMapper(bthMerInAccMapperMap).selectMerInAccSumInfo(params);
    }

    @Override
    public BthMerInAccSumInfoExtend selectByBatchNo(String batchNo) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).selectByBatchNo(batchNo);
    }

    @Override
    public List<BthMerInAcc> selectList(String statement, Map<String, Object> params) {
        return getSqlSession().selectList(statement, params);
    }

	@Override
	public BthMerInAcc queryAccState(String mchtId, List<String> batchNo) {
		// TODO Auto-generated method stub
		return getSqlSession().getMapper(bthMerInAccMapperMap).queryAccState(mchtId,batchNo);
	}

	@Override
	public List<BthMerInAcc> queryCommissionAmt(String mchtId, String inAcctDate) {
		// TODO Auto-generated method stub
		return getSqlSession().getMapper(bthMerInAccMapperMap).queryCommissionAmt(mchtId,inAcctDate);
	}

	@Override
	public int update(String statement, Map<String, Object> parameter) {
		return getSqlSession().update(statement, parameter);
	}

	@Override
	public int update(String statement, BthMerInAcc merInAcc) {
		return getSqlSession().update(statement, merInAcc);
	}

	@Override
	public BthMerInAcc selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}


    @Override
    public Integer count(String statement, Map<String, Object> parameter) {
        return (Integer)getSqlSession().selectOne(statement,parameter);
    }

    @Override
    public BthMerInAccSumInfoExtend selectByTxnSsn(String txnSsn) {
        try {
            return getSqlSession().getMapper(bthMerInAccMapperMap).selectByTxnSsn(txnSsn);
        } catch (Exception e) {
            log.error("根据流水号得到商户汇总信息失败: ", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"根据流水号得到商户汇总信息失败");
        }
    }

    @Override
    public int batchUpdate(List<BthMerInAcc> inAccList) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).batchUpdate(inAccList);
    }

    @Override
    public int batchUpdateHandleState(List<BthMerInAcc> inAccList) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).batchUpdateHandleState(inAccList);
    }


    @Override
    public int updateBySsn(BthMerInAcc bthMerInAcc) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).updateBySsn(bthMerInAcc);
    }

    @Override
    public String getBatchNo(String dateStlm) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).getBatchNo(dateStlm);
    }

    @Override
    public int updateBthMerInAccDaoT0ForTxnSsn(BthMerInAcc bthMerInAccT0) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).updateBthMerInAccDaoT0ForTxnSsn(bthMerInAccT0);
    }

    @Override
    public BthMerInAcc getTxnSsn(Map<String,String> map) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).getTxnSsn( map);
    }

    @Override
    public int insertT0(BthMerInAcc bthMerInAcc) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).insertT0(bthMerInAcc);
    }

    @Override
    public int updateByPrimaryKeySelectiveT0(BthMerInAcc bthMerInAcc) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).updateByPrimaryKeySelectiveT0(bthMerInAcc);
    }

    @Override
    public int insertBatch(List<BthMerInAcc> accList) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).insertBatch(accList);
    }

    @Override
    public int insertBatchNo(String cleaTime, String batchNo) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).insertBatchNo(cleaTime,batchNo);
    }

    @Override
    public BthMerInAcc checkInAccAmt(String batchNo) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).checkInAccAmt(batchNo);
    }

    @Override
    public List<BthMerInAcc> queryServeMchtInAcct(String inAcctType, String inAcctStat, String entryType) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).queryServeMchtInAcct(inAcctType,inAcctStat,entryType);
    }

    @Override
    public int batchUpdateForPartern(List<BthMerInAcc> updList) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).batchUpdateForPartern(updList);
    }
    @Override
    public BthMerInAcc otherSetlFeeSumInfo(String mchtId, String inAcctDate) {
        return getSqlSession().getMapper(bthMerInAccMapperMap).otherSetlFeeSumInfo(mchtId,inAcctDate);
    }

    @Override
    public int cleaOther() {
        return getSqlSession().getMapper(bthMerInAccMapperMap).cleaOther();
    }

    @Override
    public int initOtherData() {
        return getSqlSession().getMapper(bthMerInAccMapperMap).initOtherData();
    }


}
