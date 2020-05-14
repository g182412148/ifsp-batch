import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

/**
 *
 */
public class WxBillRecord {

    @CsvBindByName(column = "交易时间" , locale = "0")
    @CsvBindByPosition( position = 0)
    private String txnTime;
    @CsvBindByName(column = "公众账号ID", locale = "1")
    @CsvBindByPosition( position = 1)
    private String publicId;
    @CsvBindByName(column = "商户号", locale = "2")
    @CsvBindByPosition( position = 2)
    private String mchtId;
    @CsvBindByName(column = "特约商户号", locale = "3")
    @CsvBindByPosition( position = 3)
    private String blankMchtId;
    @CsvBindByName(column = "设备号", locale = "4")
    @CsvBindByPosition( position = 4)
    private String deviceId;
    @CsvBindByName(column = "银联订单号", locale = "5")
    @CsvBindByPosition( position = 5)
    private String ylOrderId;
    @CsvBindByName(column = "商户订单号", locale = "6")
    @CsvBindByPosition( position = 6)
    private String mchtOrderId;
    @CsvBindByName(column = "用户标识", locale = "7")
    @CsvBindByPosition( position = 7)
    private String userId;
    @CsvBindByName(column = "交易类型", locale = "8")
    @CsvBindByPosition( position = 8)
    private String txnType;
    @CsvBindByName(column = "交易状态", locale = "9")
    @CsvBindByPosition( position = 9)
    private String txnState;
    @CsvBindByName(column = "付款银行", locale = "10")
    @CsvBindByPosition( position = 10)
    private String payBank;
    @CsvBindByName(column = "货币种类", locale = "11")
    @CsvBindByPosition( position = 11)
    private String cny;
    @CsvBindByName(column = "应结订单金额", locale = "12")
    @CsvBindByPosition( position = 12)
    private String settleAmt;
    @CsvBindByName(column = "代金券金额", locale = "13")
    @CsvBindByPosition( position = 13)
    private String djAmt;
    @CsvBindByName(column = "银联退款单号", locale = "14")
    @CsvBindByPosition( position = 14)
    private String ylRefundOrderId;
    @CsvBindByName(column = "商户退款单号", locale = "15")
    @CsvBindByPosition( position = 15)
    private String mchtRefundOrderId;
    @CsvBindByName(column = "退款金额", locale = "16")
    @CsvBindByPosition( position = 16)
    private String refundAmt;
    @CsvBindByName(column = "充值券退款金额", locale = "17")
    @CsvBindByPosition( position = 17)
    private String czjRefundAmt;
    @CsvBindByName(column = "退款类型", locale = "18")
    @CsvBindByPosition( position = 18)
    private String refundType;
    @CsvBindByName(column = "退款状态", locale = "19")
    @CsvBindByPosition( position = 19)
    private String refundState;
    @CsvBindByName(column = "商品名称", locale = "20")
    @CsvBindByPosition( position = 20)
    private String goodsName;
    @CsvBindByName(column = "商户数据包", locale = "21")
    @CsvBindByPosition( position = 21)
    private String mchtData;
    @CsvBindByName(column = "手续费", locale = "22")
    @CsvBindByPosition( position = 22)
    private String rateAmt;
    @CsvBindByName(column = "费率", locale = "23")
    @CsvBindByPosition( position = 23)
    private String rate;
    @CsvBindByName(column = "订单金额", locale = "24")
    @CsvBindByPosition( position = 24)
    private String txnAmt;
    @CsvBindByName(column = "申请退款金额", locale = "25")
    @CsvBindByPosition( position = 25)
    private String applyRefundAmt;
    @CsvBindByName(column = "费率备注", locale = "26")
    @CsvBindByPosition( position = 26)
    private String rateDesc;


    public String getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(String txnTime) {
        this.txnTime = txnTime;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getMchtId() {
        return mchtId;
    }

    public void setMchtId(String mchtId) {
        this.mchtId = mchtId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getTxnState() {
        return txnState;
    }

    public void setTxnState(String txnState) {
        this.txnState = txnState;
    }

    public String getPayBank() {
        return payBank;
    }

    public void setPayBank(String payBank) {
        this.payBank = payBank;
    }

    public String getCny() {
        return cny;
    }

    public void setCny(String cny) {
        this.cny = cny;
    }

    public String getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(String settleAmt) {
        this.settleAmt = settleAmt;
    }

    public String getDjAmt() {
        return djAmt;
    }

    public void setDjAmt(String djAmt) {
        this.djAmt = djAmt;
    }

    public String getYlRefundOrderId() {
        return ylRefundOrderId;
    }

    public void setYlRefundOrderId(String ylRefundOrderId) {
        this.ylRefundOrderId = ylRefundOrderId;
    }

    public String getMchtRefundOrderId() {
        return mchtRefundOrderId;
    }

    public void setMchtRefundOrderId(String mchtRefundOrderId) {
        this.mchtRefundOrderId = mchtRefundOrderId;
    }

    public String getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(String refundAmt) {
        this.refundAmt = refundAmt;
    }

    public String getCzjRefundAmt() {
        return czjRefundAmt;
    }

    public void setCzjRefundAmt(String czjRefundAmt) {
        this.czjRefundAmt = czjRefundAmt;
    }

    public String getRefundType() {
        return refundType;
    }

    public void setRefundType(String refundType) {
        this.refundType = refundType;
    }

    public String getRefundState() {
        return refundState;
    }

    public void setRefundState(String refundState) {
        this.refundState = refundState;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getMchtData() {
        return mchtData;
    }

    public void setMchtData(String mchtData) {
        this.mchtData = mchtData;
    }

    public String getRateAmt() {
        return rateAmt;
    }

    public void setRateAmt(String rateAmt) {
        this.rateAmt = rateAmt;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getApplyRefundAmt() {
        return applyRefundAmt;
    }

    public void setApplyRefundAmt(String applyRefundAmt) {
        this.applyRefundAmt = applyRefundAmt;
    }

    public String getRateDesc() {
        return rateDesc;
    }

    public void setRateDesc(String rateDesc) {
        this.rateDesc = rateDesc;
    }



    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getYlOrderId() {
        return ylOrderId;
    }

    public void setYlOrderId(String ylOrderId) {
        this.ylOrderId = ylOrderId;
    }

    public String getMchtOrderId() {
        return mchtOrderId;
    }

    public void setMchtOrderId(String mchtOrderId) {
        this.mchtOrderId = mchtOrderId;
    }

    public String getBlankMchtId() {
        return blankMchtId;
    }

    public void setBlankMchtId(String blankMchtId) {
        this.blankMchtId = blankMchtId;
    }
}
