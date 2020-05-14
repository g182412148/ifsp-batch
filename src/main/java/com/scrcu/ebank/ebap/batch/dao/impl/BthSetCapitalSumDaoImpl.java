package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccDtl;
import com.scrcu.ebank.ebap.batch.bean.dto.BthSetCapitalSum;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthMerTxnFeeMapper;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthSetCapitalSumMapper;
import com.scrcu.ebank.ebap.batch.dao.BthMerTxnFeeDao;
import com.scrcu.ebank.ebap.batch.dao.BthSetCapitalSumDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * <p>名称 :  </p>
 * <p>方法 :  </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : ydl </p>
 * <p>日期 : 2018/6/28 0028  21:06</p>
 */
@Repository
@Slf4j
public class BthSetCapitalSumDaoImpl extends BaseBatisDao implements BthSetCapitalSumDao
{

    private Class<BthSetCapitalSumMapper> bthSetCapitalSumMapper = BthSetCapitalSumMapper.class;

    @Override
    public int clear()
    {
        return getSqlSession().getMapper(bthSetCapitalSumMapper).clear();
    }

    @Override
    public List<BthSetCapitalSum> selectList(String statement, Map<String, Object> parameter)
    {
        return getSqlSession().selectList(statement, parameter);
    }

    @Override
    public int initData(String batchNo)
    {
        return getSqlSession().getMapper(bthSetCapitalSumMapper).initData(batchNo);
    }
}
