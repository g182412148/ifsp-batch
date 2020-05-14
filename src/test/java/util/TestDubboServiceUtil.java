package util;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruim.ifsp.dubbo.bean.GenericResult;
import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.common.utils.DubboServiceUtil;
import com.scrcu.ebank.ebap.dubbo.scan.SoaKey;
import com.scrcu.ebank.ebap.log.IfspLoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获取dubbo服务
 *
 * @author DEV028752
 *
 */
public class TestDubboServiceUtil {

	static Logger log = IfspLoggerFactory.getLogger(TestDubboServiceUtil.class);

	private static class  DubboService{
		/** 服务缓存 */
		public final static Map<SoaKey, ReferenceConfig<GenericService>> soaServiceCache;
		/** 注册用心 */
		public final static RegistryConfig regConfig;
		/** 应用 */
		public final static ApplicationConfig appConfig;
		//zookeeper地址
		public final static String zkUrl;

		static{
			log.info("==============初始化dubbo上下文(start)==============");
     		/*
				加载配置
			 */
			//获取当前类加载器
			ClassLoader classLoader= DubboServiceUtil.class.getClassLoader();

			//创建一个Properties
			Properties properties=new Properties();
			//加载输入流
			//通过当前累加载器方法获得
			try(InputStream is=classLoader.getResourceAsStream("ifspconfigs/dubbo/dubbo.properties");) {
				properties.load(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			zkUrl = properties.getProperty("registry.address");
			/*
				初始化应用
			 */
			appConfig = new ApplicationConfig();
			appConfig.setName("ifsp-test");
			/*
				初始化注册中心
			 */
			regConfig = new RegistryConfig();
			regConfig.setAddress(zkUrl);
			regConfig.setGroup("ebap");
			/*
				服务缓存
			 */
			soaServiceCache = new ConcurrentHashMap<>();
			log.info("==============初始化dubbo上下文(end)==============");
		}
		/**
		 *  单独获取实例
		 */
		private static GenericService  getGenericService(SoaKey soaKey){
			log.debug("get dubbo client : " + soaKey);
			if(soaServiceCache.containsKey(soaKey)){
				return soaServiceCache.get(soaKey).get();
			}else {
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
	 * @param params 调用服务传入的参数
	 * @param soaKey 调用服务
	 * @return
	 */
	public static Map invokeDubboService(Map<String, Object> params, SoaKey soaKey){
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
	 * @return
	 */
	public static Map invokeDubboServiceBean(Object o , SoaKey soaKey){
		String msg =  JSON.toJSONString(o);
		Map<String, Object> params = JSONObject.parseObject(msg, Map.class);
		try {
			GenericResult ret = (GenericResult) DubboService.getGenericService(soaKey)
					.$invoke(IfspFastJsonUtil.tojson(soaKey), new String[]{""}, new Object[]{params});
			return ret.getResult();
		} catch (Exception e) {
			log.error("dubbo serivce [" + soaKey + "] invoke error:", e);
			throw e;
		}
	}

}
