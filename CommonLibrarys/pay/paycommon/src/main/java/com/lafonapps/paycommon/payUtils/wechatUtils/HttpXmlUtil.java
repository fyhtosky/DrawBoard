package com.lafonapps.paycommon.payUtils.wechatUtils;

/**
 * Created by leishangming on 2018/7/19.
 */


import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpXmlUtil {

    public static String sendXmlMsg(String address, String xmlMsg) {
        String method = "POST";
        String charset = "UTF-8";
        String contentType = "text/xml";
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Content-Type", contentType);
            conn.setConnectTimeout(10*1000);
            conn.setReadTimeout(10*1000);
            conn.connect();
            // 向输出流写出数据
            PrintWriter pw = new PrintWriter(conn.getOutputStream());
            pw.write(xmlMsg);
            pw.flush();
            pw.close();
            String contentType1 = conn.getContentType();
            Pattern pattern = Pattern.compile("charset=.*:?");
            Matcher matcher = pattern.matcher(contentType1);
            if (matcher.find()) {
                charset = matcher.group();
                if (charset.endsWith(";")) {
                    charset = charset.substring(charset.indexOf("=") + 1, charset.indexOf(";"));
                } else {
                    charset = charset.substring(charset.indexOf("=") + 1);
                }
                if (charset.contains("\"")) {
                    charset = charset.substring(1, charset.length() - 1);
                }
                charset = charset.trim();
            }

            InputStream inStream = conn.getInputStream();
            BufferedReader ufferedReader = new BufferedReader(new InputStreamReader(inStream, charset));
            String line;
            while ((line = ufferedReader.readLine()) != null) {
                sb.append(line);
            }
            ufferedReader.close();
            conn.disconnect();
        }catch (Exception e) {
            Log.e("wx", "error:" + e);
        }
        return sb.toString();
    }
}

