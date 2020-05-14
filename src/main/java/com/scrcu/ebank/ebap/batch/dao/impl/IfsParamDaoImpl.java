package com.scrcu.ebank.ebap.batch.dao.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.IfsOrg;
import com.scrcu.ebank.ebap.batch.bean.dto.IfsParam;
import com.scrcu.ebank.ebap.batch.bean.dto.SynchronizeOrgInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.IfsOrgMapper;
import com.scrcu.ebank.ebap.batch.bean.mapper.IfsParamMapper;
import com.scrcu.ebank.ebap.batch.bean.mapper.SynchronizeOrgInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.IfsOrgDao;
import com.scrcu.ebank.ebap.batch.dao.IfsParamDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *名称：<参数管理查询> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Repository("IfsParamDao")
public class IfsParamDaoImpl extends BaseBatisDao implements IfsParamDao {

	private final Class<IfsParamMapper> ifsParamMapper=IfsParamMapper.class;

    @Override
    public IfsParam selectByParamKey(String prarmKey) {
        return super.getSqlSession().getMapper(ifsParamMapper).selectByParamKey(prarmKey);
    }
}
