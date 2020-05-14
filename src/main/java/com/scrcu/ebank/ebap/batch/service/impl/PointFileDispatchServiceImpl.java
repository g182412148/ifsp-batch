package com.scrcu.ebank.ebap.batch.service.impl;

import java.io.File;

import com.ruim.ifsp.utils.message.IfspBase64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.common.utils.EbapFileFilter;
import com.scrcu.ebank.ebap.batch.common.utils.EncryptUtil;
import com.scrcu.ebank.ebap.batch.common.utils.FtpUtil;
import com.scrcu.ebank.ebap.batch.common.utils.SftpUtil;
import com.scrcu.ebank.ebap.batch.service.PointFileDispatchService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PointFileDispatchServiceImpl implements PointFileDispatchService 
{
	@Value("${pointFilePath}")
    private String pointFilePath;              //积分文件路径
	
	@Value("${dmzFtpHost}")
    private String dmzFtpHost;
    
    @Value("${dmzFtpPort}")
    private String dmzFtpPort;
    
    @Value("${dmzFtpUserName}")
    private String dmzFtpUserName;
    
    @Value("${dmzFtpPwd}")
    private String dmzFtpPassword;
    
    @Value("${dmzFtpPath}")
    private String dmzFtpPath;
    
    @Value("${jsFilePath}")
    private String jsFilePath;
	
	@Override
	public CommonResponse pointFileDispatch() 
	{
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>pointFileDispatch service executing...");
		String pattern = "^[0-9]+"+"_"+"[0-9]+"+"pointfile\\.txt\\.ok";
		
		log.info(">>>>>>>>>>>>>>>>>>{pattern:"+pattern+"}");
		EbapFileFilter fileFilter = new EbapFileFilter(pattern);
		
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>{pointFilePath="+this.pointFilePath+"}");
		
		
		String SFTP_PWD = EncryptUtil.getInstance().DESdecode("DC6D1D56A13071A102B402EF8CCFC5BD7B97ADD9A885173A", "ebap_UnionMcht");
		
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>SFTP_PWD : " + SFTP_PWD);
		
		
		File parent = new File(pointFilePath);
		
		for(File f : parent.listFiles())
		{
			boolean result = fileFilter.accept(f);
			
			if(result)
			{
				String okFileName = f.getAbsolutePath();
				String pointFileName = okFileName.substring(0, okFileName.lastIndexOf(".ok"));
				
				File okFile = new File(okFileName);
				File pointFile = new File(pointFileName);
				
				String shortName = pointFile.getName();
				String shortOkName = okFile.getName();
				String merNo = shortName.substring(0, shortName.indexOf("_"));
				
				log.info(">>>>>>>>>>>>>>>>>>获取商户 : "+merNo+"积分文件 {fileName="+shortName+"}");
				
				if(pointFile.exists())
				{
					try
					{
						//1)将文件ftp到dmz区
						String dmzJsFilePath =  this.dmzFtpPath.replace("merId", merNo)  + this.jsFilePath;
						/*SftpUtil.ftpUploadFile(dmzFtpHost, Integer.parseInt(dmzFtpPort), dmzFtpUserName, dmzFtpPassword, 
								shortName, dmzJsFilePath, pointFileName,pointFilePath);
						SftpUtil.ftpUploadFile(dmzFtpHost, Integer.parseInt(dmzFtpPort), dmzFtpUserName, dmzFtpPassword, 
								shortOkName, dmzJsFilePath, okFileName,pointFilePath);*/

						
						//上传积分文件
						FtpUtil.uploadFile(dmzFtpHost, Integer.parseInt(dmzFtpPort), dmzFtpUserName, dmzFtpPassword, "", dmzJsFilePath+shortName, pointFileName);
						//上传积分.ok文件
						FtpUtil.uploadFile(dmzFtpHost, Integer.parseInt(dmzFtpPort), dmzFtpUserName, dmzFtpPassword, "", dmzJsFilePath+shortOkName, okFileName);
						
						
						//2)备份文件
						File backupPath = new File(this.pointFilePath+File.separator+"backup/");
						if(!backupPath.exists())
						{
							backupPath.mkdirs();
						}
						
						File resultFile = new File(backupPath+shortName);
						File okResultFile = new File(backupPath+shortOkName);
						
						pointFile.renameTo(resultFile);
						okFile.renameTo(okResultFile);
						
					}
					catch(Exception e)
					{
						log.error(">>>>>>>>>>>>>>>>>>>>>ftp upload settle file to dmz zone failed!<<<<<<<<<<<<<<<<<<<<<<<<");
						e.printStackTrace();
					}
					
				}
				
			}
		}
		
		//应答
		CommonResponse commonResponse = new CommonResponse();
		
		commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
		commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
				
		return commonResponse;
	}

}
