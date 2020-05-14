package com.scrcu.ebank.ebap.batch.common.constant;

/**
 * 常量类
 * @author lijingbo
 */
public class Constans {
 
    /**币种**/
    public static final String CCY_TYPE = "156";
    /**通道系统编号**/
    public static final String WX_SYS_NO = "605";
    public static final String ALI_SYS_NO = "606";
    public static final String UNION_SYS_NO = "607";
    public static final String IBANK_SYS_NO = "604";
    public static final String LOAN_SYS_NO = "999";             //FOR loan ORDER CHECK ONLY
    public static final String LOAN_SYS_POINT = "998";           //FOR point ORDER CHECK ONLY
    /** 银联全渠道 */
    public static final String ALL_CHNL_UNION_SYS_NO = "608";
    
    /**********************商户类型****************/
    /**线上一级商户**/
    public static final String MER_TYPE_ONLINE_FIRST = "1001";
    /**线上二级级商户**/
    public static final String MER_TYPE_ONLINE_SECOND = "1002";
    /**线下总店**/
    public static final String MER_TYPE_SM_MAIN = "0001";
    /**线下分店**/
    public static final String MER_TYPE_SM_BRANCH = "0002";
    
    /**********************结算类型****************/
    /**实时结算**/
    public static final String STL_TYPE_INTIME = "01";
    /**隔日直接D+N**/
    public static final String STL_TYPE_DN = "0";
    /**隔日直接T+N**/
    public static final String STL_TYPE_TN = "1";
    /**按月结算**/
    public static final String STL_TYPE_BY_MONTH = "2";
    
    /*********************订单是否已实时结算***************/
    /**已实时结算**/
    public static final String MCHT_SETTLE_TYPE_0 = "0";
    /**未实时结算**/
    public static final String MCHT_SETTLE_TYPE_1 = "1";
    
    /**********************结算方式****************/
    /**结算至本商户**/
    public static final String STL_TO_MER = "01";
    /**结算至上级商户**/
    public static final String STL_TO_PARENT_MER = "02";
    /**结算至上级商户,再结算至本商户**/
    public static final String STL_TO_MER_INDIREACTLY = "03";
    
    /**********************渠道类型****************/
    /** 00.本行卡记账**/
    public static final String CHANNEL_TYPE_CORE = "00";
    /** 01.微信记账**/
    public static final String CHANNEL_TYPE_WX = "01";
    /** 02.支付宝记账**/
    public static final String CHANNEL_TYPE_ALI = "02";
    /** 03.银联二维码记账**/
    public static final String CHANNEL_TYPE_UNIONPAY = "03";
    
    /**********************对账状态****************/
    /**未对账**/
    public static final String CHK_STATE_00 = "00";
    /**已对账**/
    public static final String CHK_STATE_01 = "01";
    
    /**********************对账结果****************/
    /**一致**/
    public static final String CHK_RST_00 = "00";
    /**不一致**/
    public static final String CHK_RST_01 = "01";
    /**可疑**/
    public static final String CHK_RST_02 = "02";
    /**单边账**/
    public static final String CHK_RST_03 = "03";
    /**忽略账**/
    public static final String CHK_RST_04 = "04";
    
    /**********************清算状态****************/
    /**已清算**/
    public static final String STLM_ST_01 = "01";
    /**未清算**/
    public static final String STLM_ST_00 = "00";
    /**
     * 清算挂起
     */
    public static final String STLM_ST_02 = "02";

    /**********************差错状态****************/
    /**本地存在，第三方不存在**/
    public static final String ERR_TP_01 = "01";
    /**第三方存在，本地不存在**/
    public static final String ERR_TP_02 = "02";
    /**通道手续费不一致**/
    public static final String ERR_TP_03 = "03";
    /**订单不存在记账信息**/
    public static final String ERR_TP_04 = "04";
    /**交易金额不一致**/
    public static final String ERR_TP_001 = "001";
    /**交易结果不一致**/
    public static final String ERR_TP_010 = "010";
    /**交易结果金额均不一致**/
    public static final String ERR_TP_011 = "011";
    /**交易类型结果金额均不一致**/
    public static final String ERR_TP_111 = "111";
    /**交易类型结果均不一致**/
    public static final String ERR_TP_110 = "110";
    /**交易类型金额均不一致**/
    public static final String ERR_TP_101 = "101";
    /**交易类型不一致**/
    public static final String ERR_TP_100 = "100";
    
    /**********************交易状态****************/
    /**失败**/
    public static final String TRADE_ST_01 = "01";
    /**成功**/
    public static final String TRADE_ST_00 = "00";
    /**处理中**/
    public static final String TRADE_ST_02 = "02";

    /**********************差错处理状态****************/
    /**未处理**/
    public static final String PROC_ST_01 = "01";
    /**已处理**/
    public static final String PROC_ST_02 = "02";
    
