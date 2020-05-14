package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.PagyMchtInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface PagyMchtInfoMapper {
    int deleteByPrimaryKey(String pagyMchtNo);

    int insert(PagyMchtInfo record);

    int insertSelective(PagyMchtInfo record);

    PagyMchtInfo selectByPrimaryKey(String pagyMchtNo);

    int updateByPrimaryKeySelective(PagyMchtInfo record);

    int updateByPrimaryKey(PagyMchtInfo record);

    List<String> selectAllMchtNo(@Param("pagyNos") Set<String> pagyNos);

    PagyMchtInfo selectByMchntCd(String split);

    void updateStat(@Param("mchtNo") String mchtNo, @Param("date")Date date);

    void updatePagyMchtStat(@Param("pagyMchtNo") String pagyMchtNo, @Param("state")String state);

    void updateUpMchtSynState(@Param("pagyMchtNo") String pagyMchtNo, @Param("state")String state,@Param("curDate")String curDate);
    void updateUpMchtSynStateOld(@Param("pagyMchtNo") String pagyMchtNo, @Param("state")String state,@Param("curDate")String curDate);

    int updateUpMchtSynRes(@Param("upTableName")String upTableName,@Param("tpamPagyMchtNo")String tpamPagyMchtNo,@Param("date")String date,@Param("upMchtSynState")String upMchtSynState,@Param("upMchtSynFailedRes")String upMchtSynFailedRes);
    int successUpdateUpMchtSynRes(@Param("upTableName")String upTableName,@Param("tpamPagyMchtNo")String tpamPagyMchtNo,@Param("date")String date,@Param("upMchtSynFailedRes")String upMchtSynFailedRes);

     List<PagyMchtInfo> selectByState(List<String> stateList);

    int updateSynXwState(@Param("date")String date);
}