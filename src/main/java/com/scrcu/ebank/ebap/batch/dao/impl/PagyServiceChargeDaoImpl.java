package com.scrcu.ebank.ebap.batch.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.mapper.*;
import com.scrcu.ebank.ebap.batch.dao.BthWxFileDetDao;
import org.springframework.stereotype.Repository;

import com.scrcu.ebank.ebap.batch.dao.PagyServiceChargeDao;
import com.scrcu.ebank.ebap.common.dao.BaseBatisDao;
/**
 *名称：<通道流水抽取Impl> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/6/20 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Repository("PagyServiceChargeDaoImpl")
public class PagyServiceChargeDaoImpl extends BaseBatisDao implements PagyServiceChargeDao {
	private final Class<BthChkRsltInfoMapper> bthChkRsltInfoMapper=BthChkRsltInfoMapper.class;

	private final Class<BthPagyLocalInfoMapper> bthPagyLocalInfoMapper=BthPagyLocalInfoMapper.class;

	private final Class<PagyFeeCfgDetMapper> pagyFeeCfgDetMapper=PagyFeeCfgDetMapper.class;

	private final Class<BthPagyChkErrInfoMapper> bthPagyChkErrInfoMapper=BthPagyChkErrInfoMapper.class;

	private final Class<BthAliFileDetMapper> bthAliFileDetMapper=BthAliFileDetMapper.class;

	private final Class<BthUnionFileDetMapper> bthUnionFileDetMapper=BthUnionFileDetMapper.class;

    private final Class<WxBillOuterMapper> wxBillOuterMapper=WxBillOuterMapper.class;

    private final Class<AliBillOuterMapper> aliBillOuterMapper=AliBillOuterMapper.class;

    private final Class<BthWxFileDetMapper> bthWxFileDetMapper=BthWxFileDetMapper.class;

	@Override
	public List<BthChkRsltInfo> selectBthChkRsltInfoByPagySysNoAndSettleDate(String pagySysNo, String settleDate) {
		return super.getSqlSession().getMapper(bthChkRsltInfoMapper).selectBthChkRsltInfoByPagySysNoAndSettleDate(pagySysNo,settleDate);
	}

	@Override
	public BthPagyLocalInfo queryLocalInfoByPagyPayTxnSsn(String pagyPayTxnSsn) {
		return super.getSqlSession().getMapper(bthPagyLocalInfoMapper).queryLocalInfoByPagyPayTxnSsn(pagyPayTxnSsn);
	}


	@Override
	public PagyFeeCfgDet queryPagyFeeCfgDetByPagyNoAndPagySysNoAndCardType(String pagyNo, String pagySysNo,
			String cardType) {
		return super.getSqlSession().getMapper(pagyFeeCfgDetMapper).queryPagyFeeCfgDetByPagyNoAndPagySysNoAndCardType(pagyNo,pagySysNo,cardType);
	}

	@Override
	public void saveBthPagyChkErrInfo(BthPagyChkErrInfo bthPagyChkErrInfo) {
		
		super.getSqlSession().getMapper(bthPagyChkErrInfoMapper).insertSelective(bthPagyChkErrInfo);
	}

	@Override
	public void deletePagyChkErrInfoBySettleDateAndErrTp(String settleDate, String errTp) {
		super.getSqlSession().getMapper(bthPagyChkErrInfoMapper).deletePagyChkErrInfoBySettleDateAndErrTp(settleDate,errTp);
		
	}

	@Override
	public BthAliFileDet queryAliFileDetByOrderNo(String pagyPayTxnSsn) {
		return super.getSqlSession().getMapper(bthAliFileDetMapper).queryAliFileDetByOrderNo(pagyPayTxnSsn);

	}

	@Override
	public BthUnionFileDet queryUnionFileDetByOrderNo(String proxyInsCode, String sendInsCode, String traceNum, String transDate) {
		return super.getSqlSession().getMapper(bthUnionFileDetMapper).queryUnionFileDetByOrderNo(proxyInsCode, sendInsCode, traceNum, transDate);
	}

    @Override
    public PagyFeeCfgDet queryPagyFeeCfgDetByPagyNoAndPagySysNoAndCardTypeAndOrderSsn(String pagyNo, String pagySysNo, String cardType, String orderSsn) {
        return super.getSqlSession().getMapper(pagyFeeCfgDetMapper).queryPagyFeeCfgDetByPagyNoAndPagySysNoAndCardTypeAndOrderSsn(pagyNo,pagySysNo,cardType,orderSsn);
    }

    @Override
    public List<String> queryByPagySysNoAndSuccDate(String pagySysNo, Date recoDate) {
        return getSqlSession().getMapper(bthChkRsltInfoMapper).queryByPagySysNoAndSuccDate(pagySysNo,recoDate);
    }

    @Override
    public PagyFeeCfgDet queryPagyFeeCfgByPagyNo(String pagyNo) {
        return getSqlSession().getMapper(pagyFeeCfgDetMapper).queryPagyFeeCfgByPagyNo(pagyNo);
    }

    @Override
    public WxBillOuter queryWxBillOuter(String pagyPayTxnSsn) {
        return getSqlSession().getMapper(wxBillOuterMapper).selectByPrimaryKey(pagyPayTxnSsn);
    }

    @Override
    public int deleteByDateErrTpAndPagySysNo(Date chkErrDt, String errTp, String pagySysNo) {
        return getSqlSession().getMapper(bthPagyChkErrInfoMapper).deleteByDateErrTpAndPagySysNo(chkErrDt, errTp,pagySysNo);
    }

    @Override
    public BthWxFileDet queryWxByOrderNo(String pagyPayTxnSsn) {
        return getSqlSession().getMapper(bthWxFileDetMapper).queryWxFileDetByOrderNo(pagyPayTxnSsn);
    }

    @Override
    public AliBillOuter queryAliBillOuter(String pagyPayTxnSsn) {
        return getSqlSession().getMapper(aliBillOuterMapper).selectByPrimaryKey(pagyPayTxnSsn);
    }

	@Override
	public Map<String, String> getUpacpOrderId(String pagyTxnSsn) {
		return getSqlSession().getMapper(wxBillOuterMapper).getUpacpOrderId(pagyTxnSsn);
	}

	@Override
	public Map<String, String> getCupQrcOrderId(String pagyTxnSsn) {
		return getSqlSession().getMapper(wxBillOuterMapper).getCupQrcOrderId(pagyTxnSsn);
	}

	@Override
	public Map<String, String> getAlipayOrderId(String pagyTxnSsn) {
		return getSqlSession().getMapper(wxBillOuterMapper).getAlipayOrderId(pagyTxnSsn);
	}

	@Override
	public Map<String, String> getWechatOrderId(String pagyTxnSsn) {
		return getSqlSession().getMapper(wxBillOuterMapper).getWechatOrderId(pagyTxnSsn);
	}

}
