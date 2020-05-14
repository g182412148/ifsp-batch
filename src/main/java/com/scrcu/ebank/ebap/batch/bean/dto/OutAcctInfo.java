package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import lombok.Data;

@Data
public class OutAcctInfo extends CommonDTO {
    private String seq;
    private String outAccNo;
    private String outAccName;

}
