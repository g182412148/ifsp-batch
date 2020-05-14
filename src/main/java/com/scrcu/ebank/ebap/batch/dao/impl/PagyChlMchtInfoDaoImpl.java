package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.mapper.PagyChlMchtInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.PagyChlMchtInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author: ljy
 * @create: 2018-10-26 21:52
 */
@Slf4j
@Repository
public class PagyChlMchtInfoDaoImpl  extends BaseBatisDao implements PagyChlMchtInfoDao {

    private final Class<PagyChlMchtInfoMapper> pagyChlMchtInfoMapperClass = PagyChlMchtInfoMapper.class;


    @Override
    public void updateStateByChlMchtNo(String chlMchtNo, Date date) {
        try {
            getSqlSession().getMapper(pagyChlMchtInfoMapperClass).updateStateByChlMchtNo(chlMchtNo,date);
        } catch (Exception e) {
            log.error("更新渠道商户状态失败!!! chlMchtNo[{}]" ,chlMchtNo);
            throw new IfspBizException("9999","更新渠道商户状态失败!!!");
        }
    }
}