    /*****清分状态****/
    /**处理结果状态:00：未处理；**/
	public static final String DEAL_RESULT_NOT="00";
	/**处理结果状态:01：处理中；**/
	public static final String DEAL_RESULT_HANDING="01";
    /**处理结果状态:02：处理成功；**/
	public static final String DEAL_RESULT_SUCCESS="02";
    /**处理结果状态:03：处理失败；**/
	public static final String DEAL_RESULT_FAILE="03";
	
//	/**分录流水类型:00-结算账户补保证金**/
//	public static final String ENTRY_TYPE_MER_FOR_GUARANTEE="00";
	/**分录流水类型:01-商户入账(商户本金结算)**/
	public static final String ENTRY_TYPE_MER="01";          //商户入账：消费时为正金额，退款时为负金额

	/**分录流水类型:02-手续费垫付（行内手续费垫付）**/
	public static final String ENTRY_TYPE_FEE_ADVANCE="02";   //暂无使用
	/**分录流水类型:03-商户手续费扣帐（商户手续费结算）**/
	public static final String ENTRY_TYPE_MER_FEE="03";       //暂无使用
	/**分录流水类型:04-第三方手续费入账（第三方资金通道的手续费结算）**/
	public static final String ENTRY_TYPE_BRANCH_FEE="04";    //暂无使用
	/**分录流水类型:05-手续费分润（收单机构）**/
	public static final String ENTRY_TYPE_FEE_GAINS_SD_ORG="05";     //消费+未结算退款时使用
	/**分录流水类型:06-手续费分润（发卡行）**/
	public static final String ENTRY_TYPE_FEE_GAINS_OPEN_ORG="06";   //消费+未结算退款时使用
	/**分录流水类型:07-手续费分润（通存通兑）**/
	public static final String ENTRY_TYPE_FEE_GAINS_UNIVERSAL="07";
	/**分录流水类型:08-手续费分润（电子银行中心/运营机构）**/
	public static final String ENTRY_TYPE_FEE_GAINS_OPERATE_ORG="08";  //消费+未结算退款时使用
    /**分录流水类型:08-手续费分润（服务商）**/
	public static final String ENTRY_TYPE_FEE_GAINS_SERVICE_ORG="16";  //消费+未结算退款时使用

    /**分录流水类型:11-手续费支出（服务商）**/
    public static final String ENTRY_TYPE_FEE_PAY_SERVICE_ORG="17";   //已结算退款时使用
	/**分录流水类型:09-手续费支出（收单机构）**/
	public static final String ENTRY_TYPE_FEE_PAY_SD_ORG="09";        //已结算退款时使用
	/**分录流水类型:10-手续费支出（发卡行）**/
	public static final String ENTRY_TYPE_FEE_PAY_OPEN_ORG="10";      //已结算退款时使用
	/**分录流水类型:11-手续费支出（电子银行中心/运营机构）**/
	public static final String ENTRY_TYPE_FEE_PAY_OPERATE_ORG="11";   //已结算退款时使用
	
	/**分录流水类型:12-手续费支出（电子银行补贴手续费支出）**/
	public static final String ENTRY_TYPE_FEE_PAY_EBANK="12";
	/**分录流水类型:13-手续费支出（电子银行补贴手续费支出）**/
	public static final String ENTRY_TYPE_FEE_GUARANTEE="13";
	/**分录流水类型:14-商户佣金收入**/
	public static final String ENTRY_TYPE_COMM_IN="14";
	/**分录流水类型:20-二级商户隔日间接入账**/
	public static final String ENTRY_TYPE_SUBMER_IN="20";
	/**分录流水类型:97-日间记账(处理日间记账失败数据)**/
	public static final String ENTRY_TYPE_FOR_ACCOUNT="97";
	/**分录流水类型:98-内部帐补保证金**/
	public static final String ENTRY_TYPE_FOR_GUARANTEE="98";
	
	/**分录流水类型:99-商户退款**/
	public static final String ENTRY_TYPE_PAY_MER="99";

    /**分录流水类型:18-他行手续费**/
    public static final String ENTRY_TYPE_FEE_OTHER="18";
	
	
	/**分录流水类型:80-向一级商户返佣**/
	public static final String ENTRY_TYPE_MER_COMM="80";
	/**分录流水类型:81-向一级商户入账**/
	public static final String ENTRY_TYPE_MER_IN="81";
	/**分录流水类型:82-二级商户/分店返佣**/
	public static final String ENTRY_TYPE_BRANCH_COMM="82";
	/**分录流水类型:83-二级商户/分店交易入账至总店**/
	public static final String ENTRY_TYPE_BRANCH2MER="83";
	/**分录流水类型:84-二级商户/分店交易入账**/
	public static final String ENTRY_TYPE_BRANCH_IN="84";
	/**分录流水类型:85-平台商户佣金**/
	public static final String ENTRY_TYPE_PLAT_COMM="85";
	

