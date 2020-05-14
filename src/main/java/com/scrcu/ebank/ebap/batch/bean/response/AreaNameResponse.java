package com.scrcu.ebank.ebap.batch.bean.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 描述 </br>
 *
 * @author M.chen
 * 2019/6/13 20:03
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AreaNameResponse extends CommRegiResponse{
    /**
     * 省市区
     */
    private String areaName;

    /**
     * 省市区
     */
    private String legalIfsOrg;
}
