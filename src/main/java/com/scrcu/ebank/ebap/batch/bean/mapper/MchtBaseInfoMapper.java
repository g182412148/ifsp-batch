package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtBaseInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtBaseInfoVo;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtExtInfo;
import org.apache.ibatis.annotations.Param;

public interface MchtBaseInfoMapper {
    int deleteByPrimaryKey(String mchtId);

    int insert(MchtBaseInfo record);

    int insertSelective(MchtBaseInfo record);

    MchtBaseInfo selectByPrimaryKey(String mchtId);

    int updateByPrimaryKeySelective(MchtBaseInfo record);

    int updateByPrimaryKey(MchtBaseInfo record);

    List<MchtBaseInfo> selectAllInfo(Map parameter);

	List<MchtBaseInfo> querySubbranchByMchtId(@Param("mchtId") String mchtId);

	MchtBaseInfo queryById(@Param("mchtId") String mchtId);

	List<MchtBaseInfoVo> queryMchtTypeByMchtId(String mchtId);
     MchtExtInfo queryMchtExtInfoByMchtId(String mchtId);

    List<MchtBaseInfo> selectUntradedMerchants(@Param("orderTM") String orderTM,@Param("months") String months);


}