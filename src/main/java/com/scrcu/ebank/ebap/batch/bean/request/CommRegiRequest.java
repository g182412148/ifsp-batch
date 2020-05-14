package com.scrcu.ebank.ebap.batch.bean.request;

import org.hibernate.validator.constraints.NotBlank;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.PagnParam;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import com.scrcu.ebank.ebap.msg.common.request.ScrcuCommonRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 名称：〈公共请求报文〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月01日 <br>
 * 作者：qzhang <br>
 * 说明：<br>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommRegiRequest extends ScrcuCommonRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 请求流水
	 */
	@NotBlank(message = "请求流水号不能为空")
	private String reqSsn;
	/**
	 * 请求时间
	 */
	@NotBlank(message = "请求时间不能为空")
	private String reqTm;
	/**
	 * 请求渠道
	 */
	@NotBlank(message = "请求渠道不能为空")
	private String reqChnl;
	/**
	 * 用户编号
	 */
//	@NotBlank(message = "用户编号不能为空")
	private String userNo;
	/** 业务代码 */
	private String busCode;

	/** 版本号 */
	private String version;

	/** 终端号 */
	private String termCode;

	/** 商户号 */
	private String mchtId;
	
	/*
	 * 分页参数:包含页码：pageNo，条数：pageSize,
	 */
//	private PagnParam pagnParams;
	/**
	 *机构编号
	 */
	private String orgCode;

	@Override
	public void valid() throws IfspValidException {
		// 若分页参数不为空，页码和条数不能为空
//		if (IfspDataVerifyUtil.isNotBlank(pagnParams)) {
//			if (pagnParams.getPageSize() <= 0) {
//				throw new IfspValidException(RespConstans.RESP_NONE_PAGE_SIZE.getCode(), RespConstans.RESP_NONE_PAGE_SIZE.getDesc());
//			}
//			if (pagnParams.getPageNo() <= 0) {
//				throw new IfspValidException(RespConstans.RESP_NONE_PAGE_NO.getCode(), RespConstans.RESP_NONE_PAGE_NO.getDesc());
//			}
//		}
	}
}
