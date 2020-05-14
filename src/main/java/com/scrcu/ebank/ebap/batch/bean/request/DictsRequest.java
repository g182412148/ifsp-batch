package com.scrcu.ebank.ebap.batch.bean.request;

import org.hibernate.validator.constraints.NotEmpty;

import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;

import java.util.List;

/**
 * 名称：〈数据字典查询〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月12日 <br>
 * 作者：qzhang <br>
 * 说明：<br>
 */
public class DictsRequest extends CommonRequest {
    //数据字典类型列表
    @NotEmpty(message = "数据字典类型列表不能为空")
    private List<String> dictTypes;

    public List<String> getDictTypes() {
        return dictTypes;
    }

    public void setDictTypes(List<String> dictTypes) {
        this.dictTypes = dictTypes;
    }

	@Override
	public void valid() throws IfspValidException {
		// TODO Auto-generated method stub
		
	}
}
