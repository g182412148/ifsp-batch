package com.scrcu.ebank.ebap.batch.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.ruim.ifsp.dubbo.bean.GenericResult;
import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.common.constant.MchtChnlRequestConstants;
import com.scrcu.ebank.ebap.dubbo.scan.SoaKey;
import com.scrcu.ebank.ebap.log.IfspLoggerFactory;

/**
 * 获取dubbo服务
 *
 * @author DEV028752
 */
public class DubboServiceUtil {

    static Logger log = IfspLoggerFactory.getLogger(DubboServiceUtil.class);
    
    

    private static class DubboService {
        /**
         * 服务缓存
         */
        public final static Map<SoaKey, ReferenceConfig<GenericService>> soaServiceCache;
        /**
         * 注册用心
         */
        public final static RegistryConfig regConfig;
        /**
         * 应用
         */
        public final static ApplicationConfig appConfig;

        /**
         * zookeeper地址
         */
        public final static String zkUrl;

        /**
         * 组别
         */
        public final static String group;

        static {
            log.info("==============初始化dubbo上下文(start)==============");
            /*
				加载配置 6040980003
			 */
            
          //获取当前类加载器
            ClassLoader classLoader=DubboServiceUtil.class.getClassLoader();
            //创建一个Properties 对象
            //通过当前累加载器方法获得 文件db.properties的一个输入流
            Properties properties=new Properties();
            try (InputStream is=classLoader.getResourceAsStream("ifspconfigs/dubbo/dubbo.properties");){
                //加载输入流
				properties.load(is);
			} catch (IOException e) {
				e.printStackTrace();
			}

            zkUrl = properties.getProperty("registry.address");
//            zkUrl = "zookeeper://10.16.1.93:2181?backup=10.16.1.93:2182,10.16.1.93:2183";

            group = properties.getProperty("registry.group");
			/*
				初始化应用
			 */
            appConfig = new ApplicationConfig();
            appConfig.setName("batch");
			/*
				初始化注册中心
			 */
            regConfig = new RegistryConfig();
            regConfig.setAddress(zkUrl);
            regConfig.setGroup(group);
            regConfig.setTimeout(1000*60*35);
			/*
				服务缓存
			 */
            soaServiceCache = new ConcurrentHashMap<>();
            log.info("==============初始化dubbo上下文(end)==============");
        }

        /**
         * 单独获取实例
         */
        private static GenericService getGenericService(SoaKey soaKey) {
            log.debug("get dubbo client : " + soaKey);
            if (soaServiceCache.containsKey(soaKey)) {
                return soaServiceCache.get(soaKey).get();
            } else {
                ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
                reference.setApplication(appConfig);
                reference.setRegistry(regConfig);
                reference.setGeneric(true);
                reference.setRetries(0); //不重试
                reference.setAsync(false); //同步
                reference.setCheck(false); //不检查
//				reference.setTimeout();

                reference.setInterface(soaKey.getSoaName());
                if (IfspDataVerifyUtil.isNotBlank(soaKey.getVersion())) {
                    reference.setVersion(soaKey.getVersion());
                }
                if (IfspDataVerifyUtil.isNotBlank(soaKey.getGroup())) {
                    reference.setGroup(soaKey.getGroup());
                }
                soaServiceCache.put(soaKey, reference);
                return reference.get();
            }
        }
    }

    /**
     * 调用Dubbo服务
     *
     * @param params 调用服务传入的参数
     * @param soaKey 调用服务
     * @return
     */
    public static Map invokeDubboService(Map<String, Object> params, SoaKey soaKey) {
        try {
            GenericResult ret = (GenericResult) DubboService.getGenericService(soaKey)
                    .$invoke(IfspFastJsonUtil.tojson(soaKey), new String[]{""}, new Object[]{params});
            return ret.getResult();
        } catch (Exception e) {
            log.error("dubbo serivce [" + soaKey + "] invoke error:", e);
            throw e;
        }
    }

    /**
     * 调用Dubbo服务
     *
     * @param params 调用服务传入的参数
     * @param soaName 调用服务名称
     * @return
     */
    public static Map invokeDubboService(Map<String, Object> params, String soaName) {
        return invokeDubboService(params, new SoaKey(soaName, null, null ));
    }

    public static void main(String[] args) {
        Map<String, Object> params = new HashMap<>();
//        params.put("reqSsn", "81603412076430024281563401066343");// 请求流水号
//        params.put("reqChnl", "03");// 请求时间
//        params.put("userNo", "82018070514215821808179864482415");// 业务代码
//        params.put("mchtId", "20180707152305312003818");// 请求渠道：00-商户收款APP，01-商户门户
//        params.put("termCode", "bc15922fc35eb56983773a5e699dc26b1bc92c6a");// APP版本号
//        params.put("endDate", "20180807");// 设备终端号
//        params.put("userId", "82018070514215821808179864482415");// 用户号
//        params.put("startDate", "20180801");// 用户号
//        params.put("version", "1.0.0");// 用户号
//        params.put("reqTm", "20180808153707");// 用户号
//        Map serRetMap = DubboServiceUtil.invokeDubboService(params, new SoaKey("mchtAdd", "1.0.0", "600"));
//        String respCode = (String) serRetMap.get("respCode");// 响应码
        
		params.put("payPathCd","1002" );//支付汇路   1001-大额 1002-小额 1003-网银互联 1010-农信银 1110-四川支付 9001-行内支付 2001-智能汇路
		params.put("pltfBizTyp", "A100");//业务类型  A100：普通贷记
		params.put("pltfBizKind", "02102");//业务种类  02102：普通贷记
		params.put("debtCrdtInd", "1");//借贷标识  1-贷 2-借
//		params.put("totlCnt", 0);//明细总笔数
//		params.put("totlAmt", 0);//明细总金额
//		params.put("Bat_Doc_Nm", "S05220171026RPS171026102800719.txt");//文件名 S(1位) + 机构号 + 交易日期(8位) + 渠道号 + 渠道流水号 + .txt   机构号9996  渠道号052  渠道流水号20位随机
        params.put("txnSsn","10676510027209720190101" );
        params.put("outAcctNo","11820152621082400018" );
        params.put("inAcctNo","4367420110118369725" );
        params.put("inAcctName","肖平" );
        params.put("outAcctName","浙江省农村信用社联合社" );
        params.put("inAcctNoOrg","105100000017" );
        params.put("inAcctAmt","10.31" );
        params.put("channelSerNo",ConstantUtil.getRandomNum(20) );
        params.put("batchNo","20190101" );

        log.info("-----------组装统一支付接口报文结束-----------");
        
       // Map resMap = DubboServiceUtil.invokeDubboService(params, new SoaKey("6040980006", "1.0.0", "604"));
        Map resMap = DubboServiceUtil.invokeDubboService(params, new SoaKey("6040980007", "1.0.0", "604"));
//        Map resMap = DubboServiceUtil.invokeDubboService(params, new SoaKey("699.callCH1730", null, null));
//        Map resMap = DubboServiceUtil.invokeDubboService(params, new SoaKey("699.updateStat", null, null));
       
        System.out.println(">>>>>>>>>>>>>"+resMap);
        
        // 判断是否响应成功
        if (!MchtChnlRequestConstants.CHL_APPLY_SUCCESS.equals((String) resMap.get("respCode"))) {
            System.out.println(resMap.get("respMsg"));
        }
    }
}
