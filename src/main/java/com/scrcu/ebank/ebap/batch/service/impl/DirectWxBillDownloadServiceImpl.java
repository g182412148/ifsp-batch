package com.scrcu.ebank.ebap.batch.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthWxFileDet;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.ConstantUtil;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.common.utils.FileUtil;
import com.scrcu.ebank.ebap.batch.dao.BthWxFileDetDao;
import com.scrcu.ebank.ebap.batch.dao.DebitTranInfoDao;
import com.scrcu.ebank.ebap.batch.service.DirectWxBillDownloadService;
import com.scrcu.ebank.ebap.batch.soaclient.DirectWxBillSoaService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.exception.IfspBizException;

import lombok.extern.slf4j.Slf4j;

@Service("direactWxBillDownloadService")
@Slf4j
public class DirectWxBillDownloadServiceImpl implements DirectWxBillDownloadService 
{
	private final static String WX_FILE_NAME_PRE = "WX_DC_";
	private final static String WX_FILE_NAME_EXT = "_ALL.txt";
	
	private final static String WX_APP_ID = "wx683a1b9bfd004839";
	private final static String WX_MER_NO = "1461202802";
	private final static String WX_SUBMER_NO = "41664665";
	
	private final static BigDecimal ONE_HUNDRED = new BigDecimal(100);
	
	@Resource
	private DebitTranInfoDao debitTranInfoDao;
	
	@Resource
    private BthWxFileDetDao bthWxFileDetDao;
	
	@Resource
	private DirectWxBillSoaService directWxBillSoaService;
	
	@Value("${directWxBillPath}")
	private String directWxBillPath;             //直连微信对账单地址
	
	@Value("${directWxBillBkPath}")
	private String directWxBillBkPath;             //直连微信对账单备份地址

