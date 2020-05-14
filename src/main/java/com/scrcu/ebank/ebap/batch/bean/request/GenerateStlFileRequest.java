package com.scrcu.ebank.ebap.batch.bean.request;

import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class GenerateStlFileRequest extends CommonRequest {

    @Override
    public void valid() throws IfspValidException {

    }
}
