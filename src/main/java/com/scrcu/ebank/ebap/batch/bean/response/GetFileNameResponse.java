package com.scrcu.ebank.ebap.batch.bean.response;/**
 * Created by Administrator on 2019-04-30.
 */

import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 名称：〈名称〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019-04-30 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetFileNameResponse extends CommonResponse {
    private String downloadType;
    private String fileName;
}
