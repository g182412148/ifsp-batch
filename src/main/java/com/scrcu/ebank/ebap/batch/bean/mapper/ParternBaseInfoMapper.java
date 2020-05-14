package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.ParternBaseInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.ParternDepInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ParternBaseInfoMapper {

    int insert(ParternBaseInfo record);

    /**
     * 查询合作商列表
     * @param request
     * @return
     */

	/**
	 * 查询合作商基本信息
	 * @param parternCode
	 * @return
	 */
	ParternBaseInfo selectParternBaseInfo(String parternCode);

	List<ParternDepInfo> selectParternBaseInfoList(Map<String, Object> parameter);
	int updateParternBaseInfo(@Param("oldSettleNo")String oldSettleNo, @Param("newSettleNo")String newSettleNo);
}
