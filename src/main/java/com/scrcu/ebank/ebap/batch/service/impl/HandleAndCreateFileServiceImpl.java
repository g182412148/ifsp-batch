package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.constant.IfspConstants;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtExtInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtStaffInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.MerRegInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.PagyMchtInfo;
import com.scrcu.ebank.ebap.batch.common.dict.*;
import com.scrcu.ebank.ebap.batch.common.utils.MchtInfoTransUtil;
import com.scrcu.ebank.ebap.batch.dao.MchtBaseInfoDao;
import com.scrcu.ebank.ebap.batch.dao.MchtStaffInfoDao;
import com.scrcu.ebank.ebap.batch.dao.PagyMchtInfoDao;
import com.scrcu.ebank.ebap.batch.service.HandleAndCreateFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
@Slf4j
@Service
public class HandleAndCreateFileServiceImpl implements HandleAndCreateFileService {

    @Value("${AcqInsIdCd}")
    private String acqInsIdCd;
    /**
     * 通道商户信息表
     */
    @Resource
    private PagyMchtInfoDao pagyMchtInfoDao;
    /**
     * 商户信息表
     */
    @Resource
    private MchtBaseInfoDao mchtBaseInfoDao;
    @Resource
    private MchtStaffInfoDao mchtStaffInfoDao;
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
    public void handle(String localRegFilePath, List<PagyMchtInfo> pagyMchtInfos, String curDate) throws Exception{

        FileOutputStream fos = null;
        OutputStreamWriter osw= null;
        //生成商户同步记录数
        try {
            fos = new FileOutputStream(localRegFilePath, false);
            osw = new OutputStreamWriter(fos, IfspConstants.GBK_ENCODING);
            for (PagyMchtInfo pagyMchtInfo :pagyMchtInfos ) {
                //导入标志设置 I：表示“增加”；U：表示“修改”；D：表示“删除”，必须大写。
                String updateState = "";
                if(IfspDataVerifyUtil.equals(pagyMchtInfo.getUpMchtSynType(), UpMchtSynTypeDict.ADD.getCode())
                        ||IfspDataVerifyUtil.equals(pagyMchtInfo.getUpMchtSynType(),UpMchtSynTypeDict.MOD.getCode())
                        ||IfspDataVerifyUtil.equals(pagyMchtInfo.getUpMchtSynType(),UpMchtSynTypeDict.DEL.getCode())
                        ||IfspDataVerifyUtil.equals(pagyMchtInfo.getUpMchtSynType(),UpMchtSynTypeDict.ADD_DEL.getCode())
                ){
                    if(IfspDataVerifyUtil.equals(pagyMchtInfo.getUpMchtSynType(),UpMchtSynTypeDict.ADD_DEL.getCode())){
                        pagyMchtInfo.setUpMchtSynType(UpMchtSynTypeDict.ADD.getCode());
                    }
                    MerRegInfo merRegInfo = getMerRegInfo(pagyMchtInfo);
                    if(merRegInfo==null) continue;
                    log.info(merRegInfo.genRecordNew());
                    osw.write(merRegInfo.genRecordNew());
                    osw.flush();
                    //更新状态为同步中,并更新同步日期
                    if(IfspDataVerifyUtil.equals(pagyMchtInfo.getTableType(),"1")){
                        pagyMchtInfoDao.updateUpMchtSynStateOld(pagyMchtInfo.getPagyMchtNo(), UpMchtSynStateDict.SYN_IN.getCode(),curDate);
                    }else{
                        pagyMchtInfoDao.updateUpMchtSynState(pagyMchtInfo.getPagyMchtNo(), UpMchtSynStateDict.SYN_IN.getCode(),curDate);
                    }
                }else{
                    log.info("商户同步类型不正确，忽略，通道商户号[{}],商户号[{}],同步类型[{}]",pagyMchtInfo.getPagyMchtNo(),pagyMchtInfo.getTpamPagyMchtNo(),pagyMchtInfo.getUpMchtSynType());
                    continue;
                }
            }
            log.info("批量处理商户数[{}]，文件名称[{}]",pagyMchtInfos.size(),localRegFilePath);
            if (osw != null) {
                osw.close();
            }
            if (fos != null) {
                fos.close();
            }
        }catch(Exception e){
            e.printStackTrace();
            log.info("批量文件生成异常{}",e.getMessage());
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e1) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e2) {
                    e.printStackTrace();
                }
            }
            File file = new File(localRegFilePath);
            if(file.exists()){
                boolean result = file.delete();
                log.info("生成文件异常后删除文件[{}],删除结果[{}]",localRegFilePath,result);
            }
            throw e;
        }finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private MerRegInfo getMerRegInfo(PagyMchtInfo pagyMchtInfo) {
        MerRegInfo merRegInfo = new MerRegInfo();
        //I：表示“增加”；U：表示“修改”；D：表示“删除”，必须大写
        merRegInfo.setOpeFlag(pagyMchtInfo.getUpMchtSynType());
        /**商户中心查询商户信息*/
        MchtExtInfo mchtExtInfo = mchtBaseInfoDao.queryMchtExtInfoByMchtId(pagyMchtInfo.getChlMchtNo());
        //商户不存在
        if (mchtExtInfo == null) {
            log.error("商户中心查询商户信息不存在,mchtId{}", pagyMchtInfo.getChlMchtNo());
            return null;
        }

        //商户代码-已生成
        merRegInfo.setMchntCd(pagyMchtInfo.getTpamPagyMchtNo());
        //商户服务类型
        /**
         * 选填
         * POS收单
         * 00-传统POS商户
         * 17-多渠道直联终端商户
         * 08-电话终端
         * 多渠道
         * 01-服务提供机构
         * 02-接入渠道机构
         * 04-虚拟商户
         * 05-行业商户
         * 06-服务提供机构＋接入渠道机构
         * 互联网、全渠道
         * 03-互联网、全渠道普通商户
         * 13-互联网、全渠道多渠道商户
         * 移动支付
         * 11-移动支付平台
         * 语音支付
         * 18-语音支付平台商户
         * mPOS商户
         * 19-普通mPOS商户
         * 20-农资收购mPOS商户
         * 21-助农取款mPOS商户
         */
        //线上设置为03-互联网、全渠道普通商户，线上商户还不定
        /**
         * TODO 设置值
         */
        if (pagyMchtInfo.getTpamPagyMchtNo().startsWith(UpMchtTypePrefixDict.UPACP_MCHT_TYPE.getCode())) {
            merRegInfo.setMchntSvcTp(UpMchtTypePrefixDict.UPACP_MCHT_SVC_TP.getCode());
        }else{
            merRegInfo.setMchntSvcTp(UpMchtTypePrefixDict.UPQRC_MCHT_SVC_TP.getCode());
        }
        //商户中文名称
        merRegInfo.setMchntCnNm(mchtExtInfo.getMchtName());
        //商户中文简称
        merRegInfo.setMchntCnAbbr(mchtExtInfo.getMchtSimName());
        /**
         * TODO 设置值 收单机构代码
         */
        merRegInfo.setAcqInsIdCd(acqInsIdCd);

        //国家代码 cntry_cd 不填

        //受理地区代码
        /**
         * TODO 设置值 受理地区代码-直接查表
         */
        merRegInfo.setAcqRegionCd(mchtExtInfo.getPagyCtCode());

        //交易商户类型即MCC
        merRegInfo.setMchntTp(mchtExtInfo.getMccNo());
        //企业性质 etps_attr 不填

        //商户状态 取值：1：启用  2：冻结 暂时都设置为启用
        /**
         * TODO 根据商户状态设置值
         */
        merRegInfo.setMchntSt("1");
        //营业证明文件类型
        merRegInfo.setNetMchntSvcTp(MchtInfoTransUtil.tranNetMchntSvcTp(mchtExtInfo.getBlType()));
        //营业证明文件号码
        merRegInfo.setLicNo(mchtExtInfo.getBlNo());

        /*
         * 当为小微商户时，若要向银联进件，都是使用身份证作为营业执照上送
         * MchtNat:00-小微商户 01-普通商户
         */
        if(IfspDataVerifyUtil.equals("00",mchtExtInfo.getMchtNat())){
            merRegInfo.setNetMchntSvcTp("03");
            merRegInfo.setLicNo(mchtExtInfo.getOwnerCertNo());
        }

        //商户经营地址
        /**
         * TODO 地址没有加省市区
         */
        merRegInfo.setBussAddr(mchtExtInfo.getMchtAddr());
        //商户注册地址 根商户经营地址一样
        merRegInfo.setRegAddr(mchtExtInfo.getMchtAddr());
        //商户英文名称
        merRegInfo.setMchntEnNm(mchtExtInfo.getMchtEnName());

        /**
         * 查询联系人和法人信息
         */
        List<MchtStaffInfo> mchtStaffInfos = mchtStaffInfoDao.selectByMchtId(mchtExtInfo.getMchtId());
        for (MchtStaffInfo mchtStaffInfo : mchtStaffInfos) {
            //法人信息设置
            if (IfspDataVerifyUtil.equals(StaffRoleDict.MNG.getCode(), mchtStaffInfo.getStaffRole())) {

                //联系人信息
                //商户联系人	商户联系人	contact_person_nm
                merRegInfo.setContactPersionNm(mchtStaffInfo.getStaffName());
                //商户联系人电话	商户联系人电话	phone
                merRegInfo.setPhone(mchtStaffInfo.getStaffPhone());
                //商户联系人通讯地址
                merRegInfo.setCommAddr(mchtExtInfo.getMchtAddr());
                //移动电话	移动电话	mobile
                merRegInfo.setMobile(mchtStaffInfo.getStaffPhone());
            }
        }
            /**********法人信息begin***************/

            //法人代表姓名	法人代表姓名	artif_nm
            //  merRegInfo.setArtifNm(artifNm);
            //法定代表人证件类型	法定代表人证件类型	artif_certif_tp
            //银联类型：01-身份证
            //02-军官证1
            //03-护照
            //04-港澳居民来往内地通行证（回乡证）
            //05-台湾同胞来往内地通行证（台胞证）
            //06-警官证
            //07-士兵证
            //08-户口簿
            //09-临时身份证
            //10-外国人居留证
            //99-其他证件
            //农信内管01	身份证，02 户口簿 ，03 护照 (商户服务平台内管页面固定值，不是按数据字典取的值)
            String certType = mchtExtInfo.getOwnerCertType();
            if ("01".equals(certType)) {
                merRegInfo.setArtifCertifTp("01");
            } else if ("02".equals(certType)) {
                merRegInfo.setArtifCertifTp("08");
            } else if ("03".equals(certType)) {
                merRegInfo.setArtifCertifTp("03");
            } else {
                merRegInfo.setArtifCertifTp("99");
            }


            //法人代表证件号码	法人代表证件号码	artif_certif_id
            merRegInfo.setArtifCertifId(mchtExtInfo.getOwnerCertNo());
            merRegInfo.setArtifNm(mchtExtInfo.getOwnerName());
            /**********法人信息end***************/

            //商户拓展方式 1-自主拓展,-委托外包，填 1
            merRegInfo.setRecnclTp("1");
            //特殊计费类型	特殊计费类型	spec_disc_tp 取默认值00
            merRegInfo.setSpecDiscTp("00");
            //特殊计费档次	特殊计费档次	spec_disc_lvl 取默认值0
            merRegInfo.setSpecDiscLvl("0");


            /**
             * 限额度-全部设置
             * 普通商户的收款限额，单笔20万，日累计20万（邹浩确认）
             */
            merRegInfo.setSingleAtLimit("09");
            merRegInfo.setSingleAtLimitDesc("");  //只能填数字
            merRegInfo.setSingleCardDayAtLimit("12");
            merRegInfo.setSingleCardDayAtLimitDesc("");//只能填数字


            /**
             * 银联全渠道设置
             */
            if (pagyMchtInfo.getTpamPagyMchtNo().startsWith(UpMchtTypePrefixDict.UPACP_MCHT_TYPE.getCode())) {
                //商户网址 条件必填，互联网、全渠道商户必填（商户服务类型为03或13）
                merRegInfo.setSvcNetUrl(mchtExtInfo.getWebSiteAddr());
                //网站或APP名称
                merRegInfo.setMchntWebSiteNm(mchtExtInfo.getAppName());

                //业务类型
                merRegInfo.setBussTp("01");
                //产品功能套餐	产品功能套餐	prod_func
                merRegInfo.setProdFunc("0104");
                merRegInfo.setBussCont1Email(mchtExtInfo.getEmail());

                /**
                 *网络商户类型
                 * 条件必填，互联网、全渠道商户必填（商户服务类型为03或13）
                 1-平台型
                 2-普通型
                 */
                if (IfspDataVerifyUtil.equals(PlatFlagDict.PLAT.getCode(), mchtExtInfo.getPlatFlag())) {
                    merRegInfo.setSubmchntIn("1");
                } else {
                    merRegInfo.setSubmchntIn("2");
                }
                //ICP许可证编号
                merRegInfo.setSvcInsNm(mchtExtInfo.getIcpNo());
            }
        return merRegInfo;
    }
}
