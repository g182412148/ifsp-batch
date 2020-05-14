package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMchtListTempInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.IfsStaffOrgRel;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthMchtListTempInfoMapper;
import com.scrcu.ebank.ebap.batch.bean.mapper.IfsStaffOrgRelMapper;
import com.scrcu.ebank.ebap.batch.dao.BthMchtListTempInfoDao;
import com.scrcu.ebank.ebap.batch.dao.IfsStaffOrgRelDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Repository("bthMchtListTempInfoDao")
public class BthMchtListTempInfoDaoImp extends BaseBatisDao implements BthMchtListTempInfoDao {
	private final Class<BthMchtListTempInfoMapper> bthMchtListTempInfoMapperClass=BthMchtListTempInfoMapper.class;
	
	@Override
	public int insert(BthMchtListTempInfo bthMchtListTempInfo) {
		return super.getSqlSession().getMapper(bthMchtListTempInfoMapperClass).insert(bthMchtListTempInfo);
	}

	@Override
	public int update(BthMchtListTempInfo bthMchtListTempInfo) {
		return super.getSqlSession().getMapper(bthMchtListTempInfoMapperClass).updateByPrimaryKeySelective(bthMchtListTempInfo);
	}


	@Override
	public int countByChkDate(String chkDate) {
		return super.getSqlSession().getMapper(bthMchtListTempInfoMapperClass).countByChkDate(chkDate);
	}

	/**
	 * 清空表数据
	 * @return
	 */
	@Override
	public int truncateTable() {
		return super.getSqlSession().getMapper(bthMchtListTempInfoMapperClass).truncateTable();
	}

	/**
	 * 从商户表中抽取需要处理的商户信息到待处理表中
	 * @param chkDate
	 * @return
	 */
	@Override
	public int pullMchtInfo(String chkDate) {
		return super.getSqlSession().getMapper(bthMchtListTempInfoMapperClass).pullMchtInfo(chkDate);
	}

	@Override
	public List<BthMchtListTempInfo> queryByRange(int minIndex, int maxIndex){
		return super.getSqlSession().getMapper(bthMchtListTempInfoMapperClass).queryByRange(minIndex,maxIndex);

	}
}