	/**入账状态:00：未入账；**/
	public static final String ACCOUNT_STATUS_NOT="00";
    /**入账状态:01：入账成功；**/
	public static final String ACCOUNT_STATUS_SUCCESS="01";
    /**入账状态:02：入账失败；**/
	public static final String ACCOUNT_STATUS_FAILE="02";
	
	/****************结算状态*****************/
	/** 结算状态：00-初始化状态  **/
    public static final String  SETTLE_STATUS_NOT_CLEARING = "00";
    /** 结算状态：01-结算中  **/
    public static final String  SETTLE_STATUS_CLEARING = "01" ;
    /** 结算状态：02-结算失败  **/
    public static final String  SETTLE_STATUS_FAILE_CLEARING = "02";
    /** 结算状态：03-结算成功  **/
    public static final String  SETTLE_STATUS_SUCCESS_CLEARING = "03";
    /** 结算状态：04-结算成功手续费未清分  **/
    public static final String  SETTLE_STATUS_SUCCESS_NOTCLEARING = "04";
    /** 结算状态：05-初始化  **/
    public static final String  SETTLE_STATUS_INIT = "05";
    
    //************订单类别*********************************
  	/** 订单类别-01消费 */
  	public static final String ORDER_TYPE_CONSUME = "01";
  	/** 订单类别-02退款 */
  	public static final String ORDER_TYPE_RETURN = "02";
  	
  	/*********退款账户类型*********/
	public static final String REFUND_ACC_TYPE_CLEANER = "01" ;      //内部账
	public static final String REFUND_ACC_TYPE_GUARANTEE = "03" ;    //保证金账户
	public static final String REFUND_ACC_TYPE_SETTLE = "02" ;       //结算账户


    /***********************入账汇总区分类型*****************************/
    /** 通道入账*/
    public static final String IN_ACCT_TYPE_PAGY = "00";
    /** 商户入账*/
    public static final String IN_ACCT_TYPE_MCHT = "01";
    /** 手续费入账支出 */
    public static final String IN_ACCT_TYPE_FEE = "02";
    /** 保证金入账*/
    public static final String IN_ACCT_TYPE_GUARANTEE = "03";
    /** 商户返佣*/
    public static final String IN_ACCT_TYPR_COMMISSION = "04";
    /** 日间记账失败入账*/
    public static final String IN_ACCT_TYPE_DAY_FAIL = "05";
    /** 手续费入账收入*/
    public static final String IN_ACCT_TYPE_FEE_2 = "06";
    
    /** 向商户内部帐转账，用于间接结算*/
    public static final String IN_ACCT_TYPE_TRANSFER = "07";
    /** 二级商户间接结算*/
    public static final String IN_ACCT_TYPE_SUBMER = "08";


    /****************************清算账户类型*****************************/
    /** 本行*/
    public static final String SETTL_ACCT_TYPE_PLAT = "0";
    /** 他行*/
    public static final String SETTL_ACCT_TYPE_CROSS = "1";
    
    /****************************物流类型*****************************/
	/** 物流类型-01统一物流 */
    public static final String LOGIS_TYPE_BANK = "01";
    /** 物流类型-02平台物流 */
    public static final String LOGIS_TYPE_PLAT = "02";
    
    /****************************组织类型*****************************/
   	/**组织类型-01收单机构*/
    public static final String ORG_TYPE_SD= "01";
    /**组织类型-02运营机构 */
    public static final String ORG_TYPE_OP = "02";
    /**组织类型-03运营商*/
    public static final String ORG_TYPE_OPBYMER = "03";
    /**组织类型-04平台商户 */
    public static final String ORG_TYPE_PLAT = "04";
    /**组织类型-05物流公司 */
    public static final String ORG_TYPE_LOGIS = "05";

    /**************************借贷标记******************************/
    /** 指定账户*/
    public static final String BORROW_ALLOCATED_ACCT = "1";
    /** 手续费待分配*/
    public static final String BORROW_FEE_TO_BE_ALLOCATED = "2";
    /** 手续费支出*/
    public static final String BORROW_FEE_EXPENSE = "3";


    /** 指定账户*/
    public static final String LEND_ALLOCATED_ACCT = "1";
    /** 手续费待分配*/
    public static final String LEND_FEE_TO_BE_ALLOCATED = "2";
    /** 手续费收入*/
    public static final String LEND_FEE_INCOME = "3";

    /*************交易类型***********/
    /**被扫支付**/
    public static final String TXN_TYPE_O1000001 = "O1000001";   
    /**主扫支付**/
    public static final String TXN_TYPE_O1000002 = "O1000002";   
    /**公众号支付**/
    public static final String TXN_TYPE_O1000003 = "O1000003";   
    /**交易撤销**/
    public static final String TXN_TYPE_CANCEL = "O1100001";   
    /**交易退款**/
    public static final String TXN_TYPE_REFUND = "O1200001";  
    
