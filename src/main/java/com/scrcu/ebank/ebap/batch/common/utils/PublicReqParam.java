package com.scrcu.ebank.ebap.batch.common.utils;

import com.ruim.ifsp.utils.datetime.DateUtil;
import com.ruim.ifsp.utils.id.IfspId;
import com.scrcu.ebank.ebap.log.IfspLoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


/**
 * 名称：〈公共请求参数〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2017年02月13日 <br>
 * 作者：zhangjp <br>
 * 说明：<br>
 */
public class PublicReqParam {
	@Resource
	private static Logger logger = IfspLoggerFactory.getLogger(PublicReqParam.class);
	/**
	 * 获取公共参数
	 * @param busCode
	 * @return
	 */
	public static Map<String,Object> getParam(String busCode,String stepCode){
		Map<String,Object> paramMap = new HashMap<String,Object>(); 
		paramMap.put("reqSsn", IfspId.getUUID32());
		paramMap.put("reqDate", DateUtil.getYYYYMMDD());
		paramMap.put("reqTm", DateUtil.getHHMMSS());
		paramMap.put("reqChnl", "52");
		paramMap.put("busCode", busCode);
		paramMap.put("stepCode", stepCode);
		paramMap.put("termCode", IfspId.getUUID(8));
		logger.info("商户前置获取公共参数paramMap为："+paramMap);
		return paramMap;
	}
	
	public static HttpServletRequest getRequest(){  
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();  
        return requestAttributes==null? null : requestAttributes.getRequest();  
    }  
	public static String getSessionId (){
		return getRequest().getSession().getId();
	}

 }
