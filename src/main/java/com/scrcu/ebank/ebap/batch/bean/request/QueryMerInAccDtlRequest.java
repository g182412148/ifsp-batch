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
public class QueryMerInAccDtlRequest extends ScrcuCommonRequest {

    @NotEmpty(message = "汇总表流水: [txnSsn]不能为空!!!")
    private String txnSsn;

    /**
     * 分页参数:包含页码：pageNo，条数：pageSize,
     */
    private PagnParam pagnParams;

    @Override
    public void valid() throws IfspValidException {

    }
}
