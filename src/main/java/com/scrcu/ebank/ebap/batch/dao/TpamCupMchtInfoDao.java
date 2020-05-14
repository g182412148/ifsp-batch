package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.TpamCupMchtInfo;

import java.util.Date;
import java.util.List;

public interface TpamCupMchtInfoDao {
    List<TpamCupMchtInfo> selectAll();

    List<TpamCupMchtInfo> selectBytTimeAdd(String beginTime, String endTime);

    List<TpamCupMchtInfo> selectBytTimeUpdate(String beginTime, String endTime);

    List<String> selectNomalMchts();

    void updateStateByMchntCd(String mchntCd, Date date);

    TpamCupMchtInfo selectByMchtId(String mchtId);

}