    /**线上支付**/
    public static final String TXN_TYPE_ONLINE_PAY = "O1000006";
    /**线上退款**/
    public static final String TXN_TYPE_ONLINE_REFUND = "O2000006";

    /*************文件处理状态******************************************/
    /**
     * 未处理
     */
    public static final String FILE_DEAL_STATUS_NOT = "00";
    /**
     * 已处理
     */
    public static final String FILE_DEAL_STATUS_PROCESSED = "01";

    /*************返佣类型***********/
    /**00-无返佣(缺省);*/
    public static final String COMM_TYPE_NONE = "00";
    /**01-每笔收取固定返佣金额;*/
    public static final String COMM_TYPE_FIX_AMT = "01";
    /**02-按照金额比率收取返佣;*/
    public static final String COMM_TYPE_BY_RATE = "02";

    /******************************记账状态*********************************/
    /**待记账*/
    public static final String KEEP_ACCOUNT_STAT_PRE = "00";
    /**记账中*/
    public static final String KEEP_ACCOUNT_STAT_IN = "01";
    /**记账成功*/
    public static final String KEEP_ACCOUNT_STAT_SUCCESS = "02";
    /**记账失败*/
    public static final String KEEP_ACCOUNT_STAT_FAIL = "03";
    /**记账超时*/
    public static final String KEEP_ACCOUNT_STAT_TIMEOUT = "04";
    /**交易已冲正*/
    public static final String KEEP_ACCOUNT_STAT_REVERSE = "05";

    /***************************记账标志************************************/
    /**同步记账*/
    public static final String  KEEP_ACCT_FLAG_SYNC = "00";
    /**异步记账*/
    public static final String  KEEP_ACCT_FLAG_ASYNC = "01";

    /***************************记账类型***********************************/
    /** 支付/退款记账 10*/
    public static final String KEEP_ACC_TYPE_OTHER = "10";
    /** 冲正记账 20*/
    public static final String KEEP_ACC_TYPE_REVERSE = "20";

    /************************  记账表账户类型 *******************************/
    /** 客户账*/
    public static final String  CUST = "00";
    /** 商户待清算账户*/
    public static final String  MCHT_WAIT_SETTLE = "01";
    /** 商户结算账户*/
    public static final String  MCHT_SETTLE = "02";
    /** 商户备付金账户*/
    public static final String  MCHT_BACK = "03";
    /** 过渡户*/
    public static final String  TEMP = "04";
    /** 微信待清算账户*/
    public static final String  WECHAT_WAIT_SETTLE = "05";
    /** 支付宝待清算账户*/
    public static final String  ALI_WAIT_SETTLE = "06";
    /** 银联待清算账户*/
    public static final String  UN_WAIT_SETTLE = "07";

    /********************* 入账明细表与入账汇总表入账状态 *******************/
    /** 未入账*/
    public static final String  IN_ACC_STAT_PRE = "0";
    /** 入账成功*/
    public static final String  IN_ACC_STAT_SUCC = "1";
    /** 入账失败*/
    public static final String  IN_ACC_STAT_FAIL = "2";
    /** 已入账部分成功*/
    public static final String  IN_ACC_STAT_SCTIONSUCC = "3";

    /**********************入账汇总表 处理状态******************************/
    /** 未处理*/
    public static final String  HANDLE_STATE_PRE = "0";
    /** 处理中*/
    public static final String  HANDLE_STATE_IN = "1";
    /** 处理成功*/
    public static final String  HANDLE_STATE_SUCC = "2";
    /** 处理失败*/
    public static final String  HANDLE_STATE_FAIL = "3";
    /** 转账失败：转账失败记录不能再次处理*/
    public static final String  HANDLE_STATE_TRANSFER_FAIL = "4";
    
    
    /**********************支付渠道******************************/
    /** 微信*/
    public static final String  GAINS_CHANNEL_WX = "01";
    /** 支付宝*/
    public static final String  GAINS_CHANNEL_ALI = "02";
    /** 惠支付*/
    public static final String  GAINS_CHANNEL_HZF = "03";
    /** 蜀信卡*/
    public static final String  GAINS_CHANNEL_SXK = "05";
    /** 授信支付*/
    public static final String  GAINS_CHANNEL_LOAN = "06";
    /** 蜀信E*/
    public static final String  GAINS_CHANNEL_SXE = "07";
    /** 银联*/
    public static final String  GAINS_CHANNEL_UNIONPAY = "08";
    
    /** 积分支付*/
    public static final String  GAINS_CHANNEL_POINT = "10";

    /** 微信-线下*/
    public static final String  GAINS_CHANNEL_OFFLIN_WX = "11";
    /** 支付宝-线下*/
    public static final String  GAINS_CHANNE_OFFLINL_ALI = "12";
    /** 蜀信E-线下*/
    public static final String  GAINS_CHANNEL_OFFLIN_SXE = "13";
    /** 银联-线下*/
    public static final String  GAINS_CHANNEL_OFFLIN_UNIONPAY = "14";


