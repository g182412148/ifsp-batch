package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.BthSetCapitalDetail;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthSetCapitalDetailMapper;
import com.scrcu.ebank.ebap.batch.dao.BthSetCapitalDetailDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;

/**
 * <p>名称 :  </p>
 * <p>方法 :  </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : ydl </p>
 * <p>日期 : 2018/6/28 0028  21:06</p>
 */
@Repository
@Slf4j
public class BthSetCapitalDetailDaoImpl extends BaseBatisDao implements BthSetCapitalDetailDao {

    private Class<BthSetCapitalDetailMapper> bthSetCapitalDetailMapper= BthSetCapitalDetailMapper.class;
    @Override
    public int deleteByPrimaryKey(String subOrderSsn) {
        return getSqlSession().getMapper(bthSetCapitalDetailMapper).deleteByPrimaryKey(subOrderSsn);
    }

    @Override
    public int insert(BthSetCapitalDetail record) {
        return getSqlSession().getMapper(bthSetCapitalDetailMapper).insert(record);
    }

    @Override
    public int insertSelective(BthSetCapitalDetail record) {
        return getSqlSession().getMapper(bthSetCapitalDetailMapper).insertSelective(record);
    }

    @Override
    public BthSetCapitalDetail selectByPrimaryKey(String subOrderSsn) {
        return getSqlSession().getMapper(bthSetCapitalDetailMapper).selectByPrimaryKey(subOrderSsn);
    }

    @Override
    public int updateByPrimaryKeySelective(BthSetCapitalDetail record) {
        return getSqlSession().getMapper(bthSetCapitalDetailMapper).updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(BthSetCapitalDetail record) {
        return getSqlSession().getMapper(bthSetCapitalDetailMapper).updateByPrimaryKey(record);
    }

	@Override
	public List<BthSetCapitalDetail> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}

	@Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement,parameter);
	}

	@Override
	public BthSetCapitalDetail selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement,parameter);
	}

	@Override
	public int update(String statement, Map<String, Object> parameter) {
		return getSqlSession().update(statement, parameter);
	}

	@Override
	public List<BthSetCapitalDetail> querybthMerInAccDtlByWhere(String chlMerId, Set<String> entryType,
                                                                String pch, String outAcctNo, String inAcctNo, String outAcctNoOrg, String inAcctNoOrg) {
		return getSqlSession().getMapper(bthSetCapitalDetailMapper).querybthMerInAccDtlByWhere(chlMerId,entryType,pch,outAcctNo,inAcctNo,outAcctNoOrg,inAcctNoOrg);
	}

    @Override
    public List<String> selectByBatchNo(String batchNo) {
        return getSqlSession().getMapper(bthSetCapitalDetailMapper).selectByBatchNo(batchNo);
    }

	@Override
	public List<BthSetCapitalDetail> queryTranAmountAndentryType(String mchtId, List<String> batchNo) {
		
		return getSqlSession().getMapper(bthSetCapitalDetailMapper).queryTranAmountAndentryType(mchtId,batchNo);
	}

    @Override
    public List<BthSetCapitalDetail> queryByAccNo(String outAcctNo, String inAcctNo, String pch) {
        try {
            return getSqlSession().getMapper(bthSetCapitalDetailMapper).queryByAccNo(outAcctNo, inAcctNo, pch);
        } catch (Exception e) {
            log.error("根据转入转出账号与批次号查询清分表数据异常:",e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"根据转入转出账号与批次号查询清分表数据异常!!!");
        }
    }

    @Override
    public List<BthSetCapitalDetail> queryByDate(String dateStr) {
        try {
            return getSqlSession().getMapper(bthSetCapitalDetailMapper).queryByDate(dateStr);
        } catch (Exception e) {
            log.error("根据转入的清分日期查询清分表数据异常:",e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"根据转入的清分日期查询清分表数据异常!!!");
        }
    }

    @Override
    public int batchUpdate(List<BthSetCapitalDetail> updList) {
        return getSqlSession().getMapper(bthSetCapitalDetailMapper).batchUpdate(updList);
    }

    @Override
    public int insertBatch(List<BthSetCapitalDetail> insertList) {
        return getSqlSession().getMapper(bthSetCapitalDetailMapper).insertBatch(insertList);
    }

    @Override
    public int insertBatchTempTable(List<BthSetCapitalDetail> insertList,String pagyNo) {
        return getSqlSession().getMapper(bthSetCapitalDetailMapper).insertBatchTempTable(insertList,pagyNo);
    }

    @Override
	public List<String> selectOrderIdList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}

    @Override
    public int updateFailureT0(BthSetCapitalDetail bthSetCapitalDetail) {
        return getSqlSession().getMapper(bthSetCapitalDetailMapper).updateFailureT0(bthSetCapitalDetail);
    }

    @Override
    public BthSetCapitalDetail selectClearSumInfoMchtInT0(Map map) {
        return getSqlSession().getMapper(bthSetCapitalDetailMapper).selectClearSumInfoMchtInT0(map);
    }

    @Override
    public int delete(String statement, Map<String, Object> parameter)
    {
        return getSqlSession().delete(statement, parameter);
    }

    @Override
    public BthSetCapitalDetail queryByOrderEntry(String orderId,String entryType)
    {
        return getSqlSession().getMapper(bthSetCapitalDetailMapper).queryByOrderEntry(orderId,entryType);
    }

    @Override
    public int batchUpdateForPartern(List<BthSetCapitalDetail> updList) {
        return getSqlSession().getMapper(bthSetCapitalDetailMapper).batchUpdateForPartern(updList);
    }
}
