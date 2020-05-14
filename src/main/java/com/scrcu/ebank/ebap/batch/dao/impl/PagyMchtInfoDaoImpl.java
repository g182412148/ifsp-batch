package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.scrcu.ebank.ebap.batch.bean.mapper.PagyMchtInfoMapper;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.PagyMchtInfo;
import com.scrcu.ebank.ebap.batch.dao.PagyMchtInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;

/**
 *名称：<通道流水抽取Impl> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Slf4j
@Repository("PagyMchtInfoDao")
public class PagyMchtInfoDaoImpl  extends BaseBatisDao implements PagyMchtInfoDao{

    private final Class<PagyMchtInfoMapper> pagyMchtInfoMapperClass = PagyMchtInfoMapper.class;

	@Override
	public List<PagyMchtInfo> selectList(String statement, Map<String, Object> params) {
		return getSqlSession().selectList(statement, params);
	}

    @Override
    public List<String> selectAllMchtNo(Set<String> pagySysNo) {
        try {
            return getSqlSession().getMapper(pagyMchtInfoMapperClass).selectAllMchtNo(pagySysNo);
        } catch (Exception e) {
            log.error("根据通道系统编号查询三方商户号失败!!!");
            throw new IfspBizException("9999","根据通道系统编号查询三方商户号失败");
        }
    }

    @Override
    public PagyMchtInfo selectByMchntCd(String split) {
        try {
            return getSqlSession().getMapper(pagyMchtInfoMapperClass).selectByMchntCd(split);
        } catch (Exception e) {
            log.error("根据银联商户号查询本地渠道商户号失败!!!");
            throw new IfspBizException("9999","根据银联商户号查询本地渠道商户号失败");
        }
    }

    @Override
    public void updateStat(String mchtNo, Date date2) {
        try {
            getSqlSession().getMapper(pagyMchtInfoMapperClass).updateStat(mchtNo,date2);
        } catch (Exception e) {
            log.error("根据银联商户号更新本地通道信息表失败!!!");
            throw new IfspBizException("9999","根据银联商户号更新本地通道信息表失败");
        }
    }
    @Override
    public void updatePagyMchtStat(String pagyMchtNo, String state){
        try {
            getSqlSession().getMapper(pagyMchtInfoMapperClass).updatePagyMchtStat(pagyMchtNo,state);
        } catch (Exception e) {
            log.error("根据银联商户号更新本地通道信息表失败!!!");
            throw new IfspBizException("9999","根据银联商户号更新本地通道信息表失败");
        }
    }
    @Override
    public void updateUpMchtSynState(String pagyMchtNo, String state,String curDate){
        try {
            getSqlSession().getMapper(pagyMchtInfoMapperClass).updateUpMchtSynState(pagyMchtNo,state,curDate);
        } catch (Exception e) {
            log.error("根据通道商户号更新银联同步状态失败!!!");
            throw new IfspBizException("9999","根据通道商户号更新银联同步状态失败");
        }
    }
    @Override
    public void updateUpMchtSynStateOld(String pagyMchtNo, String state,String curDate){
        try {
            getSqlSession().getMapper(pagyMchtInfoMapperClass).updateUpMchtSynStateOld(pagyMchtNo,state,curDate);
        } catch (Exception e) {
            log.error("根据通道商户号更新银联同步状态失败!!!");
            throw new IfspBizException("9999","根据通道商户号更新银联同步状态失败");
        }
    }
    @Override
    public List<PagyMchtInfo> selectByState(List<String> stateList) {
        try {
            return getSqlSession().getMapper(pagyMchtInfoMapperClass).selectByState(stateList);
        } catch (Exception e) {
            log.error("根据通道商户状态查询通道商户信息表失败!!!");
            throw new IfspBizException("9999","根据通道商户状态查询通道商户信息表失败");
        }
    }
  @Override
  public int updateUpMchtSynRes(String upTableName,String tpamPagyMchtNo, String date, String upMchtSynState,String upMchtSynFailedRes){
      try {
          return getSqlSession().getMapper(pagyMchtInfoMapperClass).updateUpMchtSynRes(upTableName,tpamPagyMchtNo,date,upMchtSynState,upMchtSynFailedRes);
      } catch (Exception e) {
          log.error("根据银联商户号更新商户同步状态失败: ", e);
          throw new IfspBizException("9999", "根据银联商户号更新商户同步状态失败");
      }
  }
    @Override
    public int successUpdateUpMchtSynRes(String upTableName, String tpamPagyMchtNo, String date,String upMchtSynFailedRes){
        try {
            return getSqlSession().getMapper(pagyMchtInfoMapperClass).successUpdateUpMchtSynRes(upTableName,tpamPagyMchtNo,date,upMchtSynFailedRes);
        } catch (Exception e) {
            log.error("根据银联商户号更新商户同步状态失败: ", e);
            throw new IfspBizException("9999", "根据银联商户号更新商户同步状态失败");
        }
    }

    @Override
    public int updateSynXwState( String date) {
        try {
            return getSqlSession().getMapper(pagyMchtInfoMapperClass).updateSynXwState(date);
        } catch (Exception e) {
            log.error("按身份证信息查询同步中的银联商户: ", e);
            throw new IfspBizException("9999", "按身份证信息查询同步中的银联商户");
        }
    }

}
