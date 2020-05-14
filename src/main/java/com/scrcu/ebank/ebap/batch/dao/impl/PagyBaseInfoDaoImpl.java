package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.PagyBaseInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.PagyBaseInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.PagyBaseInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("pagyBaseInfoDao")
public class PagyBaseInfoDaoImpl extends BaseBatisDao implements PagyBaseInfoDao {

    private final Class<PagyBaseInfoMapper> pagyBaseInfoMapper = PagyBaseInfoMapper.class;


    @Override
    public List<PagyBaseInfo> selectByPagyNo(String wxSysNo) {
        return getSqlSession().getMapper(pagyBaseInfoMapper).selectByPagyNo(wxSysNo);
    }
}
