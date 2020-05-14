package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMchtListTempInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BthMchtListTempInfoMapper {
    int deleteByPrimaryKey(String mchtId);

    int insert(BthMchtListTempInfo record);

    int insertSelective(BthMchtListTempInfo record);

    BthMchtListTempInfo selectByPrimaryKey(String mchtId);

    int updateByPrimaryKeySelective(BthMchtListTempInfo record);

    int updateByPrimaryKey(BthMchtListTempInfo record);

    int countByChkDate(@Param("chkDate")String chkDate);

    int truncateTable();

    int pullMchtInfo(@Param("chkDate")String chkDate);

    List<BthMchtListTempInfo> queryByRange(@Param("minIndex")int minIndex, @Param("maxIndex")int maxIndex);
}