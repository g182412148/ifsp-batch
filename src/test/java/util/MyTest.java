package util;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.scrcu.ebank.ebap.batch.common.utils.DubboServiceUtil;
import org.junit.Test;

public class MyTest {
    private String url = "http://localhost:8080/batch/SOA/";
    private boolean base64Flag = false;

    @Test
    public void test() {

        Map<String, String> params = new HashMap<String, String>();
        /** ----------------------------公共------------------------- */
//        params.put("reqSsn", IfspId.getUUID32());// 请求流水号
//        params.put("reqDate", IfspDateTime.getYYYYMMDDHHMMSS());// 请求时间
//        params.put("busCode", "001.queryAppVer");// 业务代码
//        params.put("channel", "00");// 请求渠道：00-商户收款APP，01-商户门户
//        params.put("version", "1.0");// APP版本号
//        params.put("termCode", "1234567");// 设备终端号
//        params.put("userId", "000001");// 用户号
        /** ----------------------------业务------------------------- */

        /************************* 抽取流水**********************/
//        params.put("pagySysNo", "6003");
//        params.put("settleDate", "20180619");
//        params.put("settleDate", "06212018211009");
//        params.put("settleDate", "06/21/2018 21:10:09");
        
        /************************* 抽取流水 **********************/

        /************************* 对账单下载*************************/
//        params.put("pagySysNo", "6004");
//        params.put("pagyNo", "600312345678913");
        params.put("settleDate", "20180619");
        
//        params.put("settleDate", "06212018211009");
//        params.put("settleDate", "06/21/2018 21:10:09");
        /************************* 对账单下载 *************************/

        System.out.println("req:" + params);
        HttpClientUtils.send(params, "002.SynchronizeOrg", url, base64Flag);
    }
    
    @Test
    public void test1() {
//    	reqSsn : 81603412076430024281563401066343
//    	reqChnl : 03
//    	userNo : 82018070514215821808179864482415
//    	mchtId : 20180707152305312003818
//    	termCode : bc15922fc35eb56983773a5e699dc26b1bc92c6a
//    	endDate : 20180807
//    	userId : 82018070514215821808179864482415
//    	startDate : 20180801
//    	version : 1.0.0
//    	reqTm : 20180808153707
    	Map<String, String> params = new HashMap<String, String>();
    	/** ----------------------------公共------------------------- */
        params.put("reqSsn", "81603412076430024281563401066343");// 请求流水号
        params.put("reqChnl", "03");// 请求时间
        params.put("userNo", "82018070514215821808179864482415");// 业务代码
        params.put("termCode", "bc15922fc35eb56983773a5e699dc26b1bc92c6a");// APP版本号
        params.put("endDate", "20180807");// 设备终端号
        params.put("userId", "82018070514215821808179864482415");// 用户号
        params.put("startDate", "20180801");// 用户号
        params.put("version", "1.0.0");// 用户号
        params.put("reqTm", "20180808153707");// 用户号
        params.put("startDate", "20180801");// 用户号
        params.put("endDate", "20180830");// 用户号
        
        params.put("mchtId", "68855117020000568");// 请求渠道：00-商户收款APP，01-商户门户
        
    	System.out.println("req:" + params);
    	HttpClientUtils.send(params, "699.QueryMonthBill", url, base64Flag);
    }
    
    @Test
    public void test2() {
    	Map<String, Object> params = new HashMap<String, Object>();
    	/** ----------------------------公共------------------------- */
        params.put("reqSsn", "81603412076430024281563401066343");// 请求流水号
        params.put("reqChnl", "03");// 请求时间
        params.put("userNo", "82018070514215821808179864482415");// 业务代码
        params.put("termCode", "bc15922fc35eb56983773a5e699dc26b1bc92c6a");// APP版本号
        params.put("endDate", "20180807");// 设备终端号
        params.put("userId", "82018070514215821808179864482415");// 用户号
        params.put("startDate", "20180801");// 用户号
        params.put("version", "1.0.0");// 用户号
        params.put("reqTm", "20180808153707");// 用户号
        params.put("pageNo", "1");// 用户号
        params.put("pageSize", "10");// 用户号
        
        
        
        params.put("mchtId", "106765500003261");// 请求渠道：00-商户收款APP，01-商户门户
        params.put("qryDate", "20190326");// 用户号
        
    	System.out.println("req:" + params);
//    	HttpClientUtils.send(params, "699.QueryDailyBill", url, base64Flag);

        Map map = DubboServiceUtil.invokeDubboService(params, "699.QueryDailyBill");
        String s = JSON.toJSONString(map);
        System.out.println(s);

    }
    
    //分店接口
    @Test
    public void test3() {
    	Map<String, String> params = new HashMap<String, String>();
    	/** ----------------------------公共------------------------- */
    	params.put("reqSsn", "81603412076430024281563401066343");// 请求流水号
    	params.put("reqChnl", "03");// 请求时间
    	params.put("userNo", "82018070514215821808179864482415");// 业务代码
    	params.put("termCode", "bc15922fc35eb56983773a5e699dc26b1bc92c6a");// APP版本号
    	params.put("reqTm", "20180825153707");// 用户号
    	params.put("pageNo", "1");// 用户号
    	params.put("pageSize", "100");// 用户号
    	
    	params.put("mchtId", "10021101070000576");// 请求渠道：00-商户收款APP，01-商户门户
    	
    	System.out.println("req:" + params);
    	HttpClientUtils.send(params, "001.QuerySubMchtInfo", url, base64Flag);
    }
    
    //分店月账单
    @Test
    public void test4() {
    	Map<String, String> params = new HashMap<String, String>();
    	/** ----------------------------公共------------------------- */
    	params.put("reqSsn", "81603412076430024281563401066343");// 请求流水号
    	params.put("reqChnl", "03");// 请求时间
    	params.put("userNo", "82018070514215821808179864482415");// 业务代码
    	params.put("termCode", "bc15922fc35eb56983773a5e699dc26b1bc92c6a");// APP版本号
    	params.put("reqTm", "20180825153707");// 用户号
    	params.put("pageNo", "1");// 用户号
    	params.put("pageSize", "100");// 用户号
    	params.put("userId", "1231231");// 用户号
    	
    	params.put("mchtId", "11825101080000556");// 请求渠道：00-商户收款APP，01-商户门户
    	params.put("startDate", "20180801");
    	params.put("endDate", "20180830");
    	
    	System.out.println("req:" + params);
    	HttpClientUtils.send(params, "699.MonthTradeStatistics", url, base64Flag);
    }
    
    //分店日账单
    @Test
    public void test5() {
    	Map<String, String> params = new HashMap<String, String>();
    	/** ----------------------------公共------------------------- */
    	params.put("reqSsn", "81603412076430024281563401066343");// 请求流水号
    	params.put("reqChnl", "03");// 请求时间
    	params.put("userNo", "82018070514215821808179864482415");// 业务代码
    	params.put("termCode", "bc15922fc35eb56983773a5e699dc26b1bc92c6a");// APP版本号
    	params.put("reqTm", "20180825153707");// 用户号
    	params.put("pageNo", "1");// 用户号
    	params.put("pageSize", "100");// 用户号
    	params.put("userId", "1231231");// 用户号
    	
    	params.put("mchtId", "68855117020000568");// 请求渠道：00-商户收款APP，01-商户门户
    	params.put("startDate", "20180801");
    	params.put("endDate", "20180830");
    	
    	System.out.println("req:" + params);
    	HttpClientUtils.send(params, "699.DailyTradeStatistics", url, base64Flag);
    }

}
