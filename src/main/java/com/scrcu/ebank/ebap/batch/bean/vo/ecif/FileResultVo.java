package com.scrcu.ebank.ebap.batch.bean.vo.ecif;

import lombok.Data;

/**
 * 描述 </br>
 *
 * @author M.chen
 * 2019/6/13 17:38
 */
@Data
public class FileResultVo {
    /**
     * 信息记录计数
     */
    int count;
    /**
     * 商户信息
     */
    String msg;
}
