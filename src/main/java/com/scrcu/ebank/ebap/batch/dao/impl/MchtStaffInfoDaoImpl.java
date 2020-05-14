package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtStaffInfo;
//import com.scrcu.ebank.ebap.batch.bean.mapper.MchtStaffInfoMapper;
import com.scrcu.ebank.ebap.batch.bean.mapper.MchtBaseInfoMapper;
import com.scrcu.ebank.ebap.batch.bean.mapper.MchtStaffInfoMapper;
import com.scrcu.ebank.ebap.batch.bean.mapper.MchtStaffInfoTempMapper;
import com.scrcu.ebank.ebap.batch.bean.vo.ecif.ECIFMchtResultVo;
import com.scrcu.ebank.ebap.batch.dao.MchtStaffInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>名称 : 商户人员信息临时表Impl </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/8/14 </p>
 */
@Repository
public class MchtStaffInfoDaoImpl extends BaseBatisDao implements MchtStaffInfoDao {

    private Class<MchtStaffInfoTempMapper> mchtStaffInfoTempMapper = MchtStaffInfoTempMapper.class;
	private Class<MchtStaffInfoMapper> mchtStaffInfoMapper = MchtStaffInfoMapper.class;
	private Class<MchtBaseInfoMapper> mchtBaseInfoMapper = MchtBaseInfoMapper.class;


	@Override
	public List<MchtStaffInfo> selectByMchtId(String mchtId){
		return super.getSqlSession().getMapper(mchtStaffInfoMapper).selectByMchtId(mchtId);
	}
	@Override
	public List<MchtStaffInfo> selectList(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectList(statement, parameter);
	}

	@Override
	public Integer count(String statement, Map<String, Object> parameter) {
		return (Integer)getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public MchtStaffInfo selectOne(String statement, Map<String, Object> parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}

	@Override
	public MchtStaffInfo selectByPrimaryKey(String staffId) {
		return super.getSqlSession().getMapper(mchtStaffInfoMapper).selectByPrimaryKey(staffId);
	}

	@Override
	public List<ECIFMchtResultVo> createMiniUpdateInfo(String settleDate) {
		return super.getSqlSession().getMapper(mchtStaffInfoMapper).createMiniUpdateInfo(settleDate);
	}

	@Override
	public List<ECIFMchtResultVo> createNormalUpdateInfo(String settleDate) {
		return super.getSqlSession().getMapper(mchtStaffInfoMapper).createNormalUpdateInfo(settleDate);
	}


}
