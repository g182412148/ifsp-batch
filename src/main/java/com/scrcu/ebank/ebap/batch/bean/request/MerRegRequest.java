package com.scrcu.ebank.ebap.batch.bean.request;

import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author: ljy
 * @create: 2018-10-23 15:53
 */
@Data
public class MerRegRequest extends CommonRequest {

    @NotEmpty(message = "清算日期不能为空")
    private String settleDate;


    @Override
    public void valid() throws IfspValidException {

    }
}
