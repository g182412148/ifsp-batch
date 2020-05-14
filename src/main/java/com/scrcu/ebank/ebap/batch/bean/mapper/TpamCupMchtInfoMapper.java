package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamCupMchtInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface TpamCupMchtInfoMapper {
    int deleteByPrimaryKey(String mchntCd);

    int insert(TpamCupMchtInfo record);

    int insertSelective(TpamCupMchtInfo record);

    TpamCupMchtInfo selectByPrimaryKey(String mchntCd);

    int updateByPrimaryKeySelective(TpamCupMchtInfo record);

    int updateByPrimaryKey(TpamCupMchtInfo record);

    List<TpamCupMchtInfo> selectAll();

    List<TpamCupMchtInfo> selectBytTimeAdd(@Param("beginTime") String beginTime, @Param("endTime")String endTime);

    List<TpamCupMchtInfo> selectBytTimeUpdate(@Param("beginTime") String beginTime, @Param("endTime")String endTime);

    List<String> selectNomalMchts();

    void updateStateByMchntCd(@Param("mchntCd") String mchntCd,@Param("date") Date date);
}