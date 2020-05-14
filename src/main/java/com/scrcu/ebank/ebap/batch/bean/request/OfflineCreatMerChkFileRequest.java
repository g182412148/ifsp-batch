package com.scrcu.ebank.ebap.batch.bean.request;/**
 * Created by Administrator on 2019-04-28.
 */

import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.Data;

/**
 * 名称：〈名称〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019-04-28 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
@Data
public class OfflineCreatMerChkFileRequest extends CommonRequest {

    private String settleDate;

    private String merNo;

    private String pagyNo;

    @Override
    public void valid() throws IfspValidException {

    }
}
