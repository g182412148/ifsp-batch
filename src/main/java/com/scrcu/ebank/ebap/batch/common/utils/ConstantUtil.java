package com.scrcu.ebank.ebap.batch.common.utils;

import com.ruim.ifsp.utils.constant.IfspConstants;
import com.ruim.ifsp.utils.message.IfspStringUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;

import java.security.SecureRandom;

public class ConstantUtil {

    /**
     * 微信对账文件 , 去除分隔后的"`"字符
     * 针对费率 , 去除"%"
     * @param str
     * @return
     */
    public static String removeSplitSymbol(String str){
        if(IfspStringUtil.isNotBlank(str)){
            if(str.trim().startsWith("`")){
                str = str.substring(1);
                if(str.trim().endsWith(IfspConstants.SQL_LIKE_TAG)){
                    return str.substring(0, str.length()-1);
                }
                return str;
            }
            return str;
        }else{
            return "";
        }
        
    }

    /**
	 * 获得指定个数的随机数组合
	 * 
	 * @param len
	 * @return 2010-8-19上午10:51:15
	 */
	public static String getRandomNum(int len) {
		String ran = "";
		SecureRandom random = new SecureRandom();
		for (int i = 0; i < len; i++) {
			ran += String.valueOf(random.nextInt(10));
		}
		return ran;
	}


    /**
     * redis使用：动态组装Key
     *
     * @param params 动态参数
     * @return
     */
    public static String getKey(String... params) {
        StringBuffer str = new StringBuffer();
        for (String param : params) {
            if (IfspDataVerifyUtil.isNotBlank(param)) {
                str.append(param + IfspConstants.COLUMN_DEFAULT);
            }
        }
        return str.substring(0, str.length() - 1);
    }

}
