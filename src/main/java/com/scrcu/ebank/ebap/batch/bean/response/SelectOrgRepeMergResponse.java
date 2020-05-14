package com.scrcu.ebank.ebap.batch.bean.response;

import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>名称 : 查询机构撤并执行状态返回报文 </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : xiesl </p>
 * <p>日期 : 2019/6/23</p>
 */
@Data
public class SelectOrgRepeMergResponse extends CommonResponse {

    private String mergStus;

}
