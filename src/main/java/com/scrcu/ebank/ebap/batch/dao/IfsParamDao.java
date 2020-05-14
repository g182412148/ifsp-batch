package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.IfsParam;
import com.scrcu.ebank.ebap.batch.bean.dto.PagyMchtInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IfsParamDao {

	IfsParam selectByParamKey(String string);
}
