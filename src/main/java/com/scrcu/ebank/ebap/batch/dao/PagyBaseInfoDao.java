package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.PagyBaseInfo;

import java.util.List;

public interface PagyBaseInfoDao {


    List<PagyBaseInfo> selectByPagyNo(String wxSysNo);
}
