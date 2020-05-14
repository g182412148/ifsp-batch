package com.scrcu.ebank.ebap.batch.soaclient;/*
 * Copyright (C), 2015-2018, 上海睿民互联网科技有限公司
 * Package com.scrcu.ebank.ebap.order.soaClient
 * Author:   shiyw
 * Date:     2018/7/3 下午10:00
 * Description: //模块目的、功能描述      
 * History: //修改记录
 *===============================================================================================
 *   author：          time：                             version：           desc：
 *   shiyw             2018/7/3下午10:00                     1.0                  
 *===============================================================================================
 */

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.dubbo.bean.SoaResults;

public interface SoaClientService {

    public SoaResults invoke(SoaParams param, String soaCode, String version, String groupId);

}
