package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtPayStatisticsInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtPayStatisticsInfoResp;
import com.scrcu.ebank.ebap.batch.bean.mapper.MchtPayStatisticsInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.MchtPayStatisticsInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("mchtPayStatisticsInfoDao")
public class MchtPayStatisticsInfoDaoImpl extends BaseBatisDao implements MchtPayStatisticsInfoDao {

    private final Class<MchtPayStatisticsInfoMapper> mchtPayStatisticsInfoMapper = MchtPayStatisticsInfoMapper.class;

    @Override
    public int insert(MchtPayStatisticsInfo mchtPayStatisticsInfo){
        return getSqlSession().getMapper(mchtPayStatisticsInfoMapper).insert(mchtPayStatisticsInfo);
    }

    @Override
    public int insertSelective(MchtPayStatisticsInfo mchtPayStatisticsInfo){
        return getSqlSession().getMapper(mchtPayStatisticsInfoMapper).insertSelective(mchtPayStatisticsInfo);
    }

    @Override
    public int insertBatch(List<MchtPayStatisticsInfo> list){
        return getSqlSession().getMapper(mchtPayStatisticsInfoMapper).insertBatch(list);
    }
    @Override
    public List<MchtPayStatisticsInfoResp> selectMchtIdAndTime(String chlMchtNo, String time){
        return getSqlSession().getMapper(mchtPayStatisticsInfoMapper).selectMchtIdAndTime(chlMchtNo, time);
    }

    @Override
    public List<MchtPayStatisticsInfoResp> queryTimeQuanTum(String mchtId, String startDate, String endDate, String timeQuanTum) {
        if(timeQuanTum!=null && !"".equals(timeQuanTum)){
            timeQuanTum = "%"+timeQuanTum+"%";
        }
        return getSqlSession().getMapper(mchtPayStatisticsInfoMapper).queryTimeQuanTum(mchtId, startDate, endDate, timeQuanTum);
    }

	@Override
	public long insertToMchtPayInfo(Map<String, Object> map) {
		return getSqlSession().getMapper(mchtPayStatisticsInfoMapper).insertToMchtPayInfo(map);
	}
    
}