    //OLD CHANNEL 00-蜀信e  01-蜀信卡快捷 02-银联 03-微信 04-支付宝 05-易宝 06-快钱 07-积分 08-红包 09-优惠券 10-面值卡
    /** 00-蜀信e*/
    public static final String  OLD_CHANNEL_SXE = "00";
    /** 01-蜀信卡快捷*/
    public static final String  OLD_CHANNEL_SXC = "01";
    /** 02-银联*/
    public static final String  OLD_CHANNEL_UNIONPAY = "02";
    /** 03-微信*/
    public static final String  OLD_CHANNEL_WX = "03";
    /** 04-支付宝*/
    public static final String  OLD_CHANNEL_ALI = "04";
    /** 05-易宝*/
    public static final String  OLD_CHANNEL_YIBAO = "05";

    /** 06-快钱*/
    public static final String  OLD_CHANNEL_KQ = "06";
    /** 07-积分*/
    public static final String  OLD_CHANNEL_POINT = "07";
    /** 08-红包*/
    public static final String  OLD_CHANNEL_HB = "08";
    /** 09-优惠券*/
    public static final String  OLD_CHANNEL_YHQ = "09";
    /** 10-面值卡*/
    public static final String  OLD_CHANNEL_MZK = "10";

    
    /**********************可疑标识******************************/
    /** 不存在可疑*/
    public static final String  DUBIOUS_FLAG_00 = "00";
    /** 存在可疑*/
    public static final String  DUBIOUS_FLAG_01 = "01";
    
    /**********************上送文件状态******************************/
    /** 未处理*/
    public static final String FILE_STATUS_00="00";
    /** 处理中*/
    public static final String FILE_STATUS_01="01";
    /** 处理成功*/
    public static final String FILE_STATUS_02="02";
    /** 处理失败*/
    public static final String FILE_STATUS_03="03";
    /** 处理完成*/
    public static final String FILE_STATUS_04="04";
    
    /**********************入账文件类型******************************/
    /** 通道入账文件*/
    public static final String FILE_TYPE_PAGY="00";
    /** 本行商户入账文件*/
    public static final String FILE_TYPE_MCHT="01";
    /** 手续费入账文件*/
    public static final String FILE_TYPE_FEE="02";
    /** 他行商户入账文件*/
    public static final String FILE_TYPE_OTHER_MCHT="03";

    /*************************文件记录表文件类型***********************/
    /** 本行入账文件*/
    public static final String FILE_IN_ACC = "00";
    /** 他行入账文件*/
    public static final String FILE_OTHER_IN_ACC = "01";
    /**隔日间接转账文件*/
    public static final String FILE_TYPE_TRANSFER = "02";
    /**间接结算入账文件*/
    public static final String FILE_TYPE_SUBMER_STL = "03";

    /***********************他行结算入账方式**************************/
    /** 单笔入账*/
    public static final String DATA_TYPE_SINGLE="00";
    /** 拆分入账*/
    public static final String DATA_TYPE_DIV="01";
    /***********************订单账户类型**************************/
    
    /** 全账户*/
    public static final String ORDER_ACCTTYPE_ALL="000";
    /** 银联卡账户*/
    public static final String ORDER_ACCTTYPE_OTHER_BANK="001";
    /** 支付宝账户*/
    public static final String ORDER_ACCTTYPE_ALIPAY="002";
    /** 微信账户*/
    public static final String ORDER_ACCTTYPE_WECHAT="003";
    /** 本行账户*/
    public static final String ORDER_ACCTTYPE_MY_BANK="004";
    
    /***********************订单发起渠道编号**************************/
    
    /** 微信APP*/
    public static final String ORDER_CHL_NO_WECHAR="01";
    /** 支付宝APP*/
    public static final String ORDER_CHL_NO_ALIPAY="02";
    /** 惠支付APP(商户版) */
    public static final String ORDER_CHL_NO_MCHT="04";
    /** 蜀信APP*/
    public static final String ORDER_CHL_NO_SX_E="07";
    /** 新蜀信APP*/
    public static final String ORDER_CHL_NO_NIE="29";
    /** 银联APP*/
    public static final String ORDER_CHL_NO_UNIONPAY="08";
    /** 惠支付APP(个人版) */
    public static final String ORDER_CHL_NO_PER="26";

    /********************** 记账结果通知状态 *************************/
    /** 未通知*/
    public static final String ASYNC_NOTICE_PRE="00";
    /** 已通知*/
    public static final String ASYNC_NOTICE_SUCC="01";

    /********************** 定时任务 **************************/
    /**
     * 商户日交易统计任务
     */
    public static final String TASK_DAY_TXN="01";

    /**
     * 商户日交易金额分段统计任务
     */
    public static final String TASK_DAY_TXN_AMT_SECTION="02";

    /**
     * 商户每2小时交易统计任务
     */
    public static final String TASK_HOUR_TXN="03";
    
