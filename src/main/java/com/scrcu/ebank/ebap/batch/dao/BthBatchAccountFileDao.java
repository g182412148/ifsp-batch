package com.scrcu.ebank.ebap.batch.dao;

import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.BthBatchAccountFile;

public interface BthBatchAccountFileDao {

    void insertSelective(BthBatchAccountFile f);

	List<BthBatchAccountFile> queryByDealtatus(String stus);
	
	int updateByPrimaryKeySelective(BthBatchAccountFile record);
	
	int update(String statement,Map<String,Object> parameter);
	
	int update(String statement,BthBatchAccountFile accFileInfo);

    List<BthBatchAccountFile> selectList(String statement,Map<String,Object> parameter);
    
    BthBatchAccountFile selectOne(String statement,Map<String,Object> parameter);

	List<BthBatchAccountFile> queryByDealtatuss(String fileStatus00, String fileStatus01, String fileStatus03);
}
