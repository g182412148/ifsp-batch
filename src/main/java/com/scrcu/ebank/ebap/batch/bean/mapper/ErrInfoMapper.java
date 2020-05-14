package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.ErrInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ErrInfoMapper {


    List<ErrInfo> getWxErrInfo(Date recoDate);

    List<ErrInfo> getAliErrInfo(Date recoDate);

    List<ErrInfo> getUnionErrInfo(Date recoDate);

    List<ErrInfo> getErrFileInfo();

    int insertErrInfoBatch(@Param("list")List<ErrInfo> list);

    int updateErrInfoBatch(@Param("list")List<ErrInfo> list);
}
