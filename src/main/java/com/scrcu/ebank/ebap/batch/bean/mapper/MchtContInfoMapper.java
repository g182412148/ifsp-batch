package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtContInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MchtContInfoMapper {
    int deleteByPrimaryKey(String contNo);

    int insert(MchtContInfo record);

    int insertSelective(MchtContInfo record);

    MchtContInfo selectByPrimaryKey(String contNo);

    int updateByPrimaryKeySelective(MchtContInfo record);

    int updateByPrimaryKey(MchtContInfo record);

    List<MchtContInfo> selectAllInfo();

	MchtContInfo queryByMchtId(String mchtId);

    int updateMchtContInfoDep(@Param("oldSettleNo")String oldSettleNo, @Param("newSettleNo")String newSettleNo);

    int updateMchtContInfoLiq(@Param("oldSettleNo")String oldSettleNo, @Param("newSettleNo")String newSettleNo);
}