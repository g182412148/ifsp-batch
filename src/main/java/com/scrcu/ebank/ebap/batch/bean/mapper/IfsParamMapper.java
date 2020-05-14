package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.IfsParam;
import org.apache.ibatis.annotations.Param;

public interface IfsParamMapper {

    IfsParam selectByParamKey(@Param("paramKey") String paramKey);
}