    /**
     * 同步操作员任务
     */
    public static final String SYNCHRONIZE_STAFF="04";
    
    /**
     * 同步机构任务
     */
    public static final String SYNCHRONIZE_ORG="05";

    /******************** 定时任务执行状态 **********************/

    /**
     * 执行状态 : 执行完成
     */
    public static final String EXECUTE_STAT_OFF="00";

    /**
     * 执行状态 : 执行中
     */
    public static final String EXECUTE_STAT_ON="01";

    //二级商户结算状态00-未处理 01-划账中 02-划账完成 03-划账失败 04-结算中 05-结算失败 06-结算成功
    /****************结算状态*****************/
	/** 结算状态：00-未处理  **/
    public static final String  JS2_STATUS_UNHANDLE = "00";
    /** 结算状态：01-划账中  **/
    public static final String  JS2_STATUS_TRANSFERING = "01" ;
    /** 结算状态：02-划账完成  **/
    public static final String  JS2_STATUS_TRANSFER_SUCC = "02";
    /** 结算状态：03-划账失败  **/
    public static final String  JS2_STATUS_TRANSFER_FAIL = "03";
    /** 结算状态：04-结算中  **/
    public static final String  JS2_STATUS_CLEARING = "04";
    /** 结算状态：05-结算失败  **/
    public static final String  JS2_STATUS_FAIL = "05";
    /** 结算状态：06-结算成功  **/
    public static final String  JS2_STATUS_SUCC = "06";
    
    /****************转账状态*****************/
  	/** 转账状态：00-未处理  **/
    public static final String  TRANSFER_STATUS_UNHANDLE = "00";
    /** 转账状态：01-处理中  **/
    public static final String  TRANSFER_STATUS_DEALING = "01" ;
    /** 转账状态：02-处理成功  **/
    public static final String  TRANSFER_STATUS_SUCC = "02";
    /** 转账状态：03-处理失败  **/
    public static final String  TRANSFER_STATUS_FAIL = "03";
    
    /****************记账文件相关常量*****************/
    /** 费用编号：4201  **/
    public static final String  FEE_CATALOG = "4201";
    /** 交易币种：01-人民币  **/
    public static final String  TXN_CUR_CNY = "01";
    /** 钞汇标志：  **/
    public static final String  TXN_BILL_FLAG = "";
    
    /** 交易摘要码：  **/
    public static final String  TXN_SUMMARY_CODE = "200";
    /** 交易描述：  **/
    public static final String  TXN_SUMMARY_TRANSFER = "隔日间接结算转账";
    /** 交易描述：  **/
    public static final String  TXN_SUMMARY_SUBMER = "二级商户结算";
    /** 交易描述：  **/
    public static final String  TXN_SUMMARY_OTHER = "OTHER";
    
    /****************核心相关常量*****************/
    public static final String  CORE_RETURN_CODE_SUCCESS = "0000";
    public static final String  DEAL_RESULT_DESC_SUCC = "处理成功";
    public static final String  DEAL_RESULT_DESC_DEALING = "处理中";
    public static final String  DEAL_RESULT_DESC_FAIL = "处理失败";
    public static final String  DEAL_RESULT_DESC_TRANSFER_FAIL = "转账失败";
    public static final String  ACC_RESULT_DESC_SUCC = "SUCCESS";
    public static final String  ACC_RESULT_DESC_FAIL = "FAIL";
    
    /****************转账表状态*****************/
  	/** 后续处理标志 00-未处理  **/
    public static final String  HANDLE_FLAG_UNHANDLE = "00";
    /** 后续处理标志 01-处理中  **/
    public static final String  HANDLE_FLAG_DEALING = "01" ;
    /** 后续处理标志 02-处理成功  **/
    public static final String  HANDLE_FLAG_SUCC = "02";
    /** 后续处理标志 03-处理失败  **/
    public static final String  HANDLE_FLAG_FAIL = "03";
    /** 后续处理标志 04-无需处理  **/
    public static final String  HANDLE_FLAG_NOT_NEED = "04";

    /*****************  挂账状态    *****************/
    /** 挂账中  */
    public static final String HANG_ST_00 = "00";
    /** 挂账已处理  */
    public static final String HANG_ST_01 = "01";
    
    /*****************  通知结算状态    *****************/
    /** 0-未通知  */
    public static final String NOTICE_FLAG_NO = "0";
    /** 1-无需通知，自动结算  */
    public static final String NOTICE_FLAG_AUTO = "1";
    /** 2-已通知*/
    public static final String NOTICE_FLAG_YES = "2";
    
    /*****************  商户级别    *****************/
    /** 00-平台商户  */
    public static final String MER_LEVLE_00 = "00";
    /** 01-一级商户  */
    public static final String MER_LEVLE_01 = "01";
    /** 02-二级商户  */
    public static final String MER_LEVLE_02 = "02";
    /** 03-运营商  */
    public static final String MER_LEVLE_03 = "03";
    
