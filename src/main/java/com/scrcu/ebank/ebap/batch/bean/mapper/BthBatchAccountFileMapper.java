package com.scrcu.ebank.ebap.batch.bean.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.scrcu.ebank.ebap.batch.bean.dto.BthBatchAccountFile;

public interface BthBatchAccountFileMapper {
    int deleteByPrimaryKey(String id);

    int insert(BthBatchAccountFile record);

    int insertSelective(BthBatchAccountFile record);

    BthBatchAccountFile selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BthBatchAccountFile record);

    int updateByPrimaryKey(BthBatchAccountFile record);

	List<BthBatchAccountFile> queryByDealtatus(@Param("stus") String stus);

	List<BthBatchAccountFile> queryByDealtatuss(@Param("fileStatus00") String fileStatus00,@Param("fileStatus01") String fileStatus01,@Param("fileStatus03") String fileStatus03);
}