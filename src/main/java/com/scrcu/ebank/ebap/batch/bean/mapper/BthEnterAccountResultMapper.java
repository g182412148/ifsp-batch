package com.scrcu.ebank.ebap.batch.bean.mapper;

import com.scrcu.ebank.ebap.batch.bean.dto.BthEnterAccountResult;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAcc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BthEnterAccountResultMapper {
    int deleteByPrimaryKey(String id);

    int insert(BthEnterAccountResult record);

    int insertSelective(BthEnterAccountResult record);

    BthEnterAccountResult selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BthEnterAccountResult record);

    int updateByPrimaryKey(BthEnterAccountResult record);

    /**
     * 批量插入
     * @param records
     * @return
     */
    int insertBatch(@Param("recordList")List<BthEnterAccountResult> records);

    void clear();
}