package com.scrcu.ebank.ebap.batch.common.msg;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.scrcu.ebank.ebap.batch.bean.dto.BthBatchAccountFile;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAcc;
import com.scrcu.ebank.ebap.batch.bean.request.KeepAccountRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.ConstantUtil;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 *名称：<组装调用本行通道接口报文> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/7/28 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */
@Slf4j
public class IBankMsg {

    public static SoaParams errAcct(String dtAcct, String ctAcct, String txnAmt) {
		SoaParams params = new SoaParams();
        log.info("-----------组装记账接口报文开始-----------");
        params.put("pagyPayTxnSsn", ConstantUtil.getRandomNum(20)); //通道支付请求流水号
        params.put("pagyPayTxnTm", IfspDateTime.getYYYYMMDDHHMMSS()); //通道支付请求流水时间
//        params.put("pagyTxnSsn", request.getOrderSsn()); //通道支付订单号
//        params.put("pagyTxnTm", request.getOrderTm()); //通道订单时间
        params.put("dtAcctNo", dtAcct); //借方账号
//        params.put("dtAcctNm", request.getOutAccName()); //借方账户名称
        params.put("ctAcctNo", ctAcct); //贷方账号
//        params.put("ctAcctNm", request.getInAccName()); //贷方账号
        params.put("txnDesc", "差错入账"); //交易描述
        params.put("txnAmt", txnAmt); //支付金额
        params.put("txnCcyType", Constans.CCY_TYPE); //币种
        params.put("terminal", "07"); //终端类型
        params.put("proxyOrg", "9999"); //代理机构 商户所在机构
        params.put("teller", "999999"); //柜员号 机构号+”99”
        log.info("-----------组装记账接口报文结束-----------");
        return params;
    }

    public static SoaParams keepAcc(SoaParams params, KeepAccountRequest request) {

        log.info("-----------组装记账接口报文开始-----------");
        params.put("pagyPayTxnSsn", ConstantUtil.getRandomNum(20)); //通道支付请求流水号
        params.put("pagyPayTxnTm", IfspDateTime.getYYYYMMDDHHMMSS()); //通道支付请求流水时间
        params.put("pagyTxnSsn", request.getOrderSsn()); //通道支付订单号
        params.put("pagyTxnTm", request.getOrderTm()); //通道订单时间
        params.put("dtAcctNo", request.getOutAccNo()); //借方账号
        params.put("dtAcctNm", request.getOutAccName()); //借方账户名称
        params.put("ctAcctNo", request.getInAccNo()); //贷方账号
        params.put("ctAcctNm", request.getInAccName()); //贷方账号
        params.put("txnDesc", request.getTxnDesc()); //交易描述
        params.put("txnAmt", request.getTransAmt()); //支付金额
        params.put("txnCcyType", Constans.CCY_TYPE); //币种
        params.put("terminal", "07"); //终端类型
        params.put("proxyOrg", ""); //代理机构 商户所在机构
        params.put("teller", ""); //柜员号 机构号+”99”
        log.info("-----------组装记账接口报文结束-----------");
        return params;
    }

	public static SoaParams PagyFeeClearing(SoaParams params, String fileName) {
		log.info("-----------组装CH1730接口报文开始-----------");
    	params.put("oprInd", "1");
    	params.put("bizTyp", "07");
    	params.put("docNm", fileName);
    	log.info("-----------组装CH1730接口报文结束-----------");
		return params;
	}
	
	public static SoaParams unifyPay(SoaParams params,BthBatchAccountFile bthBatchAccountFile ) {
		log.info("-----------组装统一支付接口报文开始-----------");
		
		params.put("payPathCd","1002" );//支付汇路   1001-大额 1002-小额 1003-网银互联 1010-农信银 1110-四川支付 9001-行内支付 2001-智能汇路
		params.put("pltfBizTyp", "A100");//业务类型  A100：普通贷记
		params.put("pltfBizKind", "02102");//业务种类  02102：普通贷记
		params.put("debtCrdtInd", "1");//借贷标识  1-贷 2-借
		params.put("totlCnt", bthBatchAccountFile.getFileCount());//明细总笔数
		params.put("totlAmt", bthBatchAccountFile.getFileAmt().setScale(2));//明细总金额
		params.put("Bat_Doc_Nm", bthBatchAccountFile.getAccFileName());//文件名 S(1位) + 机构号 + 交易日期(8位) + 渠道号 + 渠道流水号 + .txt   机构号9996  渠道号052  渠道流水号20位随机
		log.info("-----------组装统一支付接口报文结束-----------");
		return params;
	}

    public static SoaParams unifyPayOtherSel(SoaParams params,BthMerInAcc bthMerInAcc ) {
        log.info("-----------组装统一支付接口报文开始-----------");

        params.put("payPathCd","0001" );//支付汇路   1001-大额 1002-小额 1003-网银互联 1010-农信银 1110-四川支付 9001-行内支付 2001-智能汇路
        params.put("pltfBizTyp", "A100");//业务类型  A100：普通贷记
        params.put("pltfBizKind", "02102");//业务种类  02102：普通贷记
        params.put("debtCrdtInd", "1");//借贷标识  1-贷 2-借 S
        params.put("txnSsn",bthMerInAcc.getChlMerId()+bthMerInAcc.getBatchNo());
        params.put("outAcctNo",bthMerInAcc.getOutAcctNo() );
        params.put("inAcctNo",bthMerInAcc.getInAcctNo() );
        params.put("inAcctName",bthMerInAcc.getInAcctName());
        params.put("outAcctName",bthMerInAcc.getOutAcctName() );
        params.put("inAcctNoOrg",bthMerInAcc.getInAcctNoOrg() );
        params.put("inAcctAmt",new BigDecimal( bthMerInAcc.getInAcctAmt()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        params.put("batchNo",bthMerInAcc.getBatchNo());
        params.put("channelserno",ConstantUtil.getRandomNum(20));
        log.info("-----------组装统一支付接口报文结束-----------");
        return params;
    }
}
