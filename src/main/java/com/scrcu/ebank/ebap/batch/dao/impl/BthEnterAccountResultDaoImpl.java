package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.BthBatchAccountFile;
import com.scrcu.ebank.ebap.batch.bean.dto.BthEnterAccountResult;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAcc;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthBatchAccountFileMapper;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthEnterAccountResultMapper;
import com.scrcu.ebank.ebap.batch.dao.BthEnterAccountResultDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class BthEnterAccountResultDaoImpl extends BaseBatisDao implements BthEnterAccountResultDao
{

    private final Class<BthEnterAccountResultMapper> bthEnterAccountResultMapper = BthEnterAccountResultMapper.class;

    @Override
    public int insertBatch(List<BthEnterAccountResult> records)
    {
        return getSqlSession().getMapper(bthEnterAccountResultMapper).insertBatch(records);
    }

    @Override
    public void clear()
    {
        getSqlSession().getMapper(bthEnterAccountResultMapper).clear();
    }
}
