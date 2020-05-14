package com.scrcu.ebank.ebap.batch.common.utils;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;

public class CharUtil {
    /**
     * 按字节长度截取字符串
     * @param srcStr
     * @param length
     * @return
     */
    public static String  cutStr(String srcStr,String charset,int length){
        if(StringUtils.isBlank(srcStr)){
            return srcStr;
        }
        byte[] srcBytes = new byte[0];
        try {
            srcBytes = srcStr.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String desStr = srcStr;
        while(srcBytes.length>length){
            desStr = desStr.substring(0,desStr.length()-1);
            srcBytes=desStr.getBytes();
        }
        return desStr;
    }

    /**
     * 截取4位应答码
     * @return
     */
    public static String subStrRespCode(String respCode){
        if(IfspDataVerifyUtil.isBlank(respCode))
            return respCode;
        if(respCode.length()==7)
            return respCode.substring(3,7);
        return respCode;
    }
}
