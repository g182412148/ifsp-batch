package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.scrcu.ebank.ebap.batch.common.utils.ConstantUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test001 {

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
//		String zipPath = "C:/Users/Administrator/Desktop/111.csv.zip";
//		File zipFile = new File(zipPath );
//		String descDir = "C:/Users/Administrator/Desktop/";
//		List<String> arrayList = new ArrayList<String>();
//		boolean unZip = unZip(zipFile,descDir,arrayList);
		
		String s1="`2018-04-03 22:02:33";
		String s2="14526500    00049992    644576 1026093154 5200831111111113    ";
		String s3="0123450";
		String s4="1900008721All2018-04-03.csv.zip";
		String s5="20180403220233";
//		System.out.println(s4.substring(0, s4.length()-4));
		System.out.println(IfspDateTime.parseDate(s5, "yyyyMMddHHmmss"));
//		Date txnTm = IfspDateTime.strToDate(ConstantUtil.removeSplitSymbol(s1), "yyyyMMddHHmmss");
//		System.out.println(s3.replaceAll("^[0]+", "").equals("")?"0":s3.replaceAll("^[0]+", ""));
//		System.out.println(s3.substring(1,5));
//		System.out.println(s2.substring(0, 11));
//		System.out.println(IfspDateTime.getYYYYMMDDHHMMSS(IfspDateTime.getYYYYMMDDHHMMSS(ConstantUtil.removeSplitSymbol(s1))));
	}
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
}
