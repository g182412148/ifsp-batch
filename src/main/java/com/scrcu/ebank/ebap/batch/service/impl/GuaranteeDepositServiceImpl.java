package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.id.IfspId;
import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.ReRunFlagEnum;
import com.scrcu.ebank.ebap.batch.common.dict.RealTmFlagEnum;
import com.scrcu.ebank.ebap.batch.common.dict.RespEnum;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.common.utils.PublicReqParam;
import com.scrcu.ebank.ebap.batch.dao.KeepAccInfoDao;
import com.scrcu.ebank.ebap.batch.dao.MchtBaseInfoDao;
import com.scrcu.ebank.ebap.batch.dao.MchtContInfoDao;
import com.scrcu.ebank.ebap.batch.dao.ParternBaseInfoDao;
import com.scrcu.ebank.ebap.batch.service.GuaranteeDepositService;
import com.scrcu.ebank.ebap.batch.soaclient.KeepAccSoaService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.dubbo.annotation.SoaClient;
import com.scrcu.ebank.ebap.dubbo.consumer.ISoaClient;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import com.scrcu.ebank.ebap.exception.IfspTimeOutException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * <p>名称 :  </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : zhangb </p>
 * <p>日期 : 2019-09-02  15:38 </p>
 */
@Service
@Slf4j
public class GuaranteeDepositServiceImpl implements GuaranteeDepositService {

    @Resource
    private ParternBaseInfoDao parternBaseInfoDao;
    @Resource
    private KeepAccSoaService keepAccSoaService;

