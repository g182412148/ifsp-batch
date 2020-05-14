package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.List;
import java.util.Map;

import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.bean.dto.BthWxFileDet;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthWxFileDetMapper;
import com.scrcu.ebank.ebap.batch.dao.BthWxFileDetDao;
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
@Repository("bthWxFileDetDao")
public class BthWxFileDetDaoImpl extends BaseBatisDao implements BthWxFileDetDao {
	private final Class<BthWxFileDetMapper> bthWxFileDetMapper=BthWxFileDetMapper.class;
		
	@Override
	public int deleteBypagyNoAndDate(String pagyNo, String settleDate) {
		return super.getSqlSession().getMapper(bthWxFileDetMapper).deleteBypagyNoAndDate(pagyNo,settleDate);
	}

	@Override
	public int insertSelectiveList(List<BthWxFileDet> bthWxFileDetList) {
		for (BthWxFileDet bthWxFileDet : bthWxFileDetList) {
			getSqlSession().getMapper(bthWxFileDetMapper).insertSelective(bthWxFileDet);
		}
		return 0;
	}

	@Override
	public List<BthWxFileDet> selectByDate(String settleDate) {
		return super.getSqlSession().getMapper(bthWxFileDetMapper).selectByDate(settleDate);
	}

	@Override
	public int updateByPrimaryKeySelective(BthWxFileDet bthWxFileDet) {
		return super.getSqlSession().getMapper(bthWxFileDetMapper).updateByPrimaryKeySelective(bthWxFileDet);
	}

	@Override
	public List<BthWxFileDet> selectByDateAndStat(String settleDate, String state) {
		return super.getSqlSession().getMapper(bthWxFileDetMapper).selectByDateAndStat(settleDate,state);
	}

	@Override
	public List<BthWxFileDet> selectByDateAndChkstat(String doubtDate, String chkSt) {
		return super.getSqlSession().getMapper(bthWxFileDetMapper).selectByDateAndChkstat(doubtDate,chkSt);
	}

	@Override
	public List<BthWxFileDet> selectList(String statement, Map<String, Object> msg) {
		return getSqlSession().selectList(statement, msg); 
	}

	@Override
	public int delete(String string, Map<String, Object> msgs) {
		return getSqlSession().delete(string, msgs);
	}

    @Override
    public void updateChkStRstByOrderNo(String orderNo) {
        try {
            getSqlSession().getMapper(bthWxFileDetMapper).updateChkStRstByOrderNo(orderNo);
        } catch (Exception e) {
            log.error("根据商户订单号更新微信文件明细表失败",e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"根据商户订单号更新微信文件明细表失败");
        }
    }

}
