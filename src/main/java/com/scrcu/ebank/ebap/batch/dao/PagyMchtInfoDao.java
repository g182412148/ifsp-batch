package com.scrcu.ebank.ebap.batch.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.scrcu.ebank.ebap.batch.bean.dto.PagyMchtInfo;

public interface PagyMchtInfoDao {

	List<PagyMchtInfo> selectList(String string, Map<String, Object> params);

    List<String> selectAllMchtNo(Set<String> pagySysNo);

    PagyMchtInfo selectByMchntCd(String split);

    void updateStat(String MchtNo, Date date);
    void updatePagyMchtStat(String mchtNo, String state);
    void updateUpMchtSynState(String mchtNo, String state,String curDate);
    void updateUpMchtSynStateOld(String pagyMchtNo, String code, String curDate);

     int updateUpMchtSynRes(String upTableName,String tpamPagyMchtNo, String date, String upMchtSynState,String upMchtSynFailedRes);
     int successUpdateUpMchtSynRes(String upTableName,String tpamPagyMchtNo, String date,String upMchtSynFailedRes);
     int updateSynXwState(String date);
    List<PagyMchtInfo> selectByState(List<String> stateList);
}
