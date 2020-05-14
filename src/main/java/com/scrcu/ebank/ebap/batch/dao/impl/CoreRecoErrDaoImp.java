package com.scrcu.ebank.ebap.batch.dao.impl;/**
 * Created by Administrator on 2019-05-19.
 */

import com.scrcu.ebank.ebap.batch.bean.dto.BillRecoErr;
import com.scrcu.ebank.ebap.batch.bean.dto.CoreRecoErr;
import com.scrcu.ebank.ebap.batch.bean.mapper.CoreRecoErrMapper;
import com.scrcu.ebank.ebap.batch.dao.CoreRecoErrDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 名称：〈名称〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019-05-19 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
@Repository
@Slf4j
public class CoreRecoErrDaoImp extends BaseBatisDao implements CoreRecoErrDao
{

    private final Class<CoreRecoErrMapper> mapper = CoreRecoErrMapper.class;

    @Override
    public int insert(CoreRecoErr record)
    {
        return getSqlSession().getMapper(mapper).insert(record);
    }

    @Override
    public int insertBatch(List<CoreRecoErr> recordList)
    {
        if (recordList == null || recordList.isEmpty())
        {
            return 0;
        }
        else
        {
            for (CoreRecoErr record : recordList)
            {
                getSqlSession().getMapper(mapper).insert(record);
            }
            return recordList.size();
        }
    }

    @Override
    public int clear(Date recoDate)
    {
        return getSqlSession().getMapper(mapper).clear(recoDate);
    }

    @Override
    public int updateCoreDubiousOrError(String recoDate)
    {
        return getSqlSession().getMapper(mapper).updateCoreDubiousOrError(recoDate);
    }

    @Override
    public int updateLocalDubiousOrError(String recoDate)
    {
        return getSqlSession().getMapper(mapper).updateLocalDubiousOrError(recoDate);
    }

    @Override
    public int insertCoreError(String recoDate)
    {
        return getSqlSession().getMapper(mapper).insertCoreError(recoDate);
    }

    @Override
    public int insertLocalError(String recoDate)
    {
        return getSqlSession().getMapper(mapper).insertLocalError(recoDate);
    }

    @Override
    public int updateCoreSuccess(String recoDate)
    {
        return getSqlSession().getMapper(mapper).updateCoreSuccess(recoDate);
    }

    @Override
    public int updateLocalSuccess(String recoDate)
    {
        return getSqlSession().getMapper(mapper).updateLocalSuccess(recoDate);
    }

    @Override
    public int updateAccInfo(String recoDate,String orderTmStart,String orderTmEnd) {
        return getSqlSession().getMapper(mapper).updateAccInfo(recoDate,orderTmStart,orderTmEnd);
    }
}
