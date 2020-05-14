package util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.ruim.ifsp.utils.message.IfspBase64;
import lombok.extern.slf4j.Slf4j;

/**
 * 名称：〈HTTP测试类〉<br>
 * 功能：〈功能详细描述〉<br>
 * 版本：1.0 <br>
 * 日期：2016年4月30日 <br>
 * 作者：yema <br>
 * 说明：<br>
 */
@Slf4j
public class HttpClientUtils {

    public static String send(Object reqBean, String txnCode, String urlstr, boolean base64Flag) {
        // 报文转换
        String message = "";
        Gson gson = new Gson();
        message = gson.toJson(reqBean);

        // Base64转码
        if (base64Flag) {
            message = IfspBase64.encode(message);
        }

        // 组装完整请求地址
        urlstr = urlstr + txnCode;
        OutputStream outputStream = null;
        BufferedOutputStream bos = null;
        PrintWriter conOut = null;
        InputStream is = null;
        try {

            for (int i = 0; i < 1; i++) {
                URL url = new URL(urlstr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.connect();

                outputStream = connection.getOutputStream();
                bos = new BufferedOutputStream(outputStream);
                conOut = new PrintWriter(bos);

                conOut.write(message);
                conOut.flush();
                conOut.close();

                Map<String, List<String>> map = connection.getHeaderFields();

                is = connection.getInputStream();
                String result = changeInputStreamToString(is, "UTF-8");
                System.out.println("ret:" + result);
                return result;
            }

        } catch (Exception e) {
            log.error("发送数据异常了，异常信息:",e);
        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("关闭[InputStream]异常了，异常信息:",e);
                }
            }
            if (conOut != null) {
                try {
                    conOut.close();
                } catch (Exception e) {
                    log.error("关闭[PrintWriter]异常了，异常信息:",e);
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    log.error("关闭[BufferedOutputStream]异常了，异常信息:",e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("关闭[OutputStream]异常了，异常信息:",e);
                }
            }
        }
        return "";
    }

    private static String changeInputStreamToString(InputStream is, String charsetName)
            throws UnsupportedEncodingException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, charsetName));

        StringBuffer sb = new StringBuffer();
        String str = null;

        while ((str = br.readLine()) != null) {
            sb.append(str);
        }
        br.close();

        return sb.toString();
    }
}
