package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtStaffInfo;
import com.scrcu.ebank.ebap.batch.bean.vo.ecif.ECIFMchtResultVo;

import java.util.List;
import java.util.Map;

/**
 * <p>名称 : 商户人员信息临时表Dao </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/8/14 </p>
 */
public interface MchtStaffInfoDao {
    List<MchtStaffInfo> selectByMchtId(String mchtId);
    List<MchtStaffInfo> selectList(String statement, Map<String, Object> parameter);

    public Integer count(String statement, Map<String, Object> parameter);

    MchtStaffInfo selectOne(String statement, Map<String, Object> parameter);
	    /**
     * 通过编号(staffId)查询
     *
     * @param staffId 编号
     * @return 查询DTO
     * @author M.chen
     */
    MchtStaffInfo selectByPrimaryKey(String staffId);



    /**
     * 小微商户-查询更新/新增的商户及相关负责人的ECIF同步信息
     *
     * @return ECIF同步信息
     */
    List<ECIFMchtResultVo> createMiniUpdateInfo(String settleDate);

    /**
     * 普通商户-查询更新/新增的商户及相关负责人的ECIF同步信息
     *
     * @return ECIF同步信息
     */
    List<ECIFMchtResultVo> createNormalUpdateInfo(String settleDate);
}