    /*****************  微信订单来源    *****************/
    /** 01-间连微信订单  */
    public static final String WX_ORDER_TYPE_INDIRECT = "01";
    /** 02-直连微信订单  */
    public static final String WX_ORDER_TYPE_DIRECT = "02";
    
    /*****************  订单支付状态    *****************/
    /** 01-间连微信订单  */
    public static final String PAY_STATUS_SUCC = "10";
    
    /*****************  订单状态    *****************/
    /** 00-订单成功  */
    public static final String ORDER_STATUS_SUCC = "00";
    
    /** 01-已撤销  */
    public static final String ORDER_STATUS_REVOKE = "01";
    
    /** 02-已发生退款  */
    public static final String ORDER_STATUS_REFUND = "02";
    
    /** 03-已关闭  */
    public static final String ORDER_STATUS_CLOSED = "03";
    
    /** 09-订单失败  */
    public static final String ORDER_STATUS_FAIL = "09";
    
    /** 10-订单已受理  */
    public static final String ORDER_STATUS_INIT = "10";
    
    /** 11-订单处理中  */
    public static final String ORDER_STATUS_PROCESSING = "11";
    
    /** 12-用户输密中  */
    public static final String ORDER_STATUS_USERPING = "12";
    
    /** 13-订单处理超时  */
    public static final String ORDER_STATUS_UNKNOW = "13";
    
    /*****************  优惠信息类型    *****************/
    /** 01-平台红包  */
    public static final String CONSUME_TYPE_PLAT = "01";
    /** 02-机构红包  */
    public static final String CONSUME_TYPE_ORG = "02";
    /** 05-物流  */
    public static final String CONSUME_TYPE_LOGIS = "05";
    /** 06-积分 */
    public static final String CONSUME_TYPE_POINT = "06";
    
    /*****************  商户审核状态   00-未审核；01-审核通过；02-审核拒绝； 03 - 待补充材料 *****************/
    /** 00-未审核  */
    public static final String AUDIT_ST_00 = "00";
    /** 01-审核通过  */
    public static final String AUDIT_ST_PASS = "01";
    /** 02-审核拒绝  */
    public static final String AUDIT_ST_REFUSE = "02";
    /** 03-待补充材料 */
    public static final String AUDIT_ST_NEEDMOREINFO = "03";   //当审核不通过处理
    
    /***************** RPS 审核状态(线上收单时状态，映射用)  *****************/
    /** 02-审核通过  */
    public static final String APPROVE_RESULT_AGREE = "02";
    /** 03-不通过 */
    public static final String APPROVE_RESULT_REFUSE = "03";   
    
    /***************** RPS 结算账户类型(线上收单时状态，映射用)  *****************/
    /** 01-本行  */
    public static final String ACC_TYPE_SCRCU = "01";     
    /** 02-他行 */
    public static final String ACC_TYPE_OTHER = "02"; 
    
    
    /***************** RPS 迁移数据是否退渠道手续费0-不退，1-退 *****************/
    /** 0-不退  */
    public static final String CHNL_FEE_FLAG_NO = "0";     
    /** 1-退 */
    public static final String CHNL_FEE_FLAG_YES = "1"; 
    
    /***************** 商户员工角色常量 *****************/
    /** 01 - 负责人  */
    public static final String STAFF_ROLE_ONCHARGE = "01";  
    
    /** 02 - 联系人  */
    public static final String STAFF_ROLE_CONTACT = "02";

    /** 02 - 联系人  */
    public static final String STAFF_ROLE_MANAGER = "03";

    /***************** 记账记录重跑标志常量 *****************/
    /** 00 - 支持重跑  */
    public static final String RERUN_FLAG_YES = "00";  
    /** 01 - 不支持重跑  */
    public static final String RERUN_FLAG_NO = "01";
    
    /***************** 增值税常量 *****************/
    /** 0 - 人民币 */
    public static final String VAT_CUR_CNY = "0";  
    
    /** 借贷标志：借0 贷 1 默认：贷方1  */
    public static final String VAT_DR_CR_FLAG_0 = "0";
    /** 借贷标志：借0 贷 1 默认：贷方1  */
    public static final String VAT_DR_CR_FLAG_1 = "1";
    
    /** 境内外标识:0-境内;1-境外;默认0  */
    public static final String VAT_OVERSEAS_FLAG_0 = "0";
    /** 境内外标识:0-境内;1-境外;默认0  */
    public static final String VAT_OVERSEAS_FLAG_1 = "1";
    
    /** 收单渠道编号  */
    public static final String VAT_CHNL_NO = "052";
    
    /***************** 记账记录重跑标志常量 *****************/
    /** pro1 - 线上  */
    public static final String PRO_ID_ONLINE = "pro1";  
    /** pro2 - 扫码  */
    public static final String PRO_ID_SM = "pro2";

