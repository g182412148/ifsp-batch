package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtBlackGreyInfo;

import java.util.List;
import java.util.Map;


public interface MchtBlackGreyInfoDao {

	int deleteByPrimaryKey(String id);

	int insert(MchtBlackGreyInfo record);

	int insertSelective(MchtBlackGreyInfo record);

	MchtBlackGreyInfo selectByPrimaryKey(String id);

	int updateByPrimaryKeySelective(MchtBlackGreyInfo record);

	int updateByPrimaryKey(MchtBlackGreyInfo record);

	MchtBlackGreyInfo selectByMchtId(String mchtId);
	
	MchtBlackGreyInfo queryStateByMchtId(String mchtId);
}
