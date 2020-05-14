package com.scrcu.ebank.ebap.batch.bean.request;/**
 * Created by Administrator on 2019-08-01.
 */

import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccInfo;
import lombok.Data;
import lombok.extern.log4j.Log4j;

/**
 * 名称：〈名称〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019-08-01 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
@Data
@Log4j
public class OnceKeepAcctRequest extends KeepAcctCommonRequest {
    private KeepAccInfo keepAccInfo;
}
