package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.mapper.*;
import com.scrcu.ebank.ebap.batch.bean.request.SelectOrgRepeMergRequest;
import com.scrcu.ebank.ebap.batch.dao.OrgRepeMergDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository("OrgRepeMergDao")
public class OrgRepeMergDaoImpl extends BaseBatisDao implements OrgRepeMergDao {

	private final Class<OrgRepeMergInfoMapper> orgRepeMergInfoMapper=OrgRepeMergInfoMapper.class;
	private final Class<OrgRepeMergCfgMapper> orgRepeMergCfgMapper=OrgRepeMergCfgMapper.class;
	private final Class<OrgRepeMergMapper> orgRepeMergMapper=OrgRepeMergMapper.class;
	private final Class<PayOrderInfoMapper> payOrderInfoMapper=PayOrderInfoMapper.class;
	private final Class<KeepAcctInfoMapper> keepAcctInfoMapper=KeepAcctInfoMapper.class;
	private final Class<ChoCptMapMapper> choCptMapMapper=ChoCptMapMapper.class;
	private final Class<SpecialChoecenessInfoMapper> specialChoecenessInfoMapper=SpecialChoecenessInfoMapper.class;
	private final Class<MchtOrgRelMapper> mchtOrgRelMapper=MchtOrgRelMapper.class;
	private final Class<MchtOrgRelTempMapper> mchtOrgRelTempMapper=MchtOrgRelTempMapper.class;
	private final Class<MchtContInfoMapper> mchtContInfoMapper=MchtContInfoMapper.class;
	private final Class<MchtContInfoTempMapper> mchtContInfoTempMapper=MchtContInfoTempMapper.class;
	private final Class<ParternBaseInfoMapper> parternBaseInfoMapper=ParternBaseInfoMapper.class;

	@Override
	public int insertOrgRepeMergInfo(OrgRepeMergInfo record){
		return super.getSqlSession().getMapper(orgRepeMergInfoMapper).insertSelective(record);
	}

	@Override
	public OrgRepeMergInfo selectOrgMergInfo(String repeOrg,String mergOrg,Date mergDt){
		return super.getSqlSession().getMapper(orgRepeMergInfoMapper).selectOrgMergInfo(repeOrg,mergOrg,mergDt);
	}


	@Override
	public List<OrgRepeMergCfg> selectAllOrg(){
		return super.getSqlSession().getMapper(orgRepeMergCfgMapper).selectAllOrg();
	}

	@Override
	public int updateOrg(String tableName,String param,String repeOrg,String mergOrg){
		return super.getSqlSession().getMapper(orgRepeMergMapper).updateOrg(tableName,param,repeOrg,mergOrg);
	}

	@Override
	public int updateOrgRepeMergInfo(OrgRepeMergInfo record){
		return super.getSqlSession().getMapper(orgRepeMergInfoMapper).updateByPrimaryKeySelective(record);
	}

	@Override
	public String selectOrgRepeMerg(String repeOrg,String mergOrg,Date mergDt) {
		return super.getSqlSession().getMapper(orgRepeMergInfoMapper).selectOrgRepeMerg(repeOrg,mergOrg,mergDt);
	}

	@Override
	public List<String> selectMchtId(String orgId) {
		return super.getSqlSession().getMapper(mchtOrgRelMapper).selectMchtId(orgId);
	}

	@Override
	public int updatePayOrderInfo(String mchtId, String merg, String repe) {
		return super.getSqlSession().getMapper(payOrderInfoMapper).updatePayOrderInfo(mchtId, merg, repe);
	}

	@Override
	public int updateKeepAcctInfo(String mchtId, String merg) {
		return super.getSqlSession().getMapper(keepAcctInfoMapper).updateKeepAcctInfo(mchtId,merg);
	}

	@Override
	public int updateMchtContInfoDep(String oldSettleNo,String newSettleNo) {
		return super.getSqlSession().getMapper(mchtContInfoMapper).updateMchtContInfoDep(oldSettleNo,newSettleNo);
	}

	@Override
	public int updateMchtContInfoTempDep(String oldSettleNo,String newSettleNo) {
		return super.getSqlSession().getMapper(mchtContInfoTempMapper).updateMchtContInfoTempDep(oldSettleNo,newSettleNo);
	}

	@Override
	public int updateMchtContInfoLiq(String oldSettleNo,String newSettleNo) {
		return super.getSqlSession().getMapper(mchtContInfoMapper).updateMchtContInfoLiq(oldSettleNo,newSettleNo);
	}

	@Override
	public int updateMchtContInfoTempLiq(String oldSettleNo,String newSettleNo) {
		return super.getSqlSession().getMapper(mchtContInfoTempMapper).updateMchtContInfoTempLiq(oldSettleNo,newSettleNo);
	}

	@Override
	public int updateMchtOrgRel(String reqe, String merg,String orgName) {
		return super.getSqlSession().getMapper(mchtOrgRelMapper).updateMchtOrgRel(reqe,merg,orgName);
	}

	@Override
	public int updateMchtOrgRelTemp(String reqe, String merg,String orgName) {
		return super.getSqlSession().getMapper(mchtOrgRelTempMapper).updateMchtOrgRelTemp(reqe,merg,orgName);
	}

	@Override
	public List<ChoCptMap> selectChoCptMap(String cptId) {
		return super.getSqlSession().getMapper(choCptMapMapper).selectChoCptMap(cptId);
	}

	@Override
	public ChoCptMap selectChoCptMap(String cptId,String choId) {
		return super.getSqlSession().getMapper(choCptMapMapper).selectByPrimaryKey(cptId,choId);
	}

	@Override
	public int deleteChoCptMap(String cptId,String choId) {
		return super.getSqlSession().getMapper(choCptMapMapper).deleteByPrimaryKey(cptId,choId);
	}

	@Override
	public int updateChoCptMap(String reqe, String merg) {
		return super.getSqlSession().getMapper(choCptMapMapper).updateChoCptMap(reqe,merg);
	}

	@Override
	public List<SpecialChoecenessInfo> selectSpecialChoecenessInfo(String specialParCpt) {
		return super.getSqlSession().getMapper(specialChoecenessInfoMapper).selectSpecialChoecenessInfo("%"+specialParCpt+"%");
	}

	@Override
	public int updateSpecialChoecenessInfo(SpecialChoecenessInfo specialChoecenessInfo) {
		return super.getSqlSession().getMapper(specialChoecenessInfoMapper).updateByPrimaryKeySelective(specialChoecenessInfo);
	}

	@Override
	public int updateParternBaseInfo(String oldSettleNo, String newSettleNo) {
		return super.getSqlSession().getMapper(parternBaseInfoMapper).updateParternBaseInfo(oldSettleNo,newSettleNo);
	}
}
