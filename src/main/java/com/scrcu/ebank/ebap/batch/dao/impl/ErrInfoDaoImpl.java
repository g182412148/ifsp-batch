package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.ErrInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.ErrInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.ErrInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @CopyRightInformation : 数云
 * @Prject: 数云PMS
 * @author: sun_b
 * @date: 2020/5/8
 */
@Repository
public class ErrInfoDaoImpl extends BaseBatisDao implements ErrInfoDao {

    private final Class<ErrInfoMapper> errmapper = ErrInfoMapper.class;

    @Override
    public List<ErrInfo> getWxErrInfo(Date recoDate) {
        return super.getSqlSession().getMapper(errmapper).getWxErrInfo(recoDate);
    }

    @Override
    public List<ErrInfo> getAliErrInfo(Date recoDate) {
        return super.getSqlSession().getMapper(errmapper).getAliErrInfo(recoDate);
    }

    @Override
    public List<ErrInfo> getUnionErrInfo(Date recoDate) {
        return super.getSqlSession().getMapper(errmapper).getUnionErrInfo(recoDate);
    }

    @Override
    public List<ErrInfo> getErrFileInfo() {
        return super.getSqlSession().getMapper(errmapper).getErrFileInfo();
    }

    @Override
    public int insertErrInfoBatch(List<ErrInfo> list) {
        return super.getSqlSession().getMapper(errmapper).insertErrInfoBatch(list);
    }

    @Override
    public int updateErrInfoBatch(List<ErrInfo> list) {
        return super.getSqlSession().getMapper(errmapper).updateErrInfoBatch(list);
    }

}
