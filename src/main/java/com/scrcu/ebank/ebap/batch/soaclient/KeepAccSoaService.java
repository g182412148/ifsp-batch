package com.scrcu.ebank.ebap.batch.soaclient;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;
import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccInfo;

/**
 *名称：<本行通道soa服务接口> <br>
 *功能：<功能详细描述> <br>
 *方法：<方法简述 - 方法描述> <br>
 *版本：1.0 <br>
 *日期：2018/7/27 <br>
 *作者：lijingbo <br>
 *说明：<br>
 */

public interface KeepAccSoaService {
 
    /**
     * 记账接口
     * @param soaParams
     * @return
     */
    SoaResults keepAcc(SoaParams soaParams);

	SoaResults debitBill(SoaParams soaParams);

    /**
     * 单笔记账
     * @param soaParams
     * @return
     */
    SoaResults onceKeepAcc(SoaParams soaParams);


    /**
     * 查询记账结果
     * @param params
     * @return
     */
    SoaResults qrcKeepAccRst(SoaParams params);

    /**
     * 调用本行通道冲正接口
     * @param params
     * @return
     */
    SoaResults ibankRevkeepAcc(SoaParams params);

    /**
     * 单笔冲正
     * @param params
     * @return
     */
    SoaResults onceRevKeepAcc(SoaParams params);

    /**
     * 订单单笔补记账服务
     * @param soaParams
     * @return
     */
    SoaResults supplyAcc(SoaParams soaParams);
}
