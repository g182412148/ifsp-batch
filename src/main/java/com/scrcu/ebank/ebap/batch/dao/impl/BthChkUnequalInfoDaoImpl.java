package com.scrcu.ebank.ebap.batch.dao.impl;

import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.scrcu.ebank.ebap.batch.bean.dto.BthChkUnequalInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthChkUnequalInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.BthChkUnequalInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class BthChkUnequalInfoDaoImpl extends BaseBatisDao implements BthChkUnequalInfoDao {

    private final Class<BthChkUnequalInfoMapper> bthChkUnequalInfoMapperClass = BthChkUnequalInfoMapper.class;

    @Override
    public void deleteByChkUeqDtAndPagySysNo(String chkUeqDate, String pagySysNo) {
        getSqlSession().getMapper(bthChkUnequalInfoMapperClass).deleteByChkUeqDtAndPagySysNo(chkUeqDate,pagySysNo);

    }

    @Override
    public void insert(BthChkUnequalInfo unEqRecord) {
        getSqlSession().getMapper(bthChkUnequalInfoMapperClass).insert(unEqRecord);
    }

    @Override
    public List<BthChkUnequalInfo> selectList(String statement, Map<String, Object> map) {
        return getSqlSession().selectList(statement,  map);
    }

    @Override
    public void updChkUnequalInfoProcStByPagyPayTxnSsn(String pagyPayTxnSsn) {
        getSqlSession().getMapper(bthChkUnequalInfoMapperClass).updChkUnequalInfoProcStByPagyPayTxnSsn(pagyPayTxnSsn);

    }
}
