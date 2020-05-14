package com.scrcu.ebank.ebap.batch.common.utils;

/**
 * 文件工具类<br/>
 * 
 * @author WUDUFENG
 * 
 */
public class AccountUtil {

    

	/* 会计科目 */

    //银联待清算账户
    public static final String unionpayPreSettlAccount="94610106";
    //银联手续费清算账户
    public static final String unionpayFeeSettlAccount="26210802";
   
    //通存通兑挂账户 
    public static final String universalAccount="94610101";
	//电子银行手续费收入
    public static final String electronicFeeAccount="51110095";
    //手续费垫付账户
    public static final String FeeAdvanceSettlAccount="53110025";
    
    //本行待清算账户
    public static final String bankPreSettlAccount="26210824";
    /**
     * 
     * @param payWay 支付方式: 
     * 				01-本行网关支付
	 *				02-本行快捷支付
	 *				03-银联网关支付
	 *				04-银联快捷支付
	 *				05-支付宝网关支付
	 *				06-支付宝快捷支付
	 *				07-财付通网关支付
	 *				08-财付通快捷支付
	 *				09-微信支付
	 *				10-快钱网关支付
	 *				11-快钱快捷支付
	 *				12-快钱支付(发卡)
	 *				13-银联支付(发卡)
	 *				14-易宝支付(发卡)
     * @return gainsType 分润类型:
     * 				01-本行支付网关分润
     *              02-本行收单网关分润
     *              03-非本行收单网关分润
     */
    public static String getGainsType(String payWay){
 		
 		//4位机构号+2位币种+5（内部账）+8位会计科目+00000（序列号）
 		String gainsType;
 		
 		switch (payWay) {
 			case "01":
 				gainsType="02";
 				break;
 			case "02":
 				gainsType="02";
 				break;
 			case "03":
 				gainsType="03";
 				break;
 			case "04":
 				gainsType="03";
 				break;
 			case "05":
 				gainsType="03";
 				break;
 			case "06":
 				gainsType="03";
 				break;
 			case "07":
 				gainsType="03";
 				break;
 			case "08":
 				gainsType="03";
 				break;
 			case "09":
 				gainsType="03";
 				break;
 			case "10":
 				gainsType="03";
 				break;
 			case "11":
 				gainsType="03";
 				break;
 			case "12":
 				gainsType="01";
 				break;
 			case "13":
 				gainsType="01";
 				break;
 			case "14":
 				gainsType="01";
 				break;
 			default:
 				gainsType=null;
 				break;
 		}
 		return gainsType;
 		
 	}
    
    /**
     * 生成联社内部帐
     * 规则：机构号（4位）+ 币种（01）+ 标志（5）+ 科目（8位）+ 序号（5位）
     * 1个机构1个内部账号的，序号都是5个0,00000
     * @param orgNo ： 机构号
     * @param subject ： 科目
     * @return
     */
    public static String genInnerAccount(String orgNo,String subject)
    {
    	StringBuffer innerAcc = new StringBuffer();
    	innerAcc.append(orgNo).append("01").append("5").append(subject.trim()).append("00000");
    	
    	return innerAcc.toString().trim();
    }
    
    /**
     * 生成联社内部帐2
     * 规则：机构号（4位）+ 币种（01）+ 标志（5）+ 科目（8位）+ 序号（5位）
     * 1个机构1个内部账号的，序号都是5个0,00000
     * @param orgNo ： 机构号
     * @param subject ： 科目
     * @return
     */
    public static String genInnerAccountWithSeq(String orgNo,String subject,String seq)
    {
    	StringBuffer innerAcc = new StringBuffer();
    	innerAcc.append(orgNo).append("01").append("5").append(subject.trim()).append(seq.trim());
    	
    	return innerAcc.toString().trim();
    }
    
	

}
