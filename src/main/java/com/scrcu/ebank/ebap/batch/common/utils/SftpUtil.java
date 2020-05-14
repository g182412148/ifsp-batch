/*
 * Copyright (C), 2015-2016, 上海睿民互联网科技有限公司
 * Package com.ruim.ifsp.batch.common 
 * FileName: SftpUtil.java
 * Author:   Anhui
 * Date:     2016年10月10日 下午3:36:30
 * Description: //模块目的、功能描述      
 * History: //修改记录
 *===============================================================================================
 *   author：          time：                             version：           desc：
 *   Anhui           2016年10月10日下午3:36:30                     1.0                  
 *===============================================================================================
 */
package com.scrcu.ebank.ebap.batch.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspBizException;

import com.scrcu.ebank.ebap.exception.IfspSystemException;
import lombok.extern.slf4j.Slf4j;

/**
 * 名称：〈一句话功能简述〉<br> 
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2016年10月10日 <br>
 * 作者：Anhui <br>
 * 说明：<br>
 */
@Slf4j
public class SftpUtil {
    public String host;//sftp服务器ip
    public String username;//用户名
    public String password;//密码
    protected String privateKey;//密钥文件路径
    protected String passphrase;//密钥口令
    public int port = 22;//默认的sftp端口号是22
    
    
    
    
    /**
     * @param host
     * @param username
     * @param password
     * @param port
     */
    public SftpUtil(String host, String username, String password, int port) {
        super();
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 获取连接
     * @return channel
     */
    public ChannelSftp connectSFTP() {
        JSch jsch = new JSch();
        Channel channel = null;
        try {
            if (privateKey != null && !"".equals(privateKey)) {
                //使用密钥验证方式，密钥可以使有口令的密钥，也可以是没有口令的密钥
                if (passphrase != null && "".equals(passphrase)) {
                    jsch.addIdentity(privateKey, passphrase);
                } else {
                    jsch.addIdentity(privateKey);
                }
            }
            Session session = jsch.getSession(username, host, port);
            if (password != null && !"".equals(password)) {
                session.setPassword(password);
            }
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");// do not verify host key
            session.setConfig(sshConfig);
            // session.setTimeout(timeout);
            session.setServerAliveInterval(192000);
            session.connect();
            //参数sftp指明要打开的连接是sftp连接
            channel = session.openChannel("sftp");
            channel.connect();
        } catch (JSchException e) {
            log.error("sftp连接异常",e);
        }
        return (ChannelSftp) channel;
    }

    /**
     * 获取连接 : 同步建立连接timeout = 0
     * @return channel
     */
    public ChannelSftp connectSFTPWithNoTimeOut() {
        JSch jsch = new JSch();
        Channel channel = null;
        try {
            if (privateKey != null && !"".equals(privateKey)) {
                //使用密钥验证方式，密钥可以使有口令的密钥，也可以是没有口令的密钥
                if (passphrase != null && "".equals(passphrase)) {
                    jsch.addIdentity(privateKey, passphrase);
                } else {
                    jsch.addIdentity(privateKey);
                }
            }
            Session session = jsch.getSession(username, host, port);
            if (password != null && !"".equals(password)) {
                session.setPassword(password);
            }
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");// do not verify host key
            session.setConfig(sshConfig);
            // session.setTimeout(timeout);
            //session.setServerAliveInterval(92000);
            session.connect();
            //参数sftp指明要打开的连接是sftp连接
            channel = session.openChannel("sftp");
            channel.connect();
        } catch (JSchException e) {
            log.error("sftp连接异常",e);
        }
        return (ChannelSftp) channel;
    }
    
    /**
     * 上传文件
     * 
     * @param directory
     *            上传的目录
     * @param uploadFile
     *            要上传的文件
     * @param sftp
     */
    public void upload(String directory, String lcdDir, String uploadFile, ChannelSftp sftp) {
        FileInputStream fis = null;
        try {
            sftp.cd(directory);
            sftp.lcd(lcdDir);
            File file = new File(uploadFile);
            fis = new FileInputStream(file);
            sftp.put(fis, file.getName());
        } catch (Exception e) {
            log.error("sftp连接异常",e);
        }finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error("关闭[FileInputStream]异常了，异常信息:",e);
                }
            }
        }
    }
    /**
     * 上传文件
     *
     * @param directory
     *            上传的目录
     * @param uploadFile
     *            要上传的文件
     * @param sftp
     */
    public void ftpUpload(String directory, String lcdDir, String uploadFile, ChannelSftp sftp) throws Exception{
        FileInputStream fileInputStream = null;
        try {
            sftp.cd(directory);
            sftp.lcd(lcdDir);
            File file = new File(uploadFile);
             fileInputStream = new FileInputStream(file);
            sftp.put(fileInputStream, file.getName());
            fileInputStream.close();
        } catch (Exception e) {
            log.error("sftp连接异常",e);
            throw e;
        }finally {
            if(fileInputStream!=null){
                //不能关闭流，抛出异常
              fileInputStream.close();
            }
        }
    }
    /**
     * 下载文件
     * 
     * @param srcDir
     *            下载目录
     * @param downloadFile
     *            下载的文件
     * @param saveFile
     *            存在本地的路径
     * @param sftp
     */
    public void download(String srcDir, String downloadFile,
            String destDir, String saveFile, ChannelSftp sftp) {
        try {
            sftp.cd(srcDir);
            sftp.lcd(destDir);
            sftp.get(downloadFile,saveFile);
        } catch (Exception e) {
            log.error("SFTP下载异常",e);
            throw new IfspSystemException(SystemConfig.getSysErrorCode(), "SFTP下载异常");
        }
    }

    /**
     * 删除文件
     * 
     * @param directory
     *            要删除文件所在目录
     * @param deleteFile
     *            要删除的文件
     * @param sftp
     */
    public void delete(String directory, String deleteFile, ChannelSftp sftp) {
        try {
            sftp.cd(directory);
            sftp.rm(deleteFile);
        } catch (Exception e) {
            log.error("sftp连接异常",e);
        }
    }
    
    public void disconnected(ChannelSftp sftp){
        if (sftp != null) {
            try {
                sftp.getSession().disconnect();
            } catch (JSchException e) {
                log.error("sftp连接异常",e);
            }
            sftp.disconnect();
        }
    }
    
    /**
     *  判断远程目录文件是否存在
     * @param sftp
     * @param sftpFilePath
     * @return
     */
    public boolean isFileExist(ChannelSftp sftp,String sftpFilePath){
        boolean isExistFlag = false;
        if(getFileSize(sftp,sftpFilePath) >= 0){
            isExistFlag = true;
        }
       return  isExistFlag;
    }
    public long getFileSize(ChannelSftp sftp,String sftpFilePath){
        long fileSize = 0;
        try{
            SftpATTRS sftpATTPS = sftp.lstat(sftpFilePath);
            fileSize = sftpATTPS.getSize();
        }catch(Exception e){
            fileSize = -1; 
        }
       
        return  fileSize;
    }
    
    /**
	 * 
	 * @param host
	 *            文件服务器ip
	 * @param port
	 *            文件服务器端口
	 * @param userName
	 *            文件服务器用户名
	 * @param password
	 *            文件服务器密码
	 * @param remoteFileName
	 *            文件服务器文件名
	 * @param rootDir
	 *            文件服务器路径
	 * @param localFileName
	 *            接受文件完整路径
	 */
	public static int ftpDownloadFile(String host, int port, String userName,
			String password, String remoteFileName, String rootDir,
			String localFileName,String localDir) {
		SftpUtil sftpUtil = new SftpUtil(host, userName, password, port);
        ChannelSftp sftp = sftpUtil.connectSFTP();

        try
        {
        	 log.info("---------------------SFTP上传文件------------------");
        	 sftpUtil.download(rootDir, remoteFileName, localDir, localFileName, sftp);
             log.info("---------------------SFTP断开连接------------------");
             sftpUtil.disconnected(sftp);
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
		
		log.info(">>>>>>>>>>>>>>>>>>>>上传成功：{fileName="+localFileName+"}");
		
		return 0;
	}
	
	
	public static boolean ftpUploadFile(String host, int port, String userName,
			String password, String remoteFileName, String remoteDir,
			String localFileName,String localDir)
	{
		SftpUtil sftpUtil = new SftpUtil(host, userName, password, port);
        ChannelSftp sftp = sftpUtil.connectSFTP();

        try
        {
        	log.info("---------------------SFTP上传文件------------------");
        	 sftpUtil.upload(remoteDir, localDir, localFileName, sftp);
             log.info("---------------------SFTP断开连接------------------");
             sftpUtil.disconnected(sftp);
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
		
		log.info(">>>>>>>>>>>>>>>>>>>>上传成功：{fileName="+localFileName+"}");
		
		
		return true;

	}
	
//   public static void main(String args[]){
       
//       SftpUtil sftpUtil = new SftpUtil("10.193.78.48", "file", "file", 22);
//       ChannelSftp sftp = sftpUtil.connectSFTP();
//       System.out.println("xxx");
//       if(sftpUtil.isFileExist(sftp, "/home/file/fcbp_vchr_201608310660000018.txt")){
//          System.out.println("文件存在");
//       }else{
//           System.out.println("文件不存在");
//       }
//       sftpUtil.disconnected(sftp);
//       String ss = "20161111|0000000001|S|20160901||成功|";
//       String[] dd = ss.split("\\|");
//       System.out.println(dd.length);
//       System.out.println(dd[0]);
//       System.out.println(dd[1]);
//       System.out.println(dd[2]);
//       System.out.println(dd[3]);
//       System.out.println(dd[4]);
//       System.out.println(dd[5]);
////       System.out.println(dd[6]);
     
//   }

}
