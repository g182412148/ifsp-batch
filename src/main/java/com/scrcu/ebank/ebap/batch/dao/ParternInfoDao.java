package com.scrcu.ebank.ebap.batch.dao;

import com.scrcu.ebank.ebap.batch.bean.dto.ParternInfo;

import java.util.List;

/**
 * @ClassName ParternInfoDao
 * @Description TODO
 * @Author NiklausZhu
 * @Date 2020/4/1 11:19
 **/
public interface ParternInfoDao {
    int deleteByPrimaryKey(String parternId);

    int insert(ParternInfo record);

    int insertSelective(ParternInfo record);

    ParternInfo selectByPrimaryKey(String parternId);

    int updateByPrimaryKeySelective(ParternInfo record);

    int updateByPrimaryKey(ParternInfo record);

    List<ParternInfo> selectParternList(List<String>parternCodeList);
}
