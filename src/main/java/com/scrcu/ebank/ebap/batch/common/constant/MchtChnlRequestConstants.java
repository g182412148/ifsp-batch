package com.scrcu.ebank.ebap.batch.common.constant;

/**
 * 
 * 商户通道请求-常量类
 * 
 * @author zhangjc
 *
 */
public class MchtChnlRequestConstants {
	/************************** 服务器URL ***********************************/
	/** 预申请服务器 */
	public static final String MCHT_CHL_REQUEST_PER_SEVR_URL = "MCHT_CHL_REQUEST_PER_SEVR_URL";
	/** 指定通道申请服务器 */
	public static final String MCHT_CHL_REQUEST_CHL_SEVR_URL = "MCHT_CHL_REQUEST_CHL_SEVR_URL";
	/**商户限流服务器*/
	public static final String MER_LIMIT_URL = "MER_LIMIT_URL";
	/************************** 请求方式 ***********************************/
	public static final String MCHT_CHL_REQUEST_METHOD = "MCHT_CHL_REQUEST_METHOD";
	public static final String MCHT_CHL_REQUEST_GET = "MCHT_CHL_REQUEST_GET";
	/************************** 渠道编号 ***********************************/
	public static final String MCHT_CHL_REQUEST_CHNLNO = "MCHT_CHL_REQUEST_CHNLNO";

	/************************** 编码方式 ***********************************/
	public static final String MCHT_CHL_REQUEST_ENCODING = "MCHT_CHL_REQUEST_ENCODING";

	/**************************
	 * 申请方式【用于选择接口分支】
	 ***********************************/
	/** 申请方式-预申请 **/
	public static final String APPLY_WAY_PER = "预申请";
	/** 申请方式-指定通道申请 **/
	public static final String APPLY_WAY_CHL = "指定通道申请";

	/************************** 申请类型 ***********************************/
	/** 申请类型-1-渠道申请 **/
	public static final String APPLY_TYPE_CHL = "1";
	/** 申请类型-2-渠道商户申请 **/
	public static final String APPLY_TYPE_CHL_MCHT = "2";

	/************************** 申请通道 ***********************************/
	/** 申请通道-301-银联全渠道 **/
	public static final String APPLY_PAGY_UNION_PAY = "301";
	/** 申请通道-302-微信通道 **/
	public static final String APPLY_PAGY_WECHAT = "302";
	/** 申请通道-304-支付宝通道 **/
	public static final String APPLY_PAGY_ALI_PAY = "304";
	/** 申请通道-303-本行通道 **/
	public static final String APPLY_PAGY_BH_PAY = "303";

	/************************** 类目使用方 ***********************************/
	/** 类目使用方-01-微信 **/
	public static final String USER_CODE_WECHAT = "01";
	/** 类目使用方-02-支付宝 **/
	public static final String USER_CODE_ALI_PAY = "02";

	/************************** 行业编码【微信】 ***********************************/
	/** 一级行业编码-00000-默认 **/
	public static final String LEVEL_ONE_CODE_DEFAULT = "00000";
	/** 二级行业编码-00000-默认 **/
	public static final String LEVEL_TWO_CODE_DEFAULT = "00000";
	/** 三级行业编码-00000-默认 **/
	public static final String LEVEL_THREE_CODE_DEFAULT = "00000";

	/************************** 通道申请成功 ***********************************/
	public static final String CHL_APPLY_SUCCESS = "0000";

	/************************** 终端密钥 ***********************************/
	public static final String IP = "unionIp";
	public static final String PORT = "unionPort";
	public static final String TIME_OUT = "unionTimeOut";
	public static final String TLV_OR_XML_FLAG = "tlvOrXmlflag";
	public static final String SYS_ID = "sysID";
	public static final String APP_ID = "appID";
	public static final String COUNT = "unionCount";

	/************************** 终端文件下载路径 ***********************************/
	public static final String PATH = "path";
	
	
	/****************二维码接口服务器地址********************/
	
	public static final String QRC_SEVR_URL = "QRC_SEVR_URL";//申请
	public static final String QRC_STOP_URL = "QRC_STOP_URL";//停用
	
	/**********************支付前值地址******************/
	public static final String PAY_PER_URL = "PAY_PER_URL";
	
	/**二维码图片存放路径**/
	public static final String QRC_TMP_SAVE_PATH = "QRC_TMP_SAVE_PATH";
	
	
	/**二维码图片存放路径**/
	public static final String QRC_BACKGROUND_PATH = "QRC_BACKGROUND_PATH";
	
	/**请求图片上传文件服务器地址**/
	public static final String UPLOAD_FILE = "UPLOAD_FILE";
	/**请求图片访问服务器地址**/
	public static final String SHOW_FILE = "SHOW_FILE";
	/**请求渠道号401-支付管理平台**/
	public static final String REQUEST_CHNL_NO = "REQUEST_CHNL_NO";
	
	//20170417fjf  新增  
	/**请求图片访问服务器地址另一个网段,因生产环境网段有两个的原因**/
	public static final String SHOW_FILE_WITHIN = "SHOW_FILE_WITHIN";
	public static final String MER_FLUSH_URL = "MER_FLUSH_URL";
	public static final String MER_LIMIT_URL_2 = "MER_LIMIT_URL_2";
	public static final String MER_FLUSH_URL_2 = "MER_FLUSH_URL_2";
	
	public static final String MER_IN_ACC_URL = "MER_IN_ACC_URL";
	public static final String ALL_MER_IN_ACC_URL = "MER_IN_ACC_URL_ALL";
	public static final String MER_IN_ACC_URL_2 = "MER_IN_ACC_URL_2";
	public static final String MER_IN_ACC_URL_QUERY="MER_IN_ACC_URL_QUERY";
	//商户手工入账申请通知地址
	public static final String MER_HANDWORK_URL = "MER_HANDWORK_URL";
	//商户手工入账申请入账结果查询请求地址
	public static final String MER_HANDWORK_RESULT_URL = "MER_HANDWORK_RESULT_URL";
	
	/**商户限流dubbo广播地址*/
	public static final String ZKURL = "ZKURL";
	/************************** 为每个商户绑定APPID的URL ***********************************/
	public static final String BIND_MCHT_APPID_URL="BIND_MCHT_APPID_URL";
	/**银行服务商对应的APPID*/
	public  static final String BANK_APPID="BANK_APPID";
	/**银行的服务商商户号（特殊费率通道）*/

}
