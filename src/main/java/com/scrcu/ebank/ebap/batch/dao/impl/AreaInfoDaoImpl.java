package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.AreaInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.AreaInfoMapper;
import com.scrcu.ebank.ebap.batch.bean.mapper.IfsOrgMapper;
import com.scrcu.ebank.ebap.batch.dao.AreaInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.springframework.stereotype.Repository;

/**
 * <p>名称 : 地区表查询Dao </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/8/2 </p>
 */
@Repository
public class AreaInfoDaoImpl extends BaseBatisDao implements AreaInfoDao {

    Class<AreaInfoMapper> areaInfoMapper = AreaInfoMapper.class;
    Class<IfsOrgMapper> ifsOrgMapper = IfsOrgMapper.class;

    /**
     * 根据地区号查询地区名称
     *
     * @param areaCode 地区号
     * @return 地区信息
     */
    @Override
    public AreaInfo selectByPrimaryKey(String areaCode) {
        return super.getSqlSession().getMapper(areaInfoMapper).selectByPrimaryKey(areaCode);
    }

}