    @Resource
    private KeepAccInfoDao keepAccInfoDao;
    @Override
    public CommonResponse guaDep(BatchRequest request) {

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
        commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
        String HDTellrSeqNum = null;
        try{
            Map<String,Object> soaParams = new HashMap<>();
            soaParams.put("parternType", "1");//合作方类型
            List<ParternDepInfo> ParternDepInfoList = parternBaseInfoDao.selectParternBaseInfoList(soaParams);
            for(ParternDepInfo parternInfo:ParternDepInfoList) {
                //补保证金次数
                int guaranteeDeposit = Integer.parseInt(parternInfo.getGuaranteeDeposit());
                Map<String,Object> map = new HashMap<>();
                map.put("guaranteeDeposit", guaranteeDeposit);
                map.put("parternId", parternInfo.getParternId());
                //查询余额
                SoaParams params = new SoaParams();
                params.putAll(PublicReqParam.getParam("YW2001", "1"));
                params.put("custAcctNum", parternInfo.getDepAcctNo());
                SoaResults soaResult = innerAccountSearch(params);
                if (!StringUtils.equals(soaResult.getRespCode(), SystemConfig.getSuccessCode())) {
                    log.warn("调用商户中心查询账号余额接口失败: " + soaResult.getRespCode() + ":" + soaResult.getRespMsg());
//                    visorSoaParams.put("state", "1");
//                    visorSoaParams.put("errorCode",soaResult.getRespCode());
//                    visorSoaParams.put("errorReason",soaResult.getRespMsg());
                    throw new IfspBizException(soaResult.getRespCode(), soaResult.getRespMsg());
                }
                String tojson = IfspFastJsonUtil.tojson(soaResult);
                InnerAccountSearch innerAccountSearch = IfspFastJsonUtil.jsonToobject(tojson, InnerAccountSearch.class);
                BigDecimal depAmt = new BigDecimal(parternInfo.getDepAmt().trim());
                BigDecimal usablBal = new BigDecimal(innerAccountSearch.getUsablBal().trim());
                if (usablBal.compareTo(depAmt) < 0) {
                    //需补金额
                    BigDecimal TransAmt = depAmt.subtract(usablBal);
                    KeepAccInfo keepAccInfo = initKeepInfo(parternInfo, TransAmt.multiply(new BigDecimal(100)));//需补保证金转为分
                    SoaParams param = new SoaParams();
                    initParam(param, keepAccInfo);
                    SoaResults results = keepAccSoaService.keepAcc(param);
                    HDTellrSeqNum = IfspDataVerifyUtil.isBlank(results.get("hDTellrSeqNum")) ? "" : String.valueOf(results.get("hDTellrSeqNum"));

                    Map<String, Object> info = new HashMap<>(5);
                    info.put("subOrderSsn", keepAccInfo.getSubOrderSsn());
                    info.put("realTmFlag", keepAccInfo.getRealTmFlag());
                    info.put("feeAmt", keepAccInfo.getFeeAmt());
                    info.put("commissionAmt", keepAccInfo.getCommissionAmt());
                    info.put("HDTellrSeqNum", HDTellrSeqNum);
                    //respInfo.add(info);


                    if (RespEnum.RESP_SUCCESS.getCode().equals(results.getRespCode())) {
                        log.info("===================>>本行通道受理记账成功<<===========================");
                        keepAccInfo.setState(Constans.KEEP_ACCOUNT_STAT_SUCCESS);
                        keepAccInfo.setPagyRespCode(results.getRespCode());
                        keepAccInfo.setPagyRespMsg(results.getRespMsg());
                        keepAccInfo.setReserved2(HDTellrSeqNum);
                        // 插入时判断唯一索引
                        keepAccInfoDao.insertIgnoreExist(keepAccInfo);
                        if(guaranteeDeposit != 0){
                            map.put("guaranteeDeposit",0);
                            parternBaseInfoDao.update("updateParternDepInfo", map);
                        }

                    } else if (RespEnum.RESP_TIMEOUT.getCode().equals(results.getRespCode())) {
                        log.warn("===================>>调用本行通道记账接口超时<<===========================");
                        // 记为超时 , 待日终改为记账失败
                        keepAccInfo.setState(Constans.KEEP_ACCOUNT_STAT_TIMEOUT);
                        keepAccInfo.setPagyRespCode(results.getRespCode());
                        keepAccInfo.setPagyRespMsg(results.getRespMsg());
                        keepAccInfo.setReserved2(HDTellrSeqNum);
                        keepAccInfoDao.insertIgnoreExist(keepAccInfo);


                    } else {
                        log.info("========================>>本行通道调用成功,记账失败,返回码[" + results.getRespCode() + "],返回信息[" + results.getRespMsg() + "]<<====================");
                        keepAccInfo.setState(Constans.KEEP_ACCOUNT_STAT_FAIL);
                        keepAccInfo.setPagyRespCode(results.getRespCode());
                        keepAccInfo.setPagyRespMsg(results.getRespMsg());
                        keepAccInfo.setReserved2(HDTellrSeqNum);
                        keepAccInfoDao.insertIgnoreExist(keepAccInfo);
                        guaranteeDeposit = guaranteeDeposit+1;
                        map.put("guaranteeDeposit",guaranteeDeposit);
                        parternBaseInfoDao.update("updateParternDepInfo", map);
                        if(guaranteeDeposit >=3){
                            //使用通道服务发送短信
                            SoaParams paramsSend = new SoaParams();
                            paramsSend.put("tempCode", Constans.GUARANTEE_DEPOSIT);    //短信模板编号
                            paramsSend.put("brNo",parternInfo.getAccountOrg());

                            Map<String,Object> tempData = new HashMap<String,Object>();

                            if(IfspDataVerifyUtil.isNotBlank(parternInfo.getCmPhone())){
                                StringBuffer phone = new StringBuffer();
                                phone.append(parternInfo.getCmPhone());
                                phone.append(";");
                                phone.append(parternInfo.getPhone());
                                tempData.put("phone", phone);
                                tempData.put("mchtNm", parternInfo.getParternName());

                                paramsSend.put("tempData", tempData);
                                SoaResults sendResult = sendMessage(paramsSend);
                                if (!StringUtils.equals(sendResult.getRespCode(), SystemConfig.getSuccessCode())) {
                                    log.warn("调用短信发送接口失败: " + sendResult.getRespCode() + ":" + sendResult.getRespMsg());
//                    visorSoaParams.put("state", "1");
//                    visorSoaParams.put("errorCode",soaResult.getRespCode());
//                    visorSoaParams.put("errorReason",soaResult.getRespMsg());
                                    throw new IfspBizException(sendResult.getRespCode(), sendResult.getRespMsg());
                                }
                            }
                            /*if(IfspDataVerifyUtil.isNotBlank(parternInfo.getPhone())){
                                tempData.put("phone", parternInfo.getPhone());
                                tempData.put("mchtNm", parternInfo.getParternName());

                                paramsSend.put("tempData", tempData);
                                SoaResults sendResult = sendMessage(paramsSend);
                                if (!StringUtils.equals(sendResult.getRespCode(), SystemConfig.getSuccessCode())) {
                                    log.warn("调用短信发送接口失败: " + sendResult.getRespCode() + ":" + sendResult.getRespMsg());
                                    throw new IfspBizException(sendResult.getRespCode(), sendResult.getRespMsg());
                                }
                            }*/


                        }
                    }
                }else if(guaranteeDeposit != 0){
                        map.put("guaranteeDeposit",0);
                        parternBaseInfoDao.update("updateParternDepInfo", map);

                }
                commonResponse.setRespCode(RespEnum.RESP_SUCCESS.getCode());
                commonResponse.setRespMsg("补保证金完成");
            }
        }catch (Exception e){
            log.error("补保证金异常: ", e);
            //返回结果
            commonResponse.setRespCode(SystemConfig.getSysErrorCode());
            commonResponse.setRespMsg("补保证金异常:" + e.getMessage());
        }

        return commonResponse;
    }

    /**
     *
     * 账号余额查询
     * innerAccountSearch
     */
    @SoaClient(name="6040041303", version = "1.0.0", group = "604")
    private ISoaClient innerAccountSearch;

    public SoaResults innerAccountSearch(SoaParams params) {
        SoaResults result = new SoaResults();
        log.debug("innerAccountSearch req: [" + params + "]");
        try {
            result = innerAccountSearch.invoke(params);
        } catch (IfspTimeOutException e) {
            log.error("innerAccountSearch timeout: ", e);
            throw new IfspSystemException(SystemConfig.getSysErrorCode(), "查询超时");
        } catch (Exception e){
            log.error("innerAccountSearch error: ", e);
            throw new IfspSystemException(SystemConfig.getSysErrorCode(), "查询失败");
        }
        log.debug("innerAccountSearch resp: [" + result + "]");
        return result;
    }

