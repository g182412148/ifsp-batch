package com.scrcu.ebank.ebap.batch.bean.response;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccSumInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.PagnResult;
import com.scrcu.ebank.ebap.msg.common.response.ScrcuCommonResponse;
import lombok.Data;

import java.util.List;

/**
 * @author ljy
 */
@Data
public class QueryMerInAccResponse extends ScrcuCommonResponse {
    private static final long serialVersionUID = 1L;

    private List<BthMerInAccSumInfo>  sumList;

    /**
     * 分页结果
     */
    private PagnResult pagnResult;
}
