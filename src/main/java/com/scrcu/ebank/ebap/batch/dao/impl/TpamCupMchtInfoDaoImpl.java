package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamCupMchtInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.TpamCupMchtInfoMapper;
import com.scrcu.ebank.ebap.batch.common.dict.UpMchtSynStateDict;
import com.scrcu.ebank.ebap.batch.dao.TpamCupMchtInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author: ljy
 * @create: 2018-10-24 19:38
 */
@Slf4j
@Repository
public class TpamCupMchtInfoDaoImpl extends BaseBatisDao implements TpamCupMchtInfoDao {

    private final Class<TpamCupMchtInfoMapper> tpamCupMchtInfoMapper = TpamCupMchtInfoMapper.class;

    @Override
    public List<TpamCupMchtInfo> selectAll() {
        try {
            return getSqlSession().getMapper(tpamCupMchtInfoMapper).selectAll();
        } catch (Exception e) {
            log.error("查询所有商户信息失败: ", e);
            throw new IfspBizException("9999", "查询所有商户信息失败");
        }
    }

    @Override
    public List<TpamCupMchtInfo> selectBytTimeAdd(String beginTime, String endTime) {
        try {
            return getSqlSession().getMapper(tpamCupMchtInfoMapper).selectBytTimeAdd(beginTime,endTime);
        } catch (Exception e) {
            log.error("查询昨天新增的商户信息失败: ", e);
            throw new IfspBizException("9999", "查询昨天新增的商户信息失败");
        }
    }

    @Override
    public TpamCupMchtInfo selectByMchtId(String mchtId) {
        return getSqlSession().getMapper(tpamCupMchtInfoMapper).selectByPrimaryKey(mchtId);
    }

    @Override
    public List<TpamCupMchtInfo> selectBytTimeUpdate(String beginTime, String endTime) {
        try {
            return getSqlSession().getMapper(tpamCupMchtInfoMapper).selectBytTimeUpdate(beginTime,endTime);
        } catch (Exception e) {
            log.error("查询昨天修改的商户信息失败: ", e);
            throw new IfspBizException("9999", "查询昨天修改的商户信息失败");
        }
    }
    @Override
    public List<String> selectNomalMchts() {
        try {
            return getSqlSession().getMapper(tpamCupMchtInfoMapper).selectNomalMchts();
        } catch (Exception e) {
            log.error("查询银联商户号失败: ", e);
            throw new IfspBizException("9999", "查询银联商户号失败");
        }
    }

    @Override
    public void updateStateByMchntCd(String mchntCd, Date date) {
        try {
            getSqlSession().getMapper(tpamCupMchtInfoMapper).updateStateByMchntCd(mchntCd,date);
        } catch (Exception e) {
            log.error("更新银联商户号更新商户状态失败: ", e);
            throw new IfspBizException("9999", "更新银联商户号更新商户状态失败");
        }
    }
}
