package com.scrcu.ebank.ebap.batch.bean.response;

import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccMchts;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccMchtsDtl;
import com.scrcu.ebank.ebap.batch.bean.dto.PagnResult;
import com.scrcu.ebank.ebap.msg.common.response.ScrcuCommonResponse;
import lombok.Data;

import java.util.List;

/**
 * @author: ljy
 * @create: 2018-08-25 13:52
 */
@Data
public class QueryMerInAccPosiResponse extends ScrcuCommonResponse {

    private BthMerInAccMchts bthMerInAccMchts;

    private List<BthMerInAccMchtsDtl> dtlList;

    /**
     * 分页结果
     */
    private PagnResult pagnResult;
}
