package com.scrcu.ebank.ebap.batch.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.BthPagyChkInf;

public interface BthPagyChkInfDao {

	int deleteBypagyNoAndDate(String pagyNo, Date date);

	int updateByPrimaryKeySelective(BthPagyChkInf bthPagyChkInf);

	int insertSelective(BthPagyChkInf bthPagyChkInf);

    List<BthPagyChkInf> selectByPagyNoAndDate(Map<String,Object> mapParam);
}
