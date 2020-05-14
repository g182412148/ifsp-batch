package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.BthSysParamInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthSysParamInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.BthSysParamInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * @author: ljy
 * @create: 2018-10-25 11:00
 */
@Slf4j
@Repository
public class BthSysParamInfoDaoImpl extends BaseBatisDao implements BthSysParamInfoDao {

    private final Class<BthSysParamInfoMapper> bthSysParamInfoMapperClass = BthSysParamInfoMapper.class;


    @Override
    public BthSysParamInfo selectByParamCode(String key) {
        try {
            return getSqlSession().getMapper(bthSysParamInfoMapperClass).selectByPrimaryKey(key);
        } catch (Exception e) {
            log.error("查询清算系统参数表失败, PapamCode为[{}]",key);
            throw new IfspBizException("9999","查询批量系统参数表失败!!!");
        }
    }

    @Override
    public void updateInfo(BthSysParamInfo record) {
        try {
            getSqlSession().getMapper(bthSysParamInfoMapperClass).updateByPrimaryKeySelective(record);
        } catch (Exception e) {
            log.error("更新清算系统参数表失败, PapamCode为[{}]",record.getPapamCode());
            throw new IfspBizException("9999","更新批量系统参数表失败!!!");
        }
    }
}
