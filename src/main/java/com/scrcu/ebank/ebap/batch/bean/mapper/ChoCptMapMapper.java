package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.ChoCptMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChoCptMapMapper {
    int deleteByPrimaryKey(@Param("cptId") String cptId, @Param("choId") String choId);

    int insert(ChoCptMap record);

    int insertSelective(ChoCptMap record);

    ChoCptMap selectByPrimaryKey(@Param("cptId") String cptId, @Param("choId") String choId);

    int updateByPrimaryKeySelective(ChoCptMap record);

    int updateByPrimaryKey(ChoCptMap record);

    List<ChoCptMap> selectChoCptMap(String cptId);

    int updateChoCptMap(@Param("reqe")String reqe, @Param("merg")String merg);
}