package com.scrcu.ebank.ebap.batch.bean.request;

import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>名称 : 商户中心公共请求报文类 </p>
 * <p>方法 :  </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/6/7  19:37 </p>
 */

@EqualsAndHashCode(callSuper = true)
@Data
//TODO 有些变量不能加set方法要能区分开
public class CommonBatchRequest extends CommonRequest {

    @Override
    public void valid() throws IfspValidException {}

}
