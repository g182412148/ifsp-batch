package com.scrcu.ebank.ebap.batch.bean.request;

import com.scrcu.ebank.ebap.batch.bean.dto.PagnParam;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import com.scrcu.ebank.ebap.msg.common.request.ScrcuCommonRequest;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author ljy
 */
@Data
public class QueryMerInAccRequest extends ScrcuCommonRequest {

    /**
     * 操作员机构号
     */
    @NotEmpty(message = "操作员机构号不能为空")
    private String brId;

    private String stlmDate;

    private String startDate;

    private String endDate;

    private String txnSsn;

    private String merId;

    private String merSimpleName;

    private String setlAcctNo;

    /** 0-未入账 1-入账成功 2-入账失败*/
    private String inAcctStat;


    /**
     * 分页参数:包含页码：pageNo，条数：pageSize,
     */
    private PagnParam pagnParams;

    @Override
    public void valid() throws IfspValidException {

    }
}
