package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.ChoCptMap;
import com.scrcu.ebank.ebap.batch.bean.dto.OrgRepeMergCfg;
import com.scrcu.ebank.ebap.batch.bean.dto.OrgRepeMergInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.SpecialChoecenessInfo;

import java.util.Date;
import java.util.List;

public interface OrgRepeMergDao {

	/**
	 * 插入机构撤并记录
	 * @param record
	 * @return
	 */
	int insertOrgRepeMergInfo(OrgRepeMergInfo record);

	/**
	 * 查询机构撤并记录
	 * @param repeOrg
	 * @param mergOrg
	 * @param mergDt
	 * @return
	 */
	OrgRepeMergInfo selectOrgMergInfo(String repeOrg, String mergOrg, Date mergDt);

	/**
	 * 查询所有需要更新的表
	 * @return
	 */
	List<OrgRepeMergCfg> selectAllOrg();

	/**
	 * 更新机构撤并记录
	 * @param tableName
	 * @param param
	 * @param repeOrg
	 * @param mergOrg
	 * @return
	 */
	int updateOrg(String tableName, String param, String repeOrg, String mergOrg);


	/**
	 * 更新机构撤并记录
	 * @param record
	 * @return
	 */
	int updateOrgRepeMergInfo(OrgRepeMergInfo record);

	/**
	 * 查询机构撤并执行状态
	 * @param repeOrg
	 * @param mergOrg
	 * @param mergDt
	 * @return
	 */
	String selectOrgRepeMerg(String repeOrg, String mergOrg, Date mergDt);


	/**
	 * 查询撤消机构下的商户
	 * @param req
	 * @return
	 */
	List<String> selectMchtId(String req);

	/**
	 * 通过商户号更新订单表
	 * @param mchtId
	 * @param merg
	 * @return
	 */
	int updatePayOrderInfo(String mchtId, String merg, String repe);

	/**
	 * 通过商户号更新消费记账流水表
	 * @param mchtId
	 * @param merg
	 * @return
	 */
	int updateKeepAcctInfo(String mchtId, String merg);


	/**
	 * 更新合同信息保证金账户 正式表
	 * @param oldSettleNo
	 * @param newSettleNo
	 * @return
	 */
	int updateMchtContInfoDep(String oldSettleNo, String newSettleNo);

	/**
	 * 更新合同信息保证金账户 临时表
	 * @param oldSettleNo
	 * @param newSettleNo
	 * @return
	 */
	int updateMchtContInfoTempDep(String oldSettleNo, String newSettleNo);

	/**
	 * 更新合同信息待清算账户 正式表
	 * @param oldSettleNo
	 * @param newSettleNo
	 * @return
	 */
	int updateMchtContInfoLiq(String oldSettleNo, String newSettleNo);

	/**
	 * 更新合同信息待清算账户 临时表
	 * @param oldSettleNo
	 * @param newSettleNo
	 * @return
	 */
	int updateMchtContInfoTempLiq(String oldSettleNo, String newSettleNo);

	/**
	 * 更新商户组织关联（收单机构） 正式表
	 * @param reqe
	 * @param merg
	 * @return
	 */
	int updateMchtOrgRel(String reqe, String merg, String orgName);

	/**
	 * 更新商户组织关联（收单机构） 临时表
	 * @param reqe
	 * @param merg
	 * @return
	 */
	int updateMchtOrgRelTemp(String reqe, String merg, String orgName);

	/**
	 * 查询撤消机构数据  专栏机构映射表
	 * @param cptId
	 * @return
	 */
	List<ChoCptMap> selectChoCptMap(String cptId);

	/**
	 * 查询并入机构数据  专栏机构映射表
	 * @param cptId
	 * @return
	 */
	ChoCptMap selectChoCptMap(String cptId, String choId);

	/**
	 * 删除专栏机构映射表
	 * @param cptId
	 * @param choId
	 * @return
	 */
	int deleteChoCptMap(String cptId, String choId);

	/**
	 * 更新专栏机构映射表
	 * @param reqe
	 * @param merg
	 * @return
	 */
	int updateChoCptMap(String reqe, String merg);

	/**
	 * 查询撤消机构数据 优惠专栏表
	 * @param specialParCpt
	 * @return
	 */
	List<SpecialChoecenessInfo> selectSpecialChoecenessInfo(String specialParCpt);


	/**
	 * 更新优惠专栏表
	 * @param specialChoecenessInfo
	 * @return
	 */
	int updateSpecialChoecenessInfo(SpecialChoecenessInfo specialChoecenessInfo);
	/**
	 * 更新服务商表
	 * @param
	 * @return
	 */
	int updateParternBaseInfo(String oldSettleNo, String newSettleNo);
}
