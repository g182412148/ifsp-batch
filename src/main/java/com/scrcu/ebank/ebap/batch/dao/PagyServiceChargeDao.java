package com.scrcu.ebank.ebap.batch.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.bean.dto.*;

public interface PagyServiceChargeDao {

	List<BthChkRsltInfo> selectBthChkRsltInfoByPagySysNoAndSettleDate(String pagySysNo, String settleDate);

	BthPagyLocalInfo queryLocalInfoByPagyPayTxnSsn(String pagyPayTxnSsn);

	PagyFeeCfgDet queryPagyFeeCfgDetByPagyNoAndPagySysNoAndCardType(String pagyNo, String pagySysNo, String cardType);

	void saveBthPagyChkErrInfo(BthPagyChkErrInfo bthPagyChkErrInfo);

	void deletePagyChkErrInfoBySettleDateAndErrTp(String settleDate, String errTp);

	BthAliFileDet queryAliFileDetByOrderNo(String pagyPayTxnSsn);

	BthUnionFileDet queryUnionFileDetByOrderNo(String proxyInsCode, String sendInsCode, String traceNum, String transDate);

	PagyFeeCfgDet queryPagyFeeCfgDetByPagyNoAndPagySysNoAndCardTypeAndOrderSsn(String pagyNo, String pagySysNo, String cardType, String orderSsn);

    /**
     * 根据对账成功日期与通道系统编号查询出通道流水号
     * @param pagySysNo
     * @param recoDate
     * @return
     */
    List<String> queryByPagySysNoAndSuccDate(String pagySysNo, Date recoDate);

    /**
     * 根据通道编号查询费率配置信息
     * @param pagyNo
     * @return
     */
    PagyFeeCfgDet queryPagyFeeCfgByPagyNo(String pagyNo);

    /**
     * 根据通道流水
     * @param pagyPayTxnSsn
     * @return
     */
    WxBillOuter queryWxBillOuter(String pagyPayTxnSsn);

    /**
     * 根据日期与类型删除差错信息
     * @param chkErrDt
     * @param errTp
     * @param pagySysNo
     * @return
     */
    int deleteByDateErrTpAndPagySysNo(Date chkErrDt, String errTp, String pagySysNo);

    /**
     * 根据通道流水查询微信对账文件数据
     * @param pagyPayTxnSsn
     * @return
     */
    BthWxFileDet queryWxByOrderNo(String pagyPayTxnSsn);

    /**
     * 根据通道流水查询支付宝对账文件数据
     * @param pagyPayTxnSsn
     * @return
     */
    AliBillOuter queryAliBillOuter(String pagyPayTxnSsn);
    Map<String, String> getUpacpOrderId(String pagyTxnSsn);
    Map<String, String> getCupQrcOrderId(String pagyTxnSsn);
    Map<String, String> getAlipayOrderId(String pagyTxnSsn);
    Map<String, String> getWechatOrderId(String pagyTxnSsn);

}
