package com.scrcu.ebank.ebap.batch.bean.request;

import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.scrcu.ebank.ebap.batch.common.dict.OpFlagDict;
import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
@Data
public class OrgRepeMergRequest extends CommonRequest{
	@NotBlank(message = "操作标志不能为空")
	private String opFlag;//操作标志
	@NotBlank(message = "撤出机构不能为空")
	private String repeOrg;//撤出机构
	@NotBlank(message = "并入机构不能为空")
	private String mergOrg;//并入机构
	@NotBlank(message = "撤并日期不能为空")
	private String mergDt;//撤并日期
	

	@Override
	public void valid() throws IfspValidException {
		if (!OpFlagDict.REPE_MERG.getCode().equals(opFlag)) {
			throw new IfspValidException(IfspRespCodeEnum.RESP_ERROR.getCode(), "暂时不支持该操作");
		}
	}

}
