package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.DateUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthPagyChkErrInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.DebitTranInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccInfo;
import com.scrcu.ebank.ebap.batch.bean.request.KeepAccountRequest;
import com.scrcu.ebank.ebap.batch.bean.response.KeepAccountResponse;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.RespConstans;
import com.scrcu.ebank.ebap.batch.common.msg.IBankMsg;
import com.scrcu.ebank.ebap.batch.dao.BthPagyChkErrInfoDao;
import com.scrcu.ebank.ebap.batch.dao.DebitTranInfoDao;
import com.scrcu.ebank.ebap.batch.dao.KeepAccInfoDao;
import com.scrcu.ebank.ebap.batch.service.KeepAccountService;
import com.scrcu.ebank.ebap.batch.soaclient.KeepAccSoaService;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
@Service
@Slf4j
public class KeepAccountServiceImpl implements KeepAccountService {
	@Resource
	private KeepAccInfoDao keepAccInfoDao;

	@Resource
    private KeepAccSoaService keepAccSoaService;

	@Resource
	private BthPagyChkErrInfoDao bthPagyChkErrInfoDao;

	@Resource
	private DebitTranInfoDao debitTranInfoDao;


	/**
	 * 日终记账
	 *
	 * @param request
	 * @return
	 * @throws Exception
	 */
	/* (non-Javadoc)
	 * @see com.scrcu.ebank.ebap.batch.service.KeepAccountService#nightKeepAccount(com.scrcu.ebank.ebap.batch.bean.request.KeepAccountRequest)
	 */
	@Override
	public KeepAccountResponse nightKeepAccount(KeepAccountRequest request) {
		//1.根据核心对账结果（查询差错表），【匹配】以核心为准覆盖本地状态，核心成功本地失败，更新成功      本地成功核心失败，发起补账（再次调核心接口，再次失败不在补账，做记录）
		log.info("--------------补账开始------------------");
		KeepAccountResponse response = new KeepAccountResponse();
		log.info("--------------获取参数信息------------------");
		String settleDate = request.getSettleDate();
		String transAmt = request.getTransAmt();
		String inAccNo = request.getInAccNo();
		String outAccNo = request.getOutAccNo();
		String pagySysNo = request.getPagySysNo();
		//1.1 查询差错表状态为06=交易结果不一致，三方失败本地成功且为本行贷记卡的信息
		List<BthPagyChkErrInfo> bthPagyChkErrInfos = bthPagyChkErrInfoDao.selectByStateAndDate(settleDate,"06",pagySysNo);
		if(bthPagyChkErrInfos!=null&&bthPagyChkErrInfos.size()>0){
			for (BthPagyChkErrInfo bthPagyChkErrInfo : bthPagyChkErrInfos) {
				//1.2 发起补账（再次调核心接口，再次失败不在补账，记账成功更新三方交易状态，删除对应的差错表）
				String pagyPayTxnSsn = bthPagyChkErrInfo.getPagyPayTxnSsn();
				KeepAccInfo keepAccInfo = new KeepAccInfo();
				keepAccInfo.setOrderSsn(pagyPayTxnSsn);
				keepAccInfo.setCoreSsn("");
				keepAccInfo.setInAccNo(inAccNo);
				keepAccInfo.setOutAccNo(outAccNo);
				keepAccInfo.setKeepAccTime(DateUtil.getYYYYMMDDHHMMSS());
				if(Constans.WX_SYS_NO.equals(pagySysNo)){
					keepAccInfo.setKeepAccType("01");
				}else if(Constans.ALI_SYS_NO.equals(pagySysNo)){
					keepAccInfo.setKeepAccType("02");
				}else if(Constans.UNION_SYS_NO.equals(pagySysNo)){
					keepAccInfo.setKeepAccType("03");
				}else if(Constans.IBANK_SYS_NO.equals(pagySysNo)){
					keepAccInfo.setKeepAccType("00");
				}
				keepAccInfo.setState("00");
				keepAccInfo.setTransAmt(Long.valueOf(transAmt));
				keepAccInfoDao.insert(keepAccInfo);
				SoaParams params = new SoaParams();
		        params = IBankMsg.keepAcc(params, request);
		        // 调用本行通道客户信息查询接口
		        SoaResults result = keepAccSoaService.keepAcc(params);
		        if (result == null) {
		            throw new IfspBizException(RespConstans.RESP_FAIL.getCode(), RespConstans.RESP_FAIL.getDesc());
		        }
		        // 若返回结果不是成功，则校验失败
		        if (IfspDataVerifyUtil.isBlank(result.get("respCode")) ||
		                !IfspDataVerifyUtil.equals((String) result.get("respCode"), RespConstans.RESP_SUCCESS.getCode())) {
		            response.setRespCode(RespConstans.RESP_FAIL.getCode());
		            response.setRespMsg(RespConstans.RESP_FAIL.getDesc());
		            return response;
		        }
		        Map<Object, Object> datas = result.getDatas();
		        String coreSSn =(String) datas.get("pagyPayTxnSsn");
		        KeepAccInfo keepAccInfoVo = keepAccInfoDao.selectByPrimaryKey((String) datas.get("pagyTxnSsn"));
		        keepAccInfoVo.setCoreSsn(coreSSn);
		        keepAccInfoVo.setState("01");
		        keepAccInfoDao.update(keepAccInfoVo);
		        //记账成功更新三方交易状态，删除对应的差错表
		        bthPagyChkErrInfoDao.deleteByPrimaryKey(bthPagyChkErrInfo.getChkErrSsn());
		        DebitTranInfo debitTranInfo =debitTranInfoDao.selectByPrimaryKey(bthPagyChkErrInfo.getPagyPayTxnSsn());
		        debitTranInfo.setTxnStatus("00");
		        debitTranInfoDao.update(debitTranInfo);
			}
		}
	    //2.不匹配，核心少本地多发起补账
		//2.1查询差错表状态为01=交易结果不一致，三方失败本地成功且为本行贷记卡的信息
		List<BthPagyChkErrInfo> bthPagyChkErrInfoList = bthPagyChkErrInfoDao.selectByStateAndDate(settleDate,"01",pagySysNo);
		if(bthPagyChkErrInfos!=null&&bthPagyChkErrInfos.size()>0){
			for (BthPagyChkErrInfo bthPagyChkErrInfo : bthPagyChkErrInfos) {
				//2.2发起补账（再次调核心接口，再次失败不在补账，记账成功更新三方交易状态，删除对应的差错表）
				String pagyPayTxnSsn = bthPagyChkErrInfo.getPagyPayTxnSsn();
				KeepAccInfo keepAccInfo = new KeepAccInfo();
				keepAccInfo.setOrderSsn(pagyPayTxnSsn);
				keepAccInfo.setCoreSsn("");
				keepAccInfo.setInAccNo(inAccNo);
				keepAccInfo.setOutAccNo(outAccNo);
				keepAccInfo.setKeepAccTime(DateUtil.getYYYYMMDDHHMMSS());
				if(Constans.WX_SYS_NO.equals(pagySysNo)){
					keepAccInfo.setKeepAccType("01");
				}else if(Constans.ALI_SYS_NO.equals(pagySysNo)){
					keepAccInfo.setKeepAccType("02");
				}else if(Constans.UNION_SYS_NO.equals(pagySysNo)){
					keepAccInfo.setKeepAccType("03");
				}else if(Constans.IBANK_SYS_NO.equals(pagySysNo)){
					keepAccInfo.setKeepAccType("00");
				}
				keepAccInfo.setState("00");
				keepAccInfo.setTransAmt(Long.valueOf(transAmt));
				keepAccInfoDao.insert(keepAccInfo);
				SoaParams params = new SoaParams();
		        params = IBankMsg.keepAcc(params, request);
		        // 调用本行通道客户信息查询接口
		        SoaResults result = keepAccSoaService.keepAcc(params);
		        if (result == null) {
		            throw new IfspBizException(RespConstans.RESP_FAIL.getCode(), RespConstans.RESP_FAIL.getDesc());
		        }
		        // 若返回结果不是成功，则校验失败
		        if (IfspDataVerifyUtil.isBlank(result.get("respCode")) ||
		                !IfspDataVerifyUtil.equals((String) result.get("respCode"), RespConstans.RESP_SUCCESS.getCode())) {
		            response.setRespCode(RespConstans.RESP_FAIL.getCode());
		            response.setRespMsg(RespConstans.RESP_FAIL.getDesc());
		            return response;
		        }
		        Map<Object, Object> datas = result.getDatas();
		        String coreSSn =(String) datas.get("pagyPayTxnSsn");
		        KeepAccInfo keepAccInfoVo = keepAccInfoDao.selectByPrimaryKey((String) datas.get("pagyTxnSsn"));
		        keepAccInfoVo.setCoreSsn(coreSSn);
		        keepAccInfoVo.setState("01");
		        keepAccInfoDao.update(keepAccInfoVo);
		        //记账成功插入核心成功流水，删除对应的差错表
		        bthPagyChkErrInfoDao.deleteByPrimaryKey(bthPagyChkErrInfo.getChkErrSsn());
//		        DebitTranInfo debitTranInfo =debitTranInfoDao.selectByPrimaryKey(bthPagyChkErrInfo.getPagyPayTxnSsn());
//		        debitTranInfo.setTxnStatus("00");
//		        debitTranInfoDao.update(debitTranInfo);

		        DebitTranInfo debitTranInfo = new  DebitTranInfo();
		        debitTranInfo.setChannelNo("");// 渠道号
		        debitTranInfo.setChannelDate(settleDate);// 渠道日期
		        debitTranInfo.setChannelSeq(bthPagyChkErrInfo.getPagyPayTxnSsn());// 渠道流水号
		        debitTranInfo.setTxnDate(settleDate);
		        debitTranInfo.setTellerSeq("");// TELLER_SEQ
		        debitTranInfo.setTxnStatus("0");// 交易状态 0.成功  1失败
		        debitTranInfo.setPlatformSeq("");
		        debitTranInfo.setPlatformDate("");
		        debitTranInfo.setTxnCode("CH1440");// 1320?
		        debitTranInfo.setTxnCur("01");
		        debitTranInfo.setReceiveAccount(inAccNo);
		        debitTranInfo.setPagySysNo(pagySysNo);
		        debitTranInfo.setChkAcctSt("01");
		        debitTranInfo.setChkDataDt(settleDate);
		        debitTranInfo.setChkRst("00");
		        debitTranInfoDao.insertSelective(debitTranInfo);
			}
		}
		log.info("--------------补账结束------------------");
		return response;
	}


}
