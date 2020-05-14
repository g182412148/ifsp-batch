package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import lombok.Data;

/**
 * @author: ljy
 * @create: 2018-11-01 22:46
 */
@Data
public class BthUnionFileDetVO extends CommonDTO {
    private String traceNum;

    private String chkDataDt;

    private String proxyInsCode;

    private String sendInsCode;

    private String transDate;

    private String acctNo;

    private String transAmt;

    private String partPmsAmt;

    private String customerFee;

    private String msgType;

    private String transCode;

    private String merType;

    private String recCardTerminalCode;

    private String recCardCode;

    private String retrivalNo;

    private String sevCdtCode;

    private String authRespCode;

    private String recInsCode;

    private String orgTraceNum;

    private String transRespCode;

    private String sevInputWay;

    private String hearGetFee;

    private String hearPayFee;

    private String routeSevFee;

    private String seReverseFlg;

    private String cardSeq;

    private String terLoadAbity;

    private String othMsg;

    private String transCard;

    private String periodSum;

    private String orderId;

    private String busPayMode;

    private String reserved;

    private String icCdtCode;

    private String orgTransDate;

    private String sndCardInsCode;

    private String transRegion;

    private String terminalType;

    private String eciFlag;

    private String scdPlusFee;

}
