package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtStaffRel;

public interface MchtStaffRelMapper {
    int insert(MchtStaffRel record);

    int insertSelective(MchtStaffRel record);
}