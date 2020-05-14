package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtStaffRelTemp;

public interface MchtStaffRelTempMapper {

    /**
     * 根据人员编号查找人员关联表记录id
     * @param staffId,mchtId
     * @return
     */
    List<String> selectRecordId(@Param("staffId") String staffId, @Param("mchtId") String mchtId);
    /**
     * 查询员工关联临时表
     * @param mchtId
     * @return
     */
	List<MchtStaffRelTemp> selectMchtStaffRelTempList(String mchtId);

	List<MchtStaffRelTemp> selectMchtStaffRelTempList_twoRole(String mchtId);

    /**
     * 更新商户人员关联表
     * @param mchStaffRel
     * @return
     */
    int updateMchtStaffRel(MchtStaffRelTemp mchStaffRel);

}