package com.scrcu.ebank.ebap.batch.common.msg;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.utils.message.IfspStringUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.PagnParam;
import com.scrcu.ebank.ebap.batch.bean.request.MonthTradeStatisticsRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MchtMsg {

	public static SoaParams querySubMchtInfoMmg(SoaParams params, MonthTradeStatisticsRequest request) {
		log.info("-----------组装分店信息查询接口报文开始-----------");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

//      @_@ 写死的...
//		params.put("reqSsn", "81603412076430024281563401066343");
//		params.put("reqChnl", "03");// app
//		params.put("userNo", "82018070514215821808179864482415");
//		params.put("termCode", "bc15922fc35eb56983773a5e699dc26b1bc92c6a");

		params.put("reqSsn", request.getReqSsn());
		params.put("reqChnl", "03");// app
		params.put("userNo", request.getUserId());
		params.put("termCode", request.getTermCode());
		params.put("reqTm", sdf.format(new Date()));

		PagnParam pagnParam = new PagnParam();

		if (IfspStringUtil.isNotBlank(request.getPageNo())) {
			pagnParam.setPageNo(Integer.parseInt(request.getPageNo()));
		}
		if (IfspStringUtil.isNotBlank(request.getPageSize())) {
			pagnParam.setPageSize(Integer.parseInt(request.getPageSize()));
		}

		params.put("pagnParams", pagnParam);

		// params.put("mchtId", "10021101070000576");
		params.put("mchtId", request.getMchtId());

		log.info("-----------组装分店信息查询接口报文结束-----------");
		return params;
	}
}
