package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtStaffInfo;
import com.scrcu.ebank.ebap.batch.bean.vo.ecif.ECIFMchtResultVo;

import java.util.List;

public interface MchtStaffInfoMapper {
    MchtStaffInfo selectByPrimaryKey(String staffId);
    List<MchtStaffInfo> selectByMchtId(String mchtId);

    List<ECIFMchtResultVo> createNormalUpdateInfo(String settleDate);
    List<ECIFMchtResultVo> createMiniUpdateInfo(String settleDate);

}