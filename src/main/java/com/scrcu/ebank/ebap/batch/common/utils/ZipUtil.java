package com.scrcu.ebank.ebap.batch.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;

/**
 * Zip文件处理工具类
 * @author 
 *
 */
@Slf4j
public class ZipUtil {
	
	 /**
	  * 
	  * @param zipFile 目标文件
	  * @param descDir 指定解压目录
	  * @param urlList 存放解压后的文件目录（可选）
	  * @return
	  */
	public static boolean unZip(File zipFile, String descDir,  List<String> urlList) {
	    boolean flag = false;
	    File pathFile = new File(descDir);
	    if(!pathFile.exists()){
	        pathFile.mkdirs();
	    }
	    ZipFile zip = null;
		OutputStream out = null;
		InputStream in = null;
		try {
	        //指定编码，否则压缩包里面不能有中文目录
	        zip = new ZipFile(zipFile, Charset.forName("gbk"));
	        for(Enumeration entries = zip.entries(); entries.hasMoreElements();){
	            ZipEntry entry = (ZipEntry)entries.nextElement();
	            String zipEntryName = entry.getName();
	            in = zip.getInputStream(entry);
	            String outPath = (descDir+zipEntryName).replace("/", File.separator);
	            //判断路径是否存在,不存在则创建文件路径
	            File file = new File(outPath.substring(0, outPath.lastIndexOf(File.separator)));
	            if(!file.exists()){
	                file.mkdirs();
	            }
	            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
	            if(new File(outPath).isDirectory()){
	                continue;
	            }
	            //保存文件路径信息
	            urlList.add(outPath);

	            out = new FileOutputStream(outPath);
	            byte[] buf1 = new byte[2048];
	            int len;
	            while((len=in.read(buf1))>0){
	                out.write(buf1,0,len);
	            }
	            in.close();
	            out.close();
	        }
	        flag = true;
	        //必须关闭，否则无法删除该zip文件
	        zip.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }finally{
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					log.error("关闭[OutputStream]异常了，异常信息:",e);
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.error("关闭[InputStream]异常了，异常信息:",e);
				}
			}
			if(zip != null ){
				try {
					zip.close();
				} catch (IOException e) {
					log.error("关闭[ZipFile]异常了，异常信息:",e);
				}

			}
		}
	    return flag;
	}
	
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		String zipPath = "C:/Users/Administrator/Desktop/111.csv.zip";
//		File zipFile = new File(zipPath );
//		String descDir = "C:/Users/Administrator/Desktop/";
//		List<String> arrayList = new ArrayList<String>();
//		boolean unZip = unZip(zipFile,descDir,arrayList);
//	}

}
