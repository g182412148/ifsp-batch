package com.scrcu.ebank.ebap.batch.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.PaySubOrderInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.common.utils.FileUtil;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.batch.dao.PaySubOrderInfoDao;
import com.scrcu.ebank.ebap.batch.service.GenSCLMChkFileService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GenSCLMChkFileServiceImpl implements GenSCLMChkFileService 
{
	private String batchDate;
	
	@Resource
	private BthMerInAccDtlDao bthMerInAccDtlDao;
	
    @Resource
    private PaySubOrderInfoDao paySubOrderInfoDao;     //子订单表
    
    @Value("${loanFilePath}")
    private String loanFilePath;              //放款文件路径
    
    private static String loanFileName = "";       //放款文件文件名
    
    private static BigDecimal ONE_HUNDRED = new BigDecimal(100);

	@Override
	public CommonResponse genSCLMChkFile(BatchRequest request) throws Exception 
	{
		String batchDate = request.getSettleDate();
    	if(IfspDataVerifyUtil.isBlank(batchDate))
    	{
    		batchDate = DateUtil.format(DateUtil.getDiffStringDate(new Date(), -1), "yyyyMMdd");
    	}
    	
		this.setBatchDate(batchDate);
		
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>genSCLMChkFile of "+this.batchDate+" executing ");
		
		loanFileName = this.loanFilePath+"loanpayconfirm_" +batchDate+"";
		
		//this.getTotalResult();
		
		this.getDataList();
		
		//应答
		CommonResponse commonResponse = new CommonResponse();
		
		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
				
		return commonResponse;
	}
	
	
	public int getTotalResult()
	{
		int count = 0;
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("fundChannel", Constans.GAINS_CHANNEL_LOAN);  
		params.put("transFactTime", this.getBatchDate());
		params.put("txnTypeNo", Constans.TXN_TYPE_ONLINE_PAY);
		params.put("orderType", "01");         //支付订单
		count = paySubOrderInfoDao.count("countSCLMDataByDate", params);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>loan record num @"+this.getBatchDate()+ " is : " +count);
		return count;
	}
	
	public List<PaySubOrderInfo> getDataList()
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("fundChannel", Constans.GAINS_CHANNEL_LOAN);  
		params.put("transFactTime", this.getBatchDate());
		params.put("txnTypeNo", Constans.TXN_TYPE_ONLINE_PAY);
		params.put("orderType", "01");         //支付订单
		List<PaySubOrderInfo> loanList = paySubOrderInfoDao.selectList("selectSCLMDataByDate", params);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>loanList  size of current thread is :" + loanList.size());
		
		for(PaySubOrderInfo loan : loanList)
		{
			//查询当日撤销金额
			params.put("txnTypeNo", Constans.TXN_TYPE_ONLINE_REFUND);
			params.put("openClientId", loan.getOpenClientId());
			params.put("customerId", loan.getCustomerId());
			
			List<PaySubOrderInfo> revocationList = paySubOrderInfoDao.selectList("selectRevocationDataOfLastDay", params); // 查询数据库
			
			if(revocationList != null && revocationList.size() > 0)
			{
				PaySubOrderInfo revocation = revocationList.get(0);
				if(revocation != null && revocation.getPayAmount() != null)
				{
					//扣减当日撤销金额
					loan.setPayAmount(new BigDecimal(loan.getPayAmount()).subtract(new BigDecimal(revocation.getPayAmount())).intValue()+"");
				}
			}
		}
		
		int size = loanList.size();
		for(int i = 0; i < size;)
		{
			//设置预留字段值
			loanList.get(i).setDetailsExtend1("");
			PaySubOrderInfo loan  = loanList.get(i);
			loan.setPayAmount(new BigDecimal(loan.getPayAmount()).divide(ONE_HUNDRED).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()+"");
			if("0".equals(loanList.get(i).getPayAmount()))
			{
				loanList.remove(i);
				size --;
			}
			else
			{
				i ++;
			}
		}
		
		synchronized(this)
		{
			FileUtil.write(loanFileName, loanList, false, null, null,true,"3");
			
			FileUtil.createFile(loanFileName+".ok");
		}
		
		
		return loanList;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void execute(PaySubOrderInfo subOrder)
	{
	}
	
	public void updateObject()
	{
		//更新二级商户结算状态
		log.info(">>>>>>>>>>>>>>>>>>>syncStlStatus @"+this.getBatchDate() + "completed..");
	}

	public String getBatchDate() {
		return batchDate;
	}

	public void setBatchDate(String batchDate) {
		this.batchDate = batchDate;
	}
	
}