    /***************** 结算账号修改表处理状态 *****************/
    /** 0-未处理  */
    public static final String ACCT_SYN_STATUS_UNDO = "0";
    /** 1-已处理  */
    public static final String ACCT_SYN_STATUS_DONE = "1";

    /***************** 结算账号已销户，睡眠户等核心返回码 *****************/
    /** 2002 - 结算账号已销户，睡眠户等核心返回码   */
    public static final String CORE_RESP_CODE_ACCT_STATE_INVALID = "2002";

    /***************** 商户性质（普通商户 or 小微商户） *****************/
    /** 00- 小微商户*/
    public static final String MCHT_NAT_TINY = "00";
    /** 01- 普通商户*/
    public static final String MCHT_NAT_NORMAL = "01";

    /***************** 员工状态 *****************/
    /** 00-正常*/
    public static final String STAFF_STATE_NORMAL = "00";

    /***************** 批量执行状态 *****************/
    /** E-执行中*/
    public static final String JOB_EXE_STATUS_EXECUTING = "E";
    /** S-执行成功*/
    public static final String JOB_EXE_STATUS_SUCCESS = "S";
    /** F-执行失败*/
    public static final String JOB_EXE_STATUS_FAIL = "F";
    /** T-超时*/
    public static final String JOB_EXE_STATUS_TIMEOUT = "T";

    /***************** 批量执行结果描述 *****************/
    /** T-超时*/
    public static final String JOB_EXE_DESC_TIMEOUT = "任务执行成功，但执行时间超时,需确认成功！";


    /***************** 短信模板文件模板名称*****************/
    /** 2801 smsSendAccStsInvalid 结算账户异常导致结算失败*/
    public static final String MSG_TEMPLDATE_FILE_NAME_NORMAL = "smsSendNormal";
    public static final String MSG_TEMPLDATE_CODE_NORMAL = "2801";

    /** 2804 smsSendAccStsInvalid 结算账户异常导致结算失败*/
    public static final String MSG_TEMPLDATE_FILE_NAME_ACCSTSINVALID = "smsSendAccStsInvalid";
    public static final String MSG_TEMPLDATE_CODE_ACCSTSINVALID = "2804";

    /** 2806 保证金不足*/
    public static final String GUARANTEE_DEPOSIT_FILE_NAME = "smsSendGuaranteeDepositInvalid";
    public static final String GUARANTEE_DEPOSIT = "2806";

    /***************** 批量REDIS KEY值前缀 *****************/
    /** E-执行中*/
    public static final String REDIS_KEY_BATCH_SENDMSG = "REDIS-BATCH-SENDMSG-";

    /***************** 差错交易类型 *****************/
    public static final String ERR_PAY = "10";
    public static final String ERR_REFUND = "12";
    public static final String FILE_IN_FLAG = "01";
    /** 微信对账文件中的交易日期格式 , 例如: 2018-10-10 15:05:35 */
    public static final String wxBillTxnTmFormat = "yyyy-MM-dd HH:mm:ss";
    /** 日期格式 */
    public static final String dateFormat = "yyyy-MM-dd";

    /** 支付宝对账文件中的交易日期格式 , 例如: 2018-10-10 15:05:35 */
    public static final String aliBillTxnTmFormat = "yyyy-MM-dd HH:mm:ss";

    /** zk锁key **/
    public static final String lockKey = "batch_timedKeepAccountTask";
    public static final String asynRevKeepAccKey = "asynRevKeepAccKey";
    public static final String rootPath = "/keepLock/";

    /***************** 订单记账状态 *****************/
    /** 0-未记账 **/
    public static final String KEEP_ACCT_FLAG_0 = "0";

    /** 1-记账成功 **/
    public static final String KEEP_ACCT_FLAG_1 = "1";

    /** 2-重试记账成功 **/
    public static final String KEEP_ACCT_FLAG_2 = "2";

    /** 3-记账失败 **/
    public static final String KEEP_ACCT_FLAG_3 = "3";

    /***************** 同步记账/记账查询返回标志 *****************/
    /** 1-TRUE **/
    public static final String TRUE_FLAG = "1";
    /** 0-FALSE **/
    public static final String FALSE_FLAG = "0";
    /**********************是否实时结算****************/
    /**实时**/
    public static final String isRealTmFlag = "00";
    /**非实时**/
    public static final String noRealTmFlag = "01";
    /**********************他行受理状态****************/
    /**未受理**/
    public static final String OTHER_SEL_TYPE_IN= "0";
    /**已受理**/
    public static final String OTHER_SEL_TYPE_IN_SUCC = "1";
    /**已结算**/
    public static final String OTHER_SEL_TYPE_IN_ACC_SUCC = "2";
    /**受理失败**/
    public static final String OTHER_SEL_TYPE_IN_FLAG = "3";
    /**受理成功结算失败**/
    public static final String OTHER_SEL_TYPE_IN_ACC_FLAG = "4";



}
