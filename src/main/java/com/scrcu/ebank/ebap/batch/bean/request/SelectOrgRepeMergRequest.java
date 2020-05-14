package com.scrcu.ebank.ebap.batch.bean.request;

import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Date;

@Data
public class SelectOrgRepeMergRequest extends CommonRequest{

	@NotBlank(message = "撤出机构不能为空")
	private String repeOrg;//撤出机构
	@NotBlank(message = "并入机构不能为空")
	private String mergOrg;//并入机构
	@NotBlank(message = "撤并日期不能为空")
	private String mergDt;//撤并日期
	

	@Override
	public void valid() throws IfspValidException {
	}

}
