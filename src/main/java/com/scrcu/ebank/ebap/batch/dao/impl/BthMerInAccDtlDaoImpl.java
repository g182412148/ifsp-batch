package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageHelper;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.scrcu.ebank.ebap.batch.bean.dto.*;

import com.scrcu.ebank.ebap.batch.bean.vo.MerCommissionVo;
import com.scrcu.ebank.ebap.batch.bean.vo.InAccMerVo;
import com.scrcu.ebank.ebap.batch.bean.vo.TxnCountVo;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.mapper.BthMerInAccDtlMapper;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class BthMerInAccDtlDaoImpl extends BaseBatisDao implements BthMerInAccDtlDao{

    private final Class<BthMerInAccDtlMapper> bthMerInAccDtlMapper=BthMerInAccDtlMapper.class;
    @Override
    public void insertSelective(BthMerInAccDtl dtl) throws Exception {
        try {
            getSqlSession().getMapper(bthMerInAccDtlMapper).insertSelective(dtl);
        } catch (Exception e) {
            log.error("插入入账明细表失败: ", e);
            throw new Exception( "插入入账明细表失败!");
        }
    }

    @Override
    public List<String> selectTxnSeqIdList(String statement, Map<String, Object> parameter) {
        return  getSqlSession().selectList(statement, parameter);
    }

    @Override
    public void delByStlmDate(String settleDate) throws Exception {
        try {
            getSqlSession().getMapper(bthMerInAccDtlMapper).delByStlmDate(settleDate);
        } catch (Exception e) {
            log.error("删除入账明细记录失败: ", e);
            throw new Exception( "删除入账明细记录失败!");
        }
    }

	@Override
	public List<BthMerInAccDtlVo> queryByMchtAndInAcctDate(String mchtId, String qryDate, int pageNo, int pageSize) {
			return getSqlSession().getMapper(bthMerInAccDtlMapper).queryByMchtAndInAcctDate(mchtId,qryDate,pageNo,pageSize);
	}

	@Override
	public List<MonthlyBill> queryByMchtAndMonth(String mchtId, String startDate, String endDate, String txnFlag) {
		return getSqlSession().getMapper(bthMerInAccDtlMapper).queryByMchtAndMonth(mchtId,startDate,endDate,txnFlag);

	}

	@Override
	public BthMerInAccDtlVo queryMerInAccDtlByMchtIdAndTime(String mchtId, String startDate, String endDate) {
		return getSqlSession().getMapper(bthMerInAccDtlMapper).queryMerInAccDtlByMchtIdAndTime(mchtId,startDate,endDate);
	}

	@Override
	public BthMerInAccDtlVo queryMerSum(String mchtId, String startDate, String endDate) {
		return getSqlSession().getMapper(bthMerInAccDtlMapper).queryMerSum(mchtId,startDate,endDate);
	}

    @Override
	public BthMerInAccDtl selectByTxnSeqId(String orderSsn) throws Exception {
		BthMerInAccDtl bthMerInAccDtl = new BthMerInAccDtl();
		try {
			bthMerInAccDtl =getSqlSession().getMapper(bthMerInAccDtlMapper).selectByTxnSeqId(orderSsn);
        } catch (Exception e) {
            log.error("查询入账明细记录失败: ", e);
            throw new Exception( "查询入账明细记录失败!");
        }
		return bthMerInAccDtl;
	}


	@Override
	public List<BthMerInAccDtl> querybthMerInAccDtlByWhere(String chlMerId, String inAcctTime, String inAcctNo,
			String outAcctNo, String pch) {
		return getSqlSession().getMapper(bthMerInAccDtlMapper).querybthMerInAccDtlByWhere(chlMerId,inAcctTime,inAcctNo,outAcctNo,pch);
	}

	@Override
	public int updateByPrimaryKeySelective(BthMerInAccDtl bthMerInAccDtl) {
		return getSqlSession().getMapper(bthMerInAccDtlMapper).updateByPrimaryKeySelective(bthMerInAccDtl);

	}

	@Override
	public List<BthMerInAccDtl> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}

	@Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public BthMerInAccDtl selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public int update(String statement, Map<String, Object> parameter) {
		return getSqlSession().update(statement, parameter);
	}

    @Override
    public int delete(String statement, Map<String, Object> parameter)
    {
        return getSqlSession().delete(statement, parameter);
    }

    @Override
    public int insert(String statement, Map<String, Object> parameter)
    {
        return getSqlSession().insert(statement, parameter);
    }

    @Override
    public BthMerInAccInfo selectByMerIdOrderType(String stlmDate, String merId, String orderType) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).selectByMerIdOrderType(stlmDate,merId,orderType);
    }
    
	@Override
	public List<DailyStatis> queryDailyStatis(String mchtId, String startDate, String endDate) {
		
		return getSqlSession().getMapper(bthMerInAccDtlMapper).queryDailyStatis(mchtId,startDate,endDate);
	}


    @Override
    public BthMerInAccInfo sumTxnAmtFeeAmt(Map<String, Object> map) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).sumTxnAmtFeeAmt(map);
    }
    
	@Override
	public BthMerInAccDtlVo queryTotalTxnAmtAndTotalTxnCountByMchtAndMonth(String mchtId, String startDate, String endDate, String txnFlag) {
		return getSqlSession().getMapper(bthMerInAccDtlMapper).queryTotalTxnAmtAndTotalTxnCountByMchtAndMonth(mchtId,startDate,endDate,txnFlag);
	}

	@Override
	public int queryCount(String mchtId, String qryDate) {
		return getSqlSession().getMapper(bthMerInAccDtlMapper).queryCount(mchtId,qryDate);
	}

    @Override
    public List<BthMerInAccDtlInfo> selectByBatchNo(String merId, String batchNo, PagnParam pagnParams) {
        PageHelper.startPage(pagnParams.getPageNo(),pagnParams.getPageSize());
        return getSqlSession().getMapper(bthMerInAccDtlMapper).selectByBatchNo(merId, batchNo);
    }

	@Override
	public List<BthMerInAccDtl> queryByTxnSeqIdAndPch(String txnSeqId) {
		 return getSqlSession().getMapper(bthMerInAccDtlMapper).queryByTxnSeqIdAndPch(txnSeqId);
	}

    @Override
    public BthMerInAccMchts selectByMerTxn(Map<String, Object> map) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).selectByMerTxn(map);
    }
    @Override
    public List<BthMerInAccMchts> selectByMerTxnList(Map<String, Object> map) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).selectByMerTxnList(map);
    }
    @Override
    public List<BthMerInAccMchtsDtl> selectByMerTxnDtl(Map<String, Object> map) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).selectByMerTxnDtl(map);
    }
    @Override
    public List<BthMerInAccMchtsDtl> selectByMerTxnDtlDown(Map<String, Object> map) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).selectByMerTxnDtlDown(map);
    }
	@Override
	public List<MonthlyBill> queryStateByMchtIdAndStartDateAndEndDate(String mchtId, String startDate, String endDate) {
		return getSqlSession().getMapper(bthMerInAccDtlMapper).queryStateByMchtIdAndStartDateAndEndDate(mchtId,startDate,endDate);
	}

	@Override
	public List<BthMerInAccDtlVo> queryAccCount(String mchtId, String qryDate) {
		return getSqlSession().getMapper(bthMerInAccDtlMapper).queryAccCount(mchtId,qryDate);
	}

    @Override
    public List<BthMerTxnCountInfo> selectGroupByMchtFundType(Map<String, Object> map) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).selectGroupByMchtFundType(map);
    }

	@Override
	public List<BthMerInAccDtlVo> queryGSCommissionAmt(String mchtId, String qryDate) {
		return getSqlSession().getMapper(bthMerInAccDtlMapper).queryGSCommissionAmt(mchtId,qryDate);
	}

	@Override
	public List<BthMerInAccDtl> querySubMchtAll(String mchtId, String qryDate) {
		return getSqlSession().getMapper(bthMerInAccDtlMapper).querySubMchtAll(mchtId,qryDate);
	}

	@Override
	public String queryCommissionAmt(List<String> subMchtIds, String txnTime) {
		return getSqlSession().getMapper(bthMerInAccDtlMapper).queryCommissionAmt(subMchtIds, txnTime);
	}

    @Override
    public List<BthMerInAccMchtsDtl> selectByMerTxnDtlOnline(Map<String, Object> map) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).selectByMerTxnDtlOnline(map);
    }
    @Override
    public List<BthMerInAccMchtsDtl> selectByMerTxnDtlOnlineDown(Map<String, Object> map) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).selectByMerTxnDtlOnlineDown(map);
    }
	@Override
	public List<MonthlyBill> queryByMchtAndTxnTm(String mchtId, String startDate, String endDate, String txnFlag) {
		return getSqlSession().getMapper(bthMerInAccDtlMapper).queryByMchtAndTxnTm(mchtId,startDate,endDate,txnFlag);
	}

    @Override
    public int updateByTxnSeqId(BthMerInAccDtl bthMerInAccDtl) {
        try {
            return getSqlSession().getMapper(bthMerInAccDtlMapper).updateByTxnSeqId(bthMerInAccDtl);
        } catch (Exception e) {
            log.error("根据订单号更新明细表失败: ", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"根据订单号更新明细表失败");
        }
    }

    @Override
    public List<String> queryInAccStatByMchtAndInAcctDate(String mchtId, String qryDate) {
        try {
            return getSqlSession().getMapper(bthMerInAccDtlMapper).queryInAccStatByMchtAndInAcctDate(mchtId,qryDate);
        } catch (Exception e) {
            log.error("根据商户号与入账日期查询明细表失败: ", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"根据商户号与入账日期查询明细表失败!!!");
        }
    }

    @Override
    public List<MerCommissionVo> selectCommissionAmtBySubMerIdAndTxnTm(String mchtId, String startDate, String endDate, String txnFlag) {
        try {
            return getSqlSession().getMapper(bthMerInAccDtlMapper).selectCommissionAmtBySubMerIdAndTxnTm(mchtId,startDate,endDate,txnFlag);
        } catch (Exception e) {
            log.error("根据商户号与交易日期查询返佣情况失败: ", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"根据商户号与交易日期查询返佣情况失败!!!");
        }
    }

    @Override
    public List<MerCommissionVo> selectCommissionAmtBySubMerIdAndInAcctDate(String mchtId, String startDate, String endDate, String txnFlag) {
        try {
            return getSqlSession().getMapper(bthMerInAccDtlMapper).selectCommissionAmtBySubMerIdAndInAcctDate(mchtId,startDate,endDate,txnFlag);
        } catch (Exception e) {
            log.error("根据商户号与入账日期查询返佣情况失败: ", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"根据商户号与入账日期查询返佣情况失败!!!");
        }
    }

    @Override
    public List<InAccMerVo> prdCountByBatchNo() {
        try {
            return getSqlSession().getMapper(bthMerInAccDtlMapper).prdCountByBatchNo();
        } catch (Exception e) {
            log.error("根据批次号统计跑批情况失败: ", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"根据批次号统计跑批情况失败");
        }
    }

    @Override
    public List<TxnCountVo> txnCountAmtSumGroupByPagyNo(String runDate) {
        try {
            return getSqlSession().getMapper(bthMerInAccDtlMapper).txnCountAmtSumGroupByPagyNo(runDate);
        } catch (Exception e) {
            log.error("根据跑批日统计线下各个通道成功交易笔数金额失败: ", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"根据跑批日统计线下各个通道成功交易笔数金额失败");
        }
    }

    @Override
    public int selectForMerNum(String runDate) {
        try {
            return getSqlSession().getMapper(bthMerInAccDtlMapper).selectForMerNum(runDate);
        } catch (Exception e) {
            log.error("根据跑批日统计入账成功商户数量失败: ", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"根据跑批日统计入账成功商户数量失败");
        }
    }

    @Override
    public String selectInAccEndTm(String runDate) {
        try {
            return getSqlSession().getMapper(bthMerInAccDtlMapper).selectInAccEndTm(runDate);
        } catch (Exception e) {
            log.error("根据跑批日得到入账结束时间失败: ", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"根据跑批日得到入账结束时间失败");
        }
    }

    @Override
    public List<TxnCountVo> txnCountAmtSumGroupByPagyNoOnline(String runDate) {
        try {
            return getSqlSession().getMapper(bthMerInAccDtlMapper).txnCountAmtSumGroupByPagyNoOnline(runDate);
        } catch (Exception e) {
            log.error("根据跑批日统计线上各个通道成功交易笔数金额失败: ", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"根据跑批日统计线上各个通道成功交易笔数金额失败");
        }
    }

    @Override
    public String countInAccAmt(String mchtId, String inAccDate) {
        try {
            return getSqlSession().getMapper(bthMerInAccDtlMapper).countInAccAmt(mchtId, inAccDate);
        } catch (Exception e) {
            log.error("根据商户号与入账日期统计当日入账金额失败: ", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"根据商户号与入账日期统计当日入账金额失败");
        }
    }

    @Override
    public List<InAccMerVo> prdCountFailRecord(String batchNo, String chlMerId, String statMark) {
        try {
            return getSqlSession().getMapper(bthMerInAccDtlMapper).prdCountFailRecord(batchNo,chlMerId,statMark);
        } catch (Exception e) {
            log.error("根据商户号与批次号查询失败明细记录失败: ", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"根据商户号与批次号查询失败明细记录失败");
        }
    }

    @Override
    public int batchUpdate(List<BthMerInAccDtl> updList) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).batchUpdate(updList);
    }

    @Override
    public int updateByOrderSsn(BthMerInAccDtl bthMerInAccDtl) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).updateByOrderSsn(bthMerInAccDtl);
    }

    @Override
    public int recovery(String createDate) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).recovery(createDate);
    }

    @Override
    public int cleCount(String stlDate, String channel, String stlStatus) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).cleCount(stlDate, channel, stlStatus);
    }

    @Override
    public List<BthMerInAccDtl> getCleaDateList(String stlDate, String channel, String stlStatus, int minIndex, int maxIndex) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).getCleaDateList(stlDate, channel, stlStatus, minIndex, maxIndex);
    }
    @Override
    public List<BthMerInAccDtl> getCleaDateListTemp(String stlDate, String channel, String stlStatus, int minIndex, int maxIndex) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).getCleaDateListTemp(stlDate, channel, stlStatus, minIndex, maxIndex);
    }
    @Override
    public List<BthMerInAccDtl> getCleaDateAllList(String stlDate, String channel) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).getCleaDateAllList(stlDate, channel);
    }

    @Override
    public int insertBatch(List<BthMerInAccDtl> insertList) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).insertBatch(insertList);
    }

    @Override
    public String selectUpdateDate(String txnSeqId) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).selectUpdateDate(txnSeqId);
    }

    @Override
    public int insertBatchTemp(List<BthMerInAccDtl> insertList) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).insertBatchTemp(insertList);
    }

    @Override
    public BthMerInAccInfo sumTxnAmtFeeAmtT0(Map<String, Object> map) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).sumTxnAmtFeeAmtT0(map);
    }

    @Override
    public int batchUpdateForT0Bak(BthMerInAccDtl bthMerInAccDtl) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).batchUpdateForT0Bak(bthMerInAccDtl);
    }

    @Override
    public int batchUpdateForT0(List<BthMerInAccDtl> bthMerInAccDtlList) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).batchUpdateForT0(bthMerInAccDtlList);
    }
    @Override
    public String countInAccAmtT0(String mchtId, String inAccDate) {
        try {
            return getSqlSession().getMapper(bthMerInAccDtlMapper).countInAccAmtT0(mchtId, inAccDate);
        } catch (Exception e) {
            log.error("根据商户号与入账日期统计当日入账金额失败: ", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"根据商户号与入账日期统计当日入账金额失败");
        }
    }

    @Override
    public BthMerTxnCountInfo checkSumInAccAmtDtl(String stlmDate) {
        return getSqlSession().getMapper(bthMerInAccDtlMapper).checkSumInAccAmtDtl(stlmDate);
    }
}
