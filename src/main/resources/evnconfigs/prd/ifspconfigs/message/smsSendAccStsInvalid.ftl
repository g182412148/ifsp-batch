<?xml version="1.0" encoding="gb2312"?>
<EMD>
    <HEAD>
        <PROTOCOL>C300</PROTOCOL>
        <TEMPCODE>${tempCode}</TEMPCODE><#--模板号-->
        <TRANSID></TRANSID>
        <TRANSCOMMAND></TRANSCOMMAND>
        <SRCBRANCH>${brNo!8396}</SRCBRANCH><#--付费机构号-->
        <SRCSYSTEM>${srcSystem!28}</SRCSYSTEM><#--渠道号-->
        <SRCDEVICE></SRCDEVICE>
        <TIMESTAMP>${timeStamp}</TIMESTAMP>
    </HEAD>
    <BODY>
        <ADDRESS>${phone}</ADDRESS>
        <SM_LEGAL>${legalNm}</SM_LEGAL>
        <SM_BNO>${mchtNm}</SM_BNO>
    </BODY>
</EMD>