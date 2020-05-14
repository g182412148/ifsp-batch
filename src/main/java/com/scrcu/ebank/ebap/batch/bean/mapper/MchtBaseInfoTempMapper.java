package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtBaseInfoTemp;

public interface MchtBaseInfoTempMapper {
    int deleteByPrimaryKey(String mchtId);

    int insert(MchtBaseInfoTemp record);

    int insertSelective(MchtBaseInfoTemp record);

    MchtBaseInfoTemp selectByPrimaryKey(String mchtId);

    int updateByPrimaryKeySelective(MchtBaseInfoTemp record);

    int updateByPrimaryKey(MchtBaseInfoTemp record);
    /**
     * 根据商户号查询商户性质
     * @param mchtId
     * @return by.qzhang
     */
    String mchtIdToNat(String mchtId);

    /**
     * 根据商户号查询上级商户号
     * @param mchtId
     * @return by.qzhang
     */
    List<String> selectParId(String mchtId);

    /**
     * 根据商户号查询商户类型
     * @param mchtId
     * @return by.qzhang
     */
    String selectMchtType(String mchtId);

    /**
     * 查找有更新动作的审核时间
     * @param mchtId
     * @return
     */
    Map<String,String> selectUpdAuditTM(String mchtId);

    /**
     * 查询错误原因描述
     * @param examRefuseReason
     * @return
     */
    String selectRefuReson(String examRefuseReason);

    /**
     * 查询银联地区码
     * @param areaCode
     * @return
     */
    String selectBankAreaNo(String areaCode);
}