package com.scrcu.ebank.ebap.batch.common.utils;/**
 * Created by Administrator on 2019-08-21.
 */

import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.ruim.ifsp.utils.beans.IfspBeanUtils;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.response.KeepAcctResponse;
import com.scrcu.ebank.ebap.log.IfspLoggerFactory;
import org.slf4j.Logger;

/**
 * 名称：〈名称〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019-08-21 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
public class KeepAcctUtil {
    private static Logger log = IfspLoggerFactory.getLogger(KeepAcctUtil.class);

    /**
     * 记账查询后是否冲正
     * @param soaResults
     * @return
     */
    public static boolean isKeepRecovery(SoaResults soaResults){
        boolean recoveryFlag = false;
        //超时，冲正
        if(IfspDataVerifyUtil.equals(IfspRespCodeEnum.RESP_0021.getCode(), soaResults.getRespCode())){
            recoveryFlag = true;
        }
        //非全部成功，冲正
        try {
            KeepAcctResponse keepAcctResponse = (KeepAcctResponse)IfspBeanUtils.toBean(KeepAcctResponse.class, soaResults);
            if(IfspDataVerifyUtil.equals("1",keepAcctResponse.getErrorFlag())||IfspDataVerifyUtil.equals("1", keepAcctResponse.getTimeOutFlag())){
                recoveryFlag = true;
            }
        } catch (Exception e) {
            log.info("响应报文[{}]转换异常,异常信息：", soaResults.toString(), e);
            recoveryFlag = true;
        }
        return recoveryFlag;
    }

    /**
     * 记账后是否查询
     * @param soaResults
     * @return
     */
    public static boolean isKeepQuery(SoaResults soaResults){
        boolean queryFlag = false;
        //超时，查询
        if(IfspDataVerifyUtil.equals(IfspRespCodeEnum.RESP_0021.getCode(), soaResults.getRespCode())){
            queryFlag = true;
        }
        //存在超时，查询
        try {
            KeepAcctResponse keepAcctResponse = (KeepAcctResponse)IfspBeanUtils.toBean(KeepAcctResponse.class, soaResults);
            if(IfspDataVerifyUtil.equals("1",keepAcctResponse.getTimeOutFlag())){
                queryFlag = true;
            }
        } catch (Exception e) {
            log.info("响应报文[{}]转换异常,异常信息：", soaResults.toString(), e);
            queryFlag = true;
        }
        return queryFlag;
    }

    /**
     * 记账是否成功
     * @param soaResults
     * @return
     */
    public static boolean isKeepSuc(SoaResults soaResults){
        boolean sucFlag = false;
        try {
            //0-false,1-true
            KeepAcctResponse keepAcctResponse = (KeepAcctResponse)IfspBeanUtils.toBean(KeepAcctResponse.class, soaResults);
            if(IfspDataVerifyUtil.equals("0",keepAcctResponse.getTimeOutFlag()) && IfspDataVerifyUtil.equals("0",keepAcctResponse.getErrorFlag())){
                sucFlag = true;
            }
        } catch (Exception e) {
            log.info("响应报文[{}]转换异常,异常信息：", soaResults.toString(), e);
        }
        return sucFlag;
    }

    /**
     * 记账是存在失败
     * @param soaResults
     * @return
     */
    public static boolean isKeepErr(SoaResults soaResults){
        boolean sucFlag = false;
        try {
            //0-false,1-true
            KeepAcctResponse keepAcctResponse = (KeepAcctResponse)IfspBeanUtils.toBean(KeepAcctResponse.class, soaResults);
            if(IfspDataVerifyUtil.equals("1",keepAcctResponse.getErrorFlag())){
                sucFlag = true;
            }
        } catch (Exception e) {
            log.info("响应报文[{}]转换异常,异常信息：", soaResults.toString(), e);
        }
        return sucFlag;
    }

}
