package com.scrcu.ebank.ebap.batch.bean.response;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccDtlInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccSumInfoExtend;
import com.scrcu.ebank.ebap.msg.common.response.ScrcuCommonResponse;
import lombok.Data;

import java.util.List;

/**
 * @author ljy
 */
@Data
public class QueryMerInAccDtlResponse extends ScrcuCommonResponse {
    private static final long serialVersionUID = 1L;


    private BthMerInAccSumInfoExtend bthMerInAccSumInfoExtend;

    private List<BthMerInAccDtlInfo> dtlList;

    /**
     * 记录总数
     */
    private long recordTotal;



}
