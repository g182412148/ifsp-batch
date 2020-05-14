package com.scrcu.ebank.ebap.batch.dao.impl;

import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.scrcu.ebank.ebap.batch.bean.dto.BthUnionBrandFeeInfo;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthUnionBrandFeeInfoMapper;
import com.scrcu.ebank.ebap.batch.dao.BthUnionBrandFeeInfoDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: ljy
 * @create: 2018-08-29 14:27
 */
@Slf4j
@Repository
public class BthUnionBrandFeeInfoDaoImpl extends BaseBatisDao implements BthUnionBrandFeeInfoDao {

    private final Class<BthUnionBrandFeeInfoMapper> bthUnionBrandFeeInfoMapperClass = BthUnionBrandFeeInfoMapper.class;

    @Override
    public void deleteByStlmDate(String settleDate) {
        try {
            getSqlSession().getMapper(bthUnionBrandFeeInfoMapperClass).deleteByStlmDate(settleDate);
        } catch (Exception e) {
            log.error("删除品牌服务费表数据失败!!!",e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(), "删除品牌服务费表数据失败" );
        }
    }

    @Override
    public void insertList(List<BthUnionBrandFeeInfo> bthUnionBrandFeeInfoList) {
        try {
            for (BthUnionBrandFeeInfo bthUnionBrandFeeInfo : bthUnionBrandFeeInfoList) {
                getSqlSession().getMapper(bthUnionBrandFeeInfoMapperClass).insert(bthUnionBrandFeeInfo);
            }
        } catch (Exception e) {
            log.error("银联品牌服务数据库入库失败!!!",e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(), "银联品牌服务数据库入库失败" );
        }
    }

    @Override
    public List<BthUnionBrandFeeInfo> selectByStlmDate(String settleDate) {
        try {
            return getSqlSession().getMapper(bthUnionBrandFeeInfoMapperClass).selectByStlmDate(settleDate);
        } catch (Exception e) {
            log.error("根据清算日期查询银联品牌服务数据失败!!!",e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(), "银联品牌服务数据库入库失败" );
        }
    }
}