	@Override
	public CommonResponse wxBillDownload(BatchRequest request) throws Exception 
	{
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>wxBillDownload service executing");
		String preDate = request.getSettleDate();         //对账日期
    	if(IfspDataVerifyUtil.isBlank(preDate))
    	{
    		preDate = DateUtil.format(DateUtil.getDiffStringDate(new Date(), -1), "yyyyMMdd");
    	}
    	log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>对账日期 : " + preDate);
    	String wxFileName=WX_FILE_NAME_PRE + preDate + "_"+ WX_MER_NO + WX_FILE_NAME_EXT;
    	
    	//1.根据清算日期和通道编号删除微信对账文件明细表(BTH_WX_FILE_DET)
    	Map<String,Object> params = new HashMap<String,Object>();
		params.put("srcType", Constans.WX_ORDER_TYPE_DIRECT);
		params.put("settleDate", preDate);
		params.put("pagyNo", Constans.WX_SYS_NO+" ");
		
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>删除表数据:");
		
		bthWxFileDetDao.delete("deleteBySrcTypeAndDate", params);
		
		//2.下载直连微信对账文件
		SoaParams soaParams = new SoaParams();
		
		Map<String,Object> beanParReqMsgData = new HashMap<String,Object>();
		beanParReqMsgData.put("tpamPagyAppId", WX_APP_ID);
		beanParReqMsgData.put("tpamPagyNo", WX_MER_NO);
		//beanParReqMsgData.put("tpamPagyMchtNo", WX_SUBMER_NO);          //不送子商户号
		
		soaParams.put("pagySteDate", preDate);                            //对账日期
		soaParams.put("beanParReqMsgData", beanParReqMsgData);
		
		
		
		SoaResults result;
		//请求直连微信对账文件
		result = directWxBillSoaService.downloadDirectWxBill(soaParams);
		
		Map<Object, Object> datas = result.getDatas();
		if (result == null || IfspDataVerifyUtil.isBlank(result.get("respCode"))) 
		{
			throw new IfspBizException(RespConstans.RESP_FAIL.getCode(), RespConstans.RESP_FAIL.getDesc());
		}
		
		if (!IfspDataVerifyUtil.equals((String) result.get("respCode"), RespConstans.RESP_SUCCESS.getCode())) 
		{
			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>downloadDirectWxBill fail, response code is : " +  result.get("respCode"));
			throw new IfspBizException(RespConstans.RESP_FAIL.getCode(), RespConstans.RESP_FAIL.getDesc());
		} 
		else 
		{
			log.info("====================="+datas+"=========================");
			log.info("=====================成功================================");
		}
		String wxFilePath=directWxBillPath;
		
		String localFilePath = wxFilePath + wxFileName;                 //本地存放路径 
		log.info(">>>>>>>>>>>>>>>>>>>>>>>本地存放路径为:" + localFilePath);
		
		Thread.sleep(1000*60*5);
		
		File wxBillFile = new File(localFilePath);
		
		log.info(">>>>>>>>>>>>>>>>>>>>>>>file check...");
		//本地存在直接读取，不存在中断
		if(!wxBillFile.exists())
		{
			log.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>微信对账文件不存在："+localFilePath);
			CommonResponse commonResponse = new CommonResponse();
			
			commonResponse.setRespCode(RespConstans.RESP_FAIL.getCode());
			commonResponse.setRespMsg(RespConstans.RESP_FAIL.getDesc());
			
			return commonResponse;
		}
		int rowCount = 0;
		//4.3解析文件（以“|#|”分隔），将解析结果加载到DebitTranInfo
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>解析直连微信对账单开始,fileName :{"+wxFileName+"}");
        try 
        {
        	//解析文件
        	rowCount = (int)FileUtil.getFileRowCount(localFilePath, BthWxFileDet.class);
        	List<BthWxFileDet> dataList = FileUtil.readFileToList(localFilePath, BthWxFileDet.class, 0, rowCount);
        	
        	//筛选发生在扫码系统的订单
        	List<BthWxFileDet> orderList = new ArrayList<BthWxFileDet>();
        	int count = 0;
        	for(BthWxFileDet order : dataList)
        	{
        		if(order.getOrderNo().startsWith("R"))
        		{
        			continue;
        		}
        		else
        		{
        			BigDecimal feeRate = new BigDecimal(order.getFeeRateStr().replaceAll("%", ""));
        			order.setFeeRate(feeRate);
        			DateUtil.parse(order.getTxnTm(), "yyyy-MM-dd HH:mm:ss");
        			String txnTm = order.getTxnTm().replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
        			order.setTxnTm(txnTm);
        			
        			order.setSrcType(Constans.WX_ORDER_TYPE_DIRECT);
        			order.setPagySysNo(Constans.WX_SYS_NO);
        			order.setPagyNo("605000000000001");
        			order.setChkAcctSt("00");
        			order.setChkRst("00");
        			order.setChkDataDt(preDate);
        			
        			BigDecimal orderAmt = order.getOrderAmt().multiply(ONE_HUNDRED).setScale(0, BigDecimal.ROUND_HALF_UP);
        			order.setOrderAmt(orderAmt);
        			if(order.getRefundAmt() != null)
        			{
        				BigDecimal refundAmt = order.getRefundAmt().multiply(ONE_HUNDRED).setScale(0, BigDecimal.ROUND_HALF_UP);
        				order.setRefundAmt(refundAmt);
        			}
        			
        			if(order.getFeeAmt() != null)
        			{
        				BigDecimal feeAmt = order.getFeeAmt().multiply(ONE_HUNDRED).setScale(0, BigDecimal.ROUND_HALF_UP);
        				order.setFeeAmt(feeAmt);
        			}
        			
        			if("REFUND".equals(order.getTradeSt()))
        			{
        				//退款交易
        				//order.setTradeSt("SUCCESS");
        				order.setOrderTp("12");                      //订单类型 ： 12 - 退款
        				//设置原交易订单号
        				order.setSrcWxOrderNo(order.getOrderNoWx());
        				order.setSrcOrderNo(order.getOrderNo());
        				
        				//设置退款订单号
        				order.setOrderNo(order.getRefundBillMer());
        				order.setOrderNoWx(order.getRefundBillWx());
        			}
        			else
        			{
        				order.setOrderTp("10");                      //订单类型 ： 10 - 交易
        			}
        			
        			orderList.add(order);
        		}
        	}
        	count = bthWxFileDetDao.insertSelectiveList(orderList);
        	log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>微信直连订单数@"+preDate + " is : "+count);
        }
        catch (Exception e)
        {
            log.error("解析业务明细文件失败:", e);
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(),"解析业务明细文件失败");
        }
        log.info("------------ 解析业务明细文件(结束)------------------");
        
        
        //文件备份
        File resultFile = new File(this.directWxBillBkPath+wxFileName);
        wxBillFile.renameTo(resultFile);
		
		//应答
		CommonResponse commonResponse = new CommonResponse();
		
		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
		
		return commonResponse;
	}

}
