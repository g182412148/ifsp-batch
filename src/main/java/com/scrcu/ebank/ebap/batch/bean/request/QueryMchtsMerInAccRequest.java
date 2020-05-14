package com.scrcu.ebank.ebap.batch.bean.request;

import com.scrcu.ebank.ebap.batch.bean.dto.PagnParam;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import com.scrcu.ebank.ebap.msg.common.request.ScrcuCommonRequest;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author: ljy
 * @create: 2018-08-25 12:22
 */
@Data
public class QueryMchtsMerInAccRequest extends ScrcuCommonRequest {

    private String beginDate;
    private String endDate;
    /**
     * 结算日期
     */
    @NotEmpty(message = "结算日期不能为空")
    private String inAcctDate;

    /**
     * 商户号
     */
    @NotEmpty(message = "商户号不能为空")
    private String merId;

    /**
     * 分页参数:包含页码：pageNo，条数：pageSize,
     */
    private PagnParam pagnParams;

    @Override
    public void valid() throws IfspValidException {

    }
}