    /**
     * 组装参数
     * @param params
     * @param info
     */
    public static void initParam(SoaParams params, KeepAccInfo info) {
        //通道支付请求流水号
        params.put("pagyPayTxnSsn", info.getCoreSsn());
        //通道支付请求流水时间
        params.put("pagyPayTxnTm", IfspDateTime.getYYYYMMDDHHMMSS());
        //通道支付订单号
        params.put("pagyTxnSsn", info.getOrderSsn());
        //通道订单时间
        params.put("pagyTxnTm", info.getOrderTm());
        //借方账号
        params.put("dtAcctNo", info.getOutAccNo());
        //借方账户名称 (非必输)
        params.put("dtAcctNm", info.getOutAccNoName());
        //贷方账号
        params.put("ctAcctNo", info.getInAccNo());
        //贷方账户名称 (非必输)
        params.put("ctAcctNm", info.getInAccNoName());
        //交易描述
        params.put("txnDesc", info.getTxnDesc());
        //支付金额
        params.put("txnAmt", info.getTransAmt());
        //币种
        params.put("txnCcyType", Constans.CCY_TYPE);
        //商户所在机构 (非必输)
        params.put("proxyOrg", info.getProxyOrg());
        // 摘要
        if (IfspDataVerifyUtil.isNotBlank(info.getMemo())){
            params.put("memo",info.getMemo());
        }
    }

    /**
     * 初始化记账
     * @param info
     * @return
     */
    public KeepAccInfo initKeepInfo(ParternDepInfo info,BigDecimal TransAmt)  {
        KeepAccInfo keepAccInfo = new KeepAccInfo();
        String coreSsn = IfspId.getUUID(20);
        // 核心流水
        keepAccInfo.setCoreSsn(coreSsn);
        // 订单号
        keepAccInfo.setOrderSsn(coreSsn);
        // 订单时间
        keepAccInfo.setOrderTm(IfspDateTime.getYYYYMMDDHHMMSS());
        // 借方账户
        keepAccInfo.setOutAccNo(info.getAccountNo());
        // 借方账户名
        keepAccInfo.setOutAccNoName(info.getAccountName());
        // 借方账户类型
        keepAccInfo.setOutAccType(Constans.MCHT_SETTLE);
        // 贷方账户
        keepAccInfo.setInAccNo(info.getDepAcctNo());
        // 贷方商户名
        keepAccInfo.setInAccNoName(info.getParternName());
        // 贷方账户类型
        keepAccInfo.setInAccType(Constans.MCHT_BACK);
        // 划账序号
        keepAccInfo.setKeepAccSeq(Short.valueOf("1"));
        // 记账时间
        keepAccInfo.setKeepAccTime(com.ruim.ifsp.utils.datetime.DateUtil.getYYYYMMDDHHMMSS());
        // 记账类型
        keepAccInfo.setKeepAccType(Constans.KEEP_ACC_TYPE_OTHER);
        // 记账状态
        //keepAccInfo.setState(state);
        // 交易描述
        keepAccInfo.setTxnDesc("补保证金");
        // 交易币种
        keepAccInfo.setTxnCcyType(Constans.CCY_TYPE);
        // 商户所在机构
        keepAccInfo.setProxyOrg(info.getAccountOrg());
        // 子订单号
        //keepAccInfo.setSubOrderSsn(vo.getSubOrderSsn());
        // 不支持重跑
        keepAccInfo.setRerunFlag(ReRunFlagEnum.RE_RUN_FLAG_FALSE.getCode());
        // 是否实时计算手续费 默认非实时计算
        keepAccInfo.setRealTmFlag(RealTmFlagEnum.REAL_TM_FALSE.getCode());





        keepAccInfo.setTransAmt(TransAmt.longValue());
        // 记账摘要
        keepAccInfo.setMemo("补保证金");

        // 唯一索引
        keepAccInfo.setUniqueSsn(getUUID());

        // 批次号默认设置 0 (新同步记账接口有用)
        keepAccInfo.setVerNo("0");

        return keepAccInfo;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    @SoaClient(name="6040050004", version = "1.0.0", group = "604")
    private ISoaClient sendMessage;
    public SoaResults sendMessage(SoaParams params) {
        SoaResults result = new SoaResults();
        log.debug("sendMessage req: [" + params + "]");
        try {
            result = sendMessage.invoke(params);
        } catch (IfspTimeOutException e) {
            log.error("sendMessage timeout: ", e);
            throw new IfspSystemException(SystemConfig.getSysErrorCode(), "短信发送超时");
        } catch (Exception e){
            log.error("sendMessage error: ", e);
            throw new IfspSystemException(SystemConfig.getSysErrorCode(), "短信发送失败");
        }
        log.debug("sendMessage resp: [" + result + "]");
        return result;
    }
}
