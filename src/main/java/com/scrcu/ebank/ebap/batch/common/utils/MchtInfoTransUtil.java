package com.scrcu.ebank.ebap.batch.common.utils;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;

public class MchtInfoTransUtil {

    public static String tranNetMchntSvcTp(String src){
        /**银联代码-营业证明文件类型
         * 01-营业执照
         * 02-事业单位法人证书
         * 03-身份证件
         * 04-其他证明文件
         */
        /**
         * 商户中心代码
         *01,营业执照;
         * 02,统一社会信用代码证书;
         * 03,事业单位法人证书;
         * 04,登记证书
         * 05,组织机构代码证;
         * 06,其他（批文、证明）
         */
        if(IfspDataVerifyUtil.equals(src,"01")){
            return "01";
        }else if(IfspDataVerifyUtil.equals(src,"03")){
            return "02";
        }else {
            return "04";
        }
    }
    /**
     * 选填
     * POS收单
     * 00-传统POS商户
     * 17-多渠道直联终端商户
     * 08-电话终端
     * 多渠道
     * 01-服务提供机构
     * 02-接入渠道机构
     * 04-虚拟商户
     * 05-行业商户
     * 06-服务提供机构＋接入渠道机构
     * 互联网、全渠道
     * 03-互联网、全渠道普通商户
     * 13-互联网、全渠道多渠道商户
     * 移动支付
     * 11-移动支付平台
     * 语音支付
     * 18-语音支付平台商户
     * mPOS商户
     * 19-普通mPOS商户
     * 20-农资收购mPOS商户
     * 21-助农取款mPOS商户
     */



}